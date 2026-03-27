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
package com.alibaba.cloud.ai.headless.chat.parser.llm;

import com.alibaba.cloud.ai.dataagent.service.llm.LlmService;
import com.alibaba.cloud.ai.dataagent.util.JsonParseUtil;
import com.alibaba.cloud.ai.headless.chat.parser.ParserConfig;
import com.alibaba.cloud.ai.headless.chat.query.llm.s2sql.LLMReq;
import com.alibaba.cloud.ai.headless.chat.query.llm.s2sql.LLMResp;
import com.alibaba.cloud.ai.headless.common.pojo.ChatApp;
import com.alibaba.cloud.ai.headless.common.pojo.ChatModelConfig;
import com.alibaba.cloud.ai.headless.common.pojo.Text2SQLExemplar;
import com.alibaba.cloud.ai.headless.common.pojo.enums.AppModule;
import com.alibaba.cloud.ai.headless.common.util.ChatAppManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.cloud.ai.headless.chat.parser.ParserConfig.PARSER_FORMAT_JSON_TYPE;

@Service
@Slf4j
public class OnePassSCSqlGenStrategy extends SqlGenStrategy {

    private static final Logger keyPipelineLog = LoggerFactory.getLogger("keyPipeline");

    public static final String APP_KEY = "S2SQL_PARSER";
    private final LlmService llmService;
    private final JsonParseUtil jsonParseUtil;
    @Autowired
    private ParserConfig parserConfig;

    public static final String INSTRUCTION =
            "#Role: You are a data analyst experienced in SQL languages."
                    + "\n#Task: You will be provided with a natural language question asked by users,"
                    + "please convert it to a SQL query so that relevant data could be returned "
                    + "by executing the SQL query against underlying database." + "\n#Rules:"
                    + "\n1.SQL columns and values must be mentioned in the `Schema`, DO NOT hallucinate."
                    + "\n2.ALWAYS specify time range using `>`,`<`,`>=`,`<=` operator."
                    + "\n3.DO NOT include time range in the where clause if not explicitly expressed in the `Question`."
                    + "\n4.DO NOT calculate date range using functions."
                    + "\n5.ALWAYS use `with` statement if nested aggregation is needed."
                    + "\n6.ALWAYS enclose alias declared by `AS` command in underscores."
                    + "\n7.Alias created by `AS` command must be in the same language ast the `Question`."
                    + "\n#Exemplars: {{exemplar}}"
                    + "\n#Query: Question:{{question}},Schema:{{schema}},SideInfo:{{information}}";

    public OnePassSCSqlGenStrategy(LlmService llmService,JsonParseUtil jsonParseUtil) {
        this.llmService = llmService;
        this.jsonParseUtil = jsonParseUtil;
        ChatAppManager.register(APP_KEY, ChatApp.builder().prompt(INSTRUCTION).name("语义SQL解析")
                .appModule(AppModule.CHAT).description("通过大模型做语义解析生成S2SQL").enable(true).build());
    }

    @Data
    static class SemanticSql {
        @JsonProperty("thought")
        @JsonPropertyDescription("thought or remarks to tell users about the sql, make it short.")
        private String thought;

        @JsonProperty("sql")
        @JsonPropertyDescription("sql to generate")
        private String sql;
    }

    interface SemanticSqlExtractor {
        SemanticSql generateSemanticSql(String text);
    }

    @Override
    public LLMResp generate(LLMReq llmReq) {
        LLMResp llmResp = new LLMResp();
        llmResp.setQuery(llmReq.getQueryText());
        // 1.recall exemplars
        log.debug("OnePassSCSqlGenStrategy llmReq:\n{}", llmReq);
        List<List<Text2SQLExemplar>> exemplarsList = promptHelper.getFewShotExemplars(llmReq);

        // 2.generate sql generation prompt for each self-consistency inference
        ChatApp chatApp = llmReq.getChatAppConfig().get(APP_KEY);
        ChatModelConfig chatModelConfig = chatApp.getChatModelConfig();
        if (!StringUtils.isBlank(parserConfig.getParameterValue(PARSER_FORMAT_JSON_TYPE))) {
            chatModelConfig.setJsonFormat(true);
            chatModelConfig
                    .setJsonFormatType(parserConfig.getParameterValue(PARSER_FORMAT_JSON_TYPE));
        }
        Map<String, List<Text2SQLExemplar>> prompt2Exemplar = new HashMap<>();
        for (List<Text2SQLExemplar> exemplars : exemplarsList) {
            llmReq.setDynamicExemplars(exemplars);
            String prompt = generatePrompt(llmReq, llmResp, chatApp);
            prompt2Exemplar.put(prompt, exemplars);
        }

        // 3.perform multiple self-consistency inferences parallelly
        Map<String, String> output2Prompt = new ConcurrentHashMap<>();
        prompt2Exemplar.keySet().parallelStream().forEach(prompt -> {
            Flux<ChatResponse> chatResponseFlux = llmService.callUser(prompt);
            String response = llmService.collectFluxToStringSafe(chatResponseFlux);
            SemanticSql s2Sql = jsonParseUtil.tryConvertToObject(response,SemanticSql.class);
            output2Prompt.put(s2Sql.getSql(), prompt);
            keyPipelineLog.info("OnePassSCSqlGenStrategy modelReq:\n{} \nmodelResp:\n{}",
                    prompt, s2Sql);
        });

        // 4.format response.
        Pair<String, Map<String, Double>> sqlMapPair =
                ResponseHelper.selfConsistencyVote(Lists.newArrayList(output2Prompt.keySet()));
        llmResp.setSqlOutput(sqlMapPair.getLeft());
        List<Text2SQLExemplar> usedExemplars =
                prompt2Exemplar.get(output2Prompt.get(sqlMapPair.getLeft()));
        llmResp.setSqlRespMap(ResponseHelper.buildSqlRespMap(usedExemplars, sqlMapPair.getRight()));

        return llmResp;
    }

    private String generatePrompt(LLMReq llmReq, LLMResp llmResp, ChatApp chatApp) {
        StringBuilder exemplars = new StringBuilder();
        for (Text2SQLExemplar exemplar : llmReq.getDynamicExemplars()) {
            String exemplarStr = String.format("\nQuestion:%s,Schema:%s,SideInfo:%s,SQL:%s",
                    exemplar.getQuestion(), exemplar.getDbSchema(), exemplar.getSideInfo(),
                    exemplar.getSql());
            exemplars.append(exemplarStr);
        }
        String dataSemantics = promptHelper.buildSchemaStr(llmReq);
        String sideInformation = promptHelper.buildSideInformation(llmReq);
        llmResp.setSchema(dataSemantics);
        llmResp.setSideInfo(sideInformation);

        Map<String, Object> variable = new HashMap<>();
        variable.put("exemplar", exemplars);
        variable.put("question", llmReq.getQueryText());
        variable.put("schema", dataSemantics);
        variable.put("information", sideInformation);

        // use custom prompt template if provided.
        PromptTemplate promptTemplate = new PromptTemplate(chatApp.getPrompt());
        return promptTemplate.render(variable);
    }

    @Override
    public void afterPropertiesSet() {
        SqlGenStrategyFactory
                .addSqlGenerationForFactory(LLMReq.SqlGenType.ONE_PASS_SELF_CONSISTENCY, this);
    }
}
