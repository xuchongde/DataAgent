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
import com.alibaba.cloud.ai.headless.common.pojo.enums.AppModule;
import com.alibaba.cloud.ai.headless.common.util.ChatAppManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 物理SQL修正器 - 使用LLM优化物理SQL性能
 */
@Slf4j
public class LLMPhysicalSqlCorrector extends BaseSemanticCorrector {

    private static final Logger keyPipelineLog = LoggerFactory.getLogger("keyPipeline");

    private final LlmService llmService;
    private final JsonParseUtil jsonParseUtil;

    public static final String APP_KEY = "PHYSICAL_SQL_CORRECTOR";
    private static final String INSTRUCTION = ""
            + "#Role: You are a senior database performance optimization expert experienced in SQL tuning."
            + "\n\n#Task: You will be provided with a user question and the corresponding physical SQL query,"
            + " please analyze and optimize this SQL to improve query performance." + "\n\n#Rules:"
            + "\n1. DO NOT add or introduce any new fields, columns, or aliases that are not in the original SQL."
            + "\n2. Push WHERE conditions into JOIN ON clauses when possible to reduce intermediate result sets."
            + "\n3. Optimize JOIN order by placing smaller tables or tables with selective conditions first."
            + "\n4. For date range conditions, ensure they are applied as early as possible in the query execution."
            + "\n5. Remove or comment out database-specific index hints (like USE INDEX) that may cause syntax errors."
            + "\n6. ONLY modify the structure and order of existing elements, do not change field names or add new ones."
            + "\n7. Ensure the optimized SQL is syntactically correct and logically equivalent to the original."
            + "\n\n#Question: {{question}}" + "\n\n#OriginalSQL: {{sql}}";

    public LLMPhysicalSqlCorrector(LlmService llmService,JsonParseUtil jsonParseUtil) {
        this.llmService = llmService;
        this.jsonParseUtil=jsonParseUtil;
        ChatAppManager.register(APP_KEY, ChatApp.builder().prompt(INSTRUCTION).name("物理SQL修正")
                .appModule(AppModule.CHAT).description("通过大模型对物理SQL做性能优化").enable(false).build());
    }

    @Data
    @ToString
    static class PhysicalSql {
        @JsonProperty("opinion")
        @JsonPropertyDescription("either positive or negative")
        private String opinion;
        @JsonProperty("sql")
        @JsonPropertyDescription("optimized sql if negative")
        private String sql;
    }

    @Override
    public void doCorrect(ChatQueryContext chatQueryContext, SemanticParseInfo semanticParseInfo) {
        // 1. 基础校验
        ChatApp chatApp = chatQueryContext.getRequest().getChatAppConfig().get(APP_KEY);
        if (!chatQueryContext.getRequest().getText2SQLType().enableLLM() || Objects.isNull(chatApp)
                || !chatApp.isEnable()) {
            return;
        }

        // 2 获取原始信息
        String queryText = chatQueryContext.getRequest().getQueryText();
        String originalSql = semanticParseInfo.getSqlInfo().getQuerySQL();

        if (queryText == null || queryText.trim().isEmpty()
                || originalSql == null || originalSql.trim().isEmpty()) {
            return;
        }

        // 3. 构建提示词
        String promptText = generatePrompt(queryText, originalSql);

        // 6. 调用 LLM 并获取响应
        PhysicalSql physicalSql = null;
        try {
            Flux<ChatResponse> chatResponseFlux = llmService.callUser(promptText);
            String response = llmService.collectFluxToStringSafe(chatResponseFlux);
            // 记录关键日志 (适配新参数)
            keyPipelineLog.info("LLMPhysicalSqlCorrector modelReq:\n{} \nmodelResp:\n{}", promptText,
                    response);
            // 解析 JSON 响应
            physicalSql = responseToObj(response);

        } catch (Exception e) {
            log.error("Failed to call LLM for LLMPhysicalSqlCorrector ", e);
            return;
        }

        // 7. 结果处理
        if (physicalSql != null
                && "NEGATIVE".equalsIgnoreCase(physicalSql.getOpinion())
                && physicalSql.getSql() != null
                && !physicalSql.getSql().trim().isEmpty()) {
            // 设置修正后的 SQL
            semanticParseInfo.getSqlInfo().setCorrectedQuerySQL(physicalSql.getSql());
            log.info("SQL corrected by LLM for dataset {}: {}",
                    semanticParseInfo.getDataSetId(), physicalSql.getSql());
        }
    }

    /**
     * 生成提示词
     */
    private String generatePrompt(String question, String sql) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("sql", sql);

        PromptTemplate promptTemplate = new PromptTemplate(INSTRUCTION);
        return promptTemplate.render(variables);
    }

    /**
     * 从 JSON 响应中解析 PhysicalSql 对象
     */
    private PhysicalSql responseToObj(String jsonResponse) {
        try {
            return jsonParseUtil.tryConvertToObject(jsonResponse,PhysicalSql.class);
        } catch (Exception e) {
            log.error("Failed to parse LLM response JSON: {}", jsonResponse, e);
            return null;
        }
    }
}
