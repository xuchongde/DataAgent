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
package com.alibaba.cloud.ai.headless.chat.parser.rule;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElementMatch;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaMapInfo;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.parser.SemanticParser;
import com.alibaba.cloud.ai.headless.chat.query.SemanticQuery;
import com.alibaba.cloud.ai.headless.chat.query.rule.RuleSemanticQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * RuleSqlParser resolves a specific SemanticQuery according to co-appearance of certain schema
 * element types.
 */
@Slf4j
public class RuleSqlParser implements SemanticParser {

    private static final List<SemanticParser> auxiliaryParsers =
            Arrays.asList(new TimeRangeParser(), new AggregateTypeParser());

    @Override
    public void parse(ChatQueryContext chatQueryContext) {
        if (!chatQueryContext.getCandidateQueries().isEmpty()) {
            return;
        }
        SchemaMapInfo mapInfo = chatQueryContext.getMapInfo();
        List<SemanticQuery> candidateQueries = Lists.newArrayList();
        // iterate all schemaElementMatches to resolve query mode
        for (Long dataSetId : mapInfo.getMatchedDataSetInfos()) {
            List<SchemaElementMatch> elementMatches = mapInfo.getMatchedElements(dataSetId);
            List<RuleSemanticQuery> queries =
                    RuleSemanticQuery.resolve(dataSetId, elementMatches, chatQueryContext);
            candidateQueries.addAll(queries);
        }
        chatQueryContext.setCandidateQueries(candidateQueries);

        auxiliaryParsers.forEach(p -> p.parse(chatQueryContext));

        candidateQueries.forEach(query -> query.buildS2Sql(
                chatQueryContext.getDataSetSchema(query.getParseInfo().getDataSetId())));
    }
}
