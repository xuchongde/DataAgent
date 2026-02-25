package com.alibaba.cloud.ai.dataagent.workflow.tools;

import com.alibaba.cloud.ai.dataagent.prompt.PromptLoader;
import com.alibaba.cloud.ai.dataagent.service.llm.LlmService;
import com.alibaba.cloud.ai.dataagent.util.JsonParseUtil;
import com.alibaba.cloud.ai.dataagent.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collections;

@Slf4j
@Component
@AllArgsConstructor
public class VehicleProfileParserTool implements ToolCallback {
    private final LlmService llmService;
    private final JsonParseUtil jsonParseUtil;

    @Override
    public ToolDefinition getToolDefinition()  {
        return DefaultToolDefinition.builder()
            .name("parse_vehicle_profile_query")
                .description("解析用户输入中的车辆配置条件，将车辆配置编码映射到数据库字段。")
                .inputSchema("""
                    {
                      "type": "object",
                      "properties": {
                        "userInput": {
                          "type": "string",
                          "description": "原始用户查询文本"
                        }
                      },
                      "required": ["userInput"]
                    }
                    """)
                .build();
    }

    @Override
    public String call(String toolInput) {
        // 1. 加载配置规则
        String rulesContent = loadRulesContent();

        // 2. 构建提示词
        String prompt = buildPrompt(toolInput, rulesContent);

        // 3. 调用大模型
        String responseText = callLlm(prompt);
        log.info("VehicleProfileParserTool callLlm response {}",responseText);
        // 4. 解析模型输出
        return parseModelResponse(responseText);
    }

    private String loadRulesContent() {
        return PromptLoader.loadPrompt("business_domain_rules_vehicle");
    }

    private String buildPrompt(String userInput, String rulesContent) {
        return String.format("""
            %s
            用户查询: %s
            请严格按以下JSON格式输出：
            {
              "criteria": {
                "eseries_code": "",
                "model_code": "",
                "target_date": "",
                "package_code": ""
              },
              "ambiguity": [],
              "error": null
            }
            注意：
            1. 只输出JSON，不要包含任何其他文本
            2. 如果有多个可能的匹配，优先使用最符合规则的
            """, rulesContent, userInput);
    }

    private String callLlm(String prompt) {
        Flux<ChatResponse> responseFlux = llmService.callUser(prompt);
        return responseFlux.collectList()
                .map(chatResponses -> {
                    StringBuilder sb = new StringBuilder();
                    for (ChatResponse response : chatResponses) {
                        sb.append(response.getResult().getOutput().getText());
                    }
                    return sb.toString();
                })
                .block();
    }

    private String parseModelResponse(String responseText) {
        String resp="";
        VehicleQueryResponse response;
        try {
            // 尝试解析为标准JSON
            response = jsonParseUtil.tryConvertToObject(responseText, VehicleQueryResponse.class);
        } catch (Exception e) {
            // 降级处理
            response = new VehicleQueryResponse(
                    Collections.emptyMap(),
                    new String[]{"解析失败: " + e.getMessage()},
                    "解析失败"
            );
        }
        try {
            resp = JsonUtil.getObjectMapper().writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("writeValueAsString error {}",e);
        }
        return resp;
    }
}
