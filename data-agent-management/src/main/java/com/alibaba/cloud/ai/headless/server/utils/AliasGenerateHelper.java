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

import com.alibaba.cloud.ai.dataagent.service.llm.LlmService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AliasGenerateHelper {

    private static final Logger keyPipelineLog = LoggerFactory.getLogger("keyPipeline");
    private LlmService llmService;
    private static final String NAME_ALIAS_INSTRUCTION = ""
            + "#Role: You are a professional data analyst specializing in metrics and dimensions."
            + "\n#Task: You will be provided with metadata about a metric or dimension, please help "
            + "generate a few aliases in the same language as its `fieldName`." + "\n#Rules:"
            + "1. Please do not generate aliases like xxx1, xxx2, xxx3."
            + "2. Please do not generate aliases that are the same as the original names of metrics/dimensions."
            + "3. Please pay attention to the quality of the generated aliases and "
            + "avoid creating aliases that look like test data."
            + "4. Please output as a json string array."
            + "\n#Metadata: {'table':'{{table}}', 'name':'{{name}}', 'type':'{{type}}', "
            + "'field':'field', 'description':'{{desc}}'}" + "\n#Output:";

    private static final String VALUE_ALIAS_INSTRUCTION =
            "" + "\n#Role: You are a professional data analyst."
                    + "\n#Task: You will be provided with a json array of dimension values,"
                    + "please help generate a few aliases for each value." + "\n#Rule:"
                    + "1. ALWAYS output json array for each value."
                    + "2. The aliases should be in the same language as its original value."
                    + "\n#Exemplar:" + "Values: [\\\"qq_music\\\",\\\"kugou_music\\\"], "
                    + "Output: {\\\"tran\\\":[\\\"qq音乐\\\",\\\"酷狗音乐\\\"],"
                    + "         \\\"alias\\\":{\\\"qq_music\\\":[\\\"q音\\\",\\\"qq音乐\\\"],"
                    + "         \\\"kugou_music\\\":[\\\"kugou\\\",\\\"酷狗\\\"]}}"
                    + "\nValues: {{values}}, Output:";

    public String generateAlias(String mockType, String name, String bizName, String table,
            String desc) {
        Map<String, Object> variable = new HashMap<>();
        variable.put("table", table);
        variable.put("name", name);
        variable.put("field", bizName);
        variable.put("type", mockType);
        variable.put("desc", desc);

        PromptTemplate promptTemplate = new PromptTemplate(NAME_ALIAS_INSTRUCTION);
        String promptText = promptTemplate.render(variable);
        String response = getChatCompletion(promptText);
        keyPipelineLog.info("AliasGenerateHelper.generateAlias modelReq:\n{} \nmodelResp:\n{}",
                promptText, response);
        return response;
    }

    public String generateDimensionValueAlias(String json) {
        Map<String, Object> variable = new HashMap<>();
        variable.put("values", json);
        PromptTemplate promptTemplate = new PromptTemplate(VALUE_ALIAS_INSTRUCTION);
        String promptText = promptTemplate.render(variable);
        String response = getChatCompletion(promptText);
        keyPipelineLog.info(
                "AliasGenerateHelper.generateValueAlias modelReq:\n{} " + "\nmodelResp:\n{}",
                promptText, response);
        return response;
    }

    private String getChatCompletion(String promptText) {
        Flux<ChatResponse> chatResponseFlux = llmService.callSystem(promptText);
        String response = llmService.collectFluxToStringSafe(chatResponseFlux);
        return response;
    }

    private static String extractString(String targetString, String left, String right,
            Boolean exclusionFlag) {
        if (targetString == null || left == null || right == null || exclusionFlag == null) {
            return targetString;
        }
        if (left.equals(right)) {
            int firstIndex = targetString.indexOf(left);
            if (firstIndex == -1) {
                return null;
            }
            int secondIndex = targetString.indexOf(left, firstIndex + left.length());
            if (secondIndex == -1) {
                return null;
            }
            String extractedString =
                    targetString.substring(firstIndex + left.length(), secondIndex);
            if (!exclusionFlag) {
                extractedString = left + extractedString + right;
            }
            return extractedString;
        } else {
            int leftIndex = targetString.indexOf(left);
            if (leftIndex == -1) {
                return null;
            }
            int start = leftIndex + left.length();
            int rightIndex = targetString.indexOf(right, start);
            if (rightIndex == -1) {
                return null;
            }
            String extractedString = targetString.substring(start, rightIndex);
            if (!exclusionFlag) {
                extractedString = left + extractedString + right;
            }
            return extractedString;
        }
    }

    public static String extractJsonStringFromAiMessage(String aiMessage) {
        class BoundaryPattern {
            final String left;
            final String right;
            final Boolean exclusionFlag;

            public BoundaryPattern(String start, String end, Boolean includeMarkers) {
                this.left = start;
                this.right = end;
                this.exclusionFlag = includeMarkers;
            }
        }
        BoundaryPattern[] patterns = {
                        // 不做任何匹配
                        new BoundaryPattern(null, null, null),
                        // ```{"name":"Alice","age":25,"city":"NewYork"}```
                        new BoundaryPattern("```", "```", true),
                        // ```json {"name":"Alice","age":25,"city":"NewYork"}```
                        new BoundaryPattern("```json", "```", true),
                        // ```JSON {"name":"Alice","age":25,"city":"NewYork"}```
                        new BoundaryPattern("```JSON", "```", true),
                        // {"name":"Alice","age":25,"city":"NewYork"}
                        new BoundaryPattern("{", "}", false),
                        // ["Alice", "Bob"]
                        new BoundaryPattern("[", "]", false)};
        for (BoundaryPattern pattern : patterns) {
            String extracted =
                    extractString(aiMessage, pattern.left, pattern.right, pattern.exclusionFlag);
            if (extracted == null) {
                continue;
            }
            // 判断是否能解析为Object或者Array
            try {
                JSON.parseObject(extracted);
                return extracted;
            } catch (JSONException ignored) {
                // ignored
            }
            try {
                JSON.parseArray(extracted);
                return extracted;
            } catch (JSONException ignored) {
                // ignored
            }
        }
        throw new JSONException("json extract failed");
    }
}
