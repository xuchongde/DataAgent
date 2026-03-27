/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.headless.server.utils;

import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.common.util.JsonUtil;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.api.pojo.enums.ChatWorkflowState;
import com.alibaba.cloud.ai.headless.api.pojo.request.SemanticQueryReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.ParseResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SemanticTranslateResp;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.corrector.LLMPhysicalSqlCorrector;
import com.alibaba.cloud.ai.headless.chat.corrector.SemanticCorrector;
import com.alibaba.cloud.ai.headless.chat.mapper.SchemaMapper;
import com.alibaba.cloud.ai.headless.chat.parser.SemanticParser;
import com.alibaba.cloud.ai.headless.chat.query.QueryManager;
import com.alibaba.cloud.ai.headless.chat.query.SemanticQuery;
import com.alibaba.cloud.ai.headless.server.facade.service.SemanticLayerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatWorkflowEngine {

    private final List<SchemaMapper> schemaMappers = CoreComponentFactory.getSchemaMappers();
    private final List<SemanticParser> semanticParsers = CoreComponentFactory.getSemanticParsers();
    private final List<SemanticCorrector> semanticCorrectors =
            CoreComponentFactory.getSemanticCorrectors();

    public void start(ChatWorkflowState initialState, ChatQueryContext queryCtx) {
        ParseResp parseResult = queryCtx.getParseResp();
        queryCtx.setChatWorkflowState(initialState);
        while (queryCtx.getChatWorkflowState() != ChatWorkflowState.FINISHED) {
            switch (queryCtx.getChatWorkflowState()) {
                case MAPPING:
                    performMapping(queryCtx);
                    if (queryCtx.getMapInfo().isEmpty()) {
                        parseResult.setState(ParseResp.ParseState.FAILED);
                        parseResult.setErrorMsg(
                                "No semantic entities can be mapped against user question.");
                        queryCtx.setChatWorkflowState(ChatWorkflowState.FINISHED);
                    } else {
                        queryCtx.setChatWorkflowState(ChatWorkflowState.PARSING);
                    }
                    break;
                case PARSING:
                    performParsing(queryCtx);
                    if (queryCtx.getCandidateQueries().isEmpty()) {
                        parseResult.setState(ParseResp.ParseState.FAILED);
                        parseResult.setErrorMsg("No semantic queries can be parsed out.");
                        queryCtx.setChatWorkflowState(ChatWorkflowState.FINISHED);
                    } else {
                        List<SemanticParseInfo> parseInfos = queryCtx.getCandidateQueries().stream()
                                .map(SemanticQuery::getParseInfo).collect(Collectors.toList());
                        parseResult.setSelectedParses(parseInfos);
                        if (queryCtx.needSQL()) {
                            queryCtx.setChatWorkflowState(ChatWorkflowState.S2SQL_CORRECTING);
                        } else {
                            parseResult.setState(ParseResp.ParseState.COMPLETED);
                            queryCtx.setChatWorkflowState(ChatWorkflowState.FINISHED);
                        }
                    }
                    break;
                case S2SQL_CORRECTING:
                    performCorrecting(queryCtx);
                    queryCtx.setChatWorkflowState(ChatWorkflowState.TRANSLATING);
                    break;
                case TRANSLATING:
                    long start = System.currentTimeMillis();
                    performTranslating(queryCtx, parseResult);
                    parseResult.getParseTimeCost().setSqlTime(System.currentTimeMillis() - start);
                    queryCtx.setChatWorkflowState(ChatWorkflowState.PHYSICAL_SQL_CORRECTING);
                    break;
                case PHYSICAL_SQL_CORRECTING:
                    performPhysicalSqlCorrecting(queryCtx);
                    queryCtx.setChatWorkflowState(ChatWorkflowState.FINISHED);
                    break;
                default:
                    if (parseResult.getState().equals(ParseResp.ParseState.PENDING)) {
                        parseResult.setState(ParseResp.ParseState.COMPLETED);
                    }
                    queryCtx.setChatWorkflowState(ChatWorkflowState.FINISHED);
                    break;
            }
        }
    }

    private void performMapping(ChatQueryContext queryCtx) {
        if (Objects.isNull(queryCtx.getMapInfo())
                || MapUtils.isEmpty(queryCtx.getMapInfo().getDataSetElementMatches())) {
            schemaMappers.forEach(mapper -> mapper.map(queryCtx));
        }
    }

    private void performParsing(ChatQueryContext queryCtx) {
        semanticParsers.forEach(parser -> {
            parser.parse(queryCtx);
            log.debug("{} result:{}", parser.getClass().getSimpleName(),
                    JsonUtil.toString(queryCtx));
        });
    }

    private void performCorrecting(ChatQueryContext queryCtx) {
        List<SemanticQuery> candidateQueries = queryCtx.getCandidateQueries();
        if (CollectionUtils.isNotEmpty(candidateQueries)) {
            for (SemanticQuery semanticQuery : candidateQueries) {
                for (SemanticCorrector corrector : semanticCorrectors) {
                    corrector.correct(queryCtx, semanticQuery.getParseInfo());
                    if (!ChatWorkflowState.S2SQL_CORRECTING
                            .equals(queryCtx.getChatWorkflowState())) {
                        break;
                    }
                }
            }
        }
    }

    private void performTranslating(ChatQueryContext queryCtx, ParseResp parseResult) {
        List<SemanticParseInfo> semanticParseInfos = queryCtx.getCandidateQueries().stream()
                .map(SemanticQuery::getParseInfo).collect(Collectors.toList());
        List<String> errorMsg = new ArrayList<>();
        if (StringUtils.isNotBlank(parseResult.getErrorMsg())) {
            errorMsg.add(parseResult.getErrorMsg());
        }
        semanticParseInfos.forEach(parseInfo -> {
            try {
                SemanticQuery semanticQuery = QueryManager.createQuery(parseInfo.getQueryMode());
                if (Objects.isNull(semanticQuery)) {
                    return;
                }
                semanticQuery.setParseInfo(parseInfo);
                SemanticQueryReq semanticQueryReq = semanticQuery.buildSemanticQueryReq();
                SemanticLayerService queryService =
                        ContextUtils.getBean(SemanticLayerService.class);
                SemanticTranslateResp explain =
                        queryService.translate(semanticQueryReq, queryCtx.getRequest().getUser());
                if (explain.isOk()) {
                    parseInfo.getSqlInfo().setQuerySQL(explain.getQuerySQL());
                    parseResult.setState(ParseResp.ParseState.COMPLETED);
                } else {
                    parseResult.setState(ParseResp.ParseState.FAILED);
                }
                if (StringUtils.isNotBlank(explain.getErrMsg())) {
                    errorMsg.add(explain.getErrMsg());
                }
                log.info(
                        "SqlInfoProcessor results:\n"
                                + "Parsed S2SQL: {}\nCorrected S2SQL: {}\nQuery SQL: {}",
                        StringUtils.normalizeSpace(parseInfo.getSqlInfo().getParsedS2SQL()),
                        StringUtils.normalizeSpace(parseInfo.getSqlInfo().getCorrectedS2SQL()),
                        StringUtils.normalizeSpace(parseInfo.getSqlInfo().getQuerySQL()));
            } catch (Exception e) {
                log.warn("get sql info failed:{}", e);
                errorMsg.add(String.format("S2SQL:%s %s", parseInfo.getSqlInfo().getParsedS2SQL(),
                        e.getMessage()));
            }
        });
        if (!errorMsg.isEmpty()) {
            parseResult.setErrorMsg(String.join("\n", errorMsg));
        }
    }

    private void performPhysicalSqlCorrecting(ChatQueryContext queryCtx) {
        List<SemanticQuery> candidateQueries = queryCtx.getCandidateQueries();
        if (CollectionUtils.isNotEmpty(candidateQueries)) {
            for (SemanticQuery semanticQuery : candidateQueries) {
                for (SemanticCorrector corrector : semanticCorrectors) {
                    if (corrector instanceof LLMPhysicalSqlCorrector) {
                        corrector.correct(queryCtx, semanticQuery.getParseInfo());
                        // 如果物理SQL被修正了，更新querySQL为修正后的版本
                        SemanticParseInfo parseInfo = semanticQuery.getParseInfo();
                        if (StringUtils.isNotBlank(parseInfo.getSqlInfo().getCorrectedQuerySQL())) {
                            parseInfo.getSqlInfo()
                                    .setQuerySQL(parseInfo.getSqlInfo().getCorrectedQuerySQL());
                            log.info("Physical SQL corrected and updated querySQL: {}",
                                    parseInfo.getSqlInfo().getQuerySQL());
                        }
                        break;
                    }
                }
            }
        }
    }
}
