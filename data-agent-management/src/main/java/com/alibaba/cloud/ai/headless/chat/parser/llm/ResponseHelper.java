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

import com.alibaba.cloud.ai.headless.common.pojo.Text2SQLExemplar;
import com.alibaba.cloud.ai.headless.chat.query.llm.s2sql.LLMSqlResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class ResponseHelper {

    public static String getSql(String sqlOutput) {
        String sql = "";
        try {
            sqlOutput = sqlOutput.trim();
            String pattern = "SQL:(.*)";
            Pattern regexPattern = Pattern.compile(pattern);
            Matcher matcher = regexPattern.matcher(sqlOutput);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return sql;
    }

    public static Pair<String, Map<String, Double>> selfConsistencyVote(List<String> outputList) {
        Map<String, Integer> inputCounts = new HashMap<>();
        for (String input : outputList) {
            inputCounts.put(input, inputCounts.getOrDefault(input, 0) + 1);
        }

        String inputMax = null;
        int maxCount = 0;
        int inputSize = outputList.size();
        Map<String, Double> votePercentage = new HashMap<>();
        for (Map.Entry<String, Integer> entry : inputCounts.entrySet()) {
            String input = entry.getKey();
            int count = entry.getValue();
            if (count > maxCount) {
                inputMax = input;
                maxCount = count;
            }
            double percentage = (double) count / inputSize;
            votePercentage.put(input, percentage);
        }
        return Pair.of(inputMax, votePercentage);
    }

    public static Map<String, LLMSqlResp> buildSqlRespMap(List<Text2SQLExemplar> sqlExamples,
            Map<String, Double> sqlMap) {
        if (sqlMap == null) {
            return new HashMap<>();
        }
        return sqlMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> LLMSqlResp.builder()
                        .sqlWeight(entry.getValue()).fewShots(sqlExamples).build()));
    }
}
