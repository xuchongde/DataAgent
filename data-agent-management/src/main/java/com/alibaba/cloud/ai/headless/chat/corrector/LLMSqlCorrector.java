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
package com.alibaba.cloud.ai.headless.chat.corrector;

import com.alibaba.cloud.ai.dataagent.service.llm.LlmService;
import com.alibaba.cloud.ai.dataagent.util.JsonParseUtil;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.common.pojo.ChatApp;
import com.alibaba.cloud.ai.headless.common.pojo.Text2SQLExemplar;
import com.alibaba.cloud.ai.headless.common.pojo.enums.AppModule;
import com.alibaba.cloud.ai.headless.common.util.ChatAppManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class LLMSqlCorrector extends BaseSemanticCorrector {

    private static final Logger keyPipelineLog = LoggerFactory.getLogger("keyPipeline");
    private final LlmService llmService;
    private final JsonParseUtil jsonParseUtil;
    public static final String APP_KEY = "S2SQL_CORRECTOR";
    private static final String INSTRUCTION = ""
            + "#Role: You are a senior data engineer experienced in writing SQL."
            + "\n#Task: Your will be provided with a user question and the SQL written by a junior engineer,"
            + "please take a review and help correct it if necessary." + "\n#Rules: "
            + "1.ALWAYS specify time range using `>`,`<`,`>=`,`<=` operator."
            + "2.DO NOT calculate date range using functions."
            + "3.SQL columns and values must be mentioned in the `#Schema`."
            + "\n#Question:{{question}} #Schema:{{schema}} #InputSQL:{{sql}} #Response:";

    public LLMSqlCorrector(LlmService llmService,JsonParseUtil jsonParseUtil) {
        this.llmService=llmService;
        this.jsonParseUtil=jsonParseUtil;
        ChatAppManager.register(APP_KEY, ChatApp.builder().prompt(INSTRUCTION).name("语义SQL修正")
                .appModule(AppModule.CHAT).description("通过大模型对解析S2SQL做二次修正").enable(false).build());
    }

    @Data
    @ToString
    static class SemanticSql {
        @JsonPropertyDescription("either positive or negative")
        @JsonProperty("opinion")
        private String opinion;

        @JsonPropertyDescription("corrected sql if negative")
        @JsonProperty("sql")
        private String sql;
    }

    @Override
    public void doCorrect(ChatQueryContext chatQueryContext, SemanticParseInfo semanticParseInfo) {
        ChatApp chatApp = chatQueryContext.getRequest().getChatAppConfig().get(APP_KEY);
        if (!chatQueryContext.getRequest().getText2SQLType().enableLLM() || Objects.isNull(chatApp)
                || !chatApp.isEnable()) {
            return;
        }

        Text2SQLExemplar exemplar = (Text2SQLExemplar) semanticParseInfo.getProperties()
                .get(Text2SQLExemplar.PROPERTY_KEY);

        String queryText = chatQueryContext.getRequest().getQueryText();
        String promptText = generatePrompt(queryText,
                semanticParseInfo, exemplar);
        SemanticSql s2Sql = null;
        try {
            Flux<ChatResponse> chatResponseFlux = llmService.callUser(promptText);
            String response = llmService.collectFluxToStringSafe(chatResponseFlux);
            // 记录关键日志 (适配新参数)
            keyPipelineLog.info("LLMSqlCorrector modelReq:\n{} \nmodelResp:\n{}", promptText,
                    response);
            // 解析 JSON 响应
            s2Sql = responseToObj(response);

        } catch (Exception e) {
            log.error("Failed to call LLM for LLMSqlCorrector ", e);
            return;
        }
        if ("NEGATIVE".equalsIgnoreCase(s2Sql.getOpinion())
                && StringUtils.isNotBlank(s2Sql.getSql())) {
            semanticParseInfo.getSqlInfo().setCorrectedS2SQL(s2Sql.getSql());
        }
    }

    /**
     * 生成提示词
     */
    private String generatePrompt(String queryText, SemanticParseInfo semanticParseInfo, Text2SQLExemplar exemplar) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("question", queryText);
        variables.put("sql", semanticParseInfo.getSqlInfo().getCorrectedS2SQL());
        variables.put("schema", exemplar.getDbSchema());
        PromptTemplate promptTemplate = new PromptTemplate(INSTRUCTION);
        return promptTemplate.render(variables);
    }

    private SemanticSql responseToObj(String jsonResponse) {
        try {
            return jsonParseUtil.tryConvertToObject(jsonResponse, SemanticSql.class);
        } catch (Exception e) {
            log.error("Failed to parse LLM response JSON: {}", jsonResponse, e);
            return null;
        }
    }
}
