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
package com.alibaba.cloud.ai.dataagent.workflow.tools.vehicle;

import com.alibaba.cloud.ai.dataagent.prompt.PromptLoader;
import com.alibaba.cloud.ai.dataagent.service.llm.LlmService;
import com.alibaba.cloud.ai.dataagent.util.JsonParseUtil;
import com.alibaba.cloud.ai.dataagent.workflow.tools.DataAgentExtTool;
import com.alibaba.cloud.ai.dataagent.workflow.tools.ToolDataResponse;
import com.alibaba.cloud.ai.graph.OverAllState;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("vehicleProfileParserImpl")
@Slf4j
@AllArgsConstructor
public class VehicleProfileParserImpl implements DataAgentExtTool {
    private final LlmService llmService;
    private final JsonParseUtil jsonParseUtil;
    @Override
    public ToolDataResponse toolData(OverAllState state, String userInput) {
        // 1. 加载配置规则
        String rulesContent = loadRulesContent();

        // 2. 构建提示词
        String prompt = buildPrompt(userInput, rulesContent);

        // 3. 调用大模型
        String responseText = callLlm(prompt);
        log.info("VehicleProfileParserTool callLlm response {}",responseText);
        // 4. 解析模型输出
        //VehicleQueryResponse
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
        if(Objects.isNull(response)){
            log.warn("VehicleProfileParserImpl response null");
            return null;
        }
        ToolDataResponse toolResponse = new ToolDataResponse();
        toolResponse.setToolError(response.getError());
        toolResponse.setToolReturnData(response.getCriteria());
        return toolResponse;
    }

    @Override
    public boolean needExecute(OverAllState state, String userInput) {
        // 检查是否包含车辆相关关键词
        if (userInput.contains("车辆") || userInput.contains("车型") || userInput.contains("profile")) {
            return true;
        }

        // 检查是否包含车辆代码模式
        Pattern pattern = Pattern.compile("[A-Z]\\d{2,4}|\\d{4,8}|[A-Z]{2}\\d{2}");
        Matcher matcher = pattern.matcher(userInput);
        return matcher.find();
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
}
