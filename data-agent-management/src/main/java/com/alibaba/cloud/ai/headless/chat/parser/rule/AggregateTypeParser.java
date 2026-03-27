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

import com.alibaba.cloud.ai.headless.common.pojo.enums.AggregateTypeEnum;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.parser.SemanticParser;
import com.alibaba.cloud.ai.headless.chat.query.SemanticQuery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alibaba.cloud.ai.headless.common.pojo.enums.AggregateTypeEnum.COUNT;
import static com.alibaba.cloud.ai.headless.common.pojo.enums.AggregateTypeEnum.DISTINCT;

/**
 * AggregateTypeParser extracts aggregation type specified in the user query based on keyword
 * matching. Currently, it supports 7 types of aggregation: max, min, sum, avg, topN, distinct
 * count, count.
 */
@Slf4j
public class AggregateTypeParser implements SemanticParser {

    private static final Map<AggregateTypeEnum, Pattern> REGX_MAP = Stream.of(
            new AbstractMap.SimpleEntry<>(AggregateTypeEnum.MAX,
                    Pattern.compile("(?i)(最大值|最大|max|峰值|最高|最多)")),
            new AbstractMap.SimpleEntry<>(AggregateTypeEnum.MIN,
                    Pattern.compile("(?i)(最小值|最小|min|最低|最少)")),
            new AbstractMap.SimpleEntry<>(AggregateTypeEnum.SUM,
                    Pattern.compile("(?i)(汇总|总和|sum)")),
            new AbstractMap.SimpleEntry<>(AggregateTypeEnum.AVG,
                    Pattern.compile("(?i)(平均值|日均|平均|avg)")),
            new AbstractMap.SimpleEntry<>(AggregateTypeEnum.TOPN, Pattern.compile("(?i)(top)")),
            new AbstractMap.SimpleEntry<>(DISTINCT, Pattern.compile("(?i)(uv)")),
            new AbstractMap.SimpleEntry<>(COUNT, Pattern.compile("(?i)(总数|pv)")),
            new AbstractMap.SimpleEntry<>(AggregateTypeEnum.NONE, Pattern.compile("(?i)(明细)")))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k2));

    @Override
    public void parse(ChatQueryContext chatQueryContext) {
        String queryText = chatQueryContext.getRequest().getQueryText();
        AggregateConf aggregateConf = resolveAggregateConf(queryText);

        for (SemanticQuery semanticQuery : chatQueryContext.getCandidateQueries()) {
            if (!AggregateTypeEnum.NONE.equals(semanticQuery.getParseInfo().getAggType())) {
                continue;
            }
            semanticQuery.getParseInfo().setAggType(aggregateConf.type);
            int detectWordLength = 0;
            if (StringUtils.isNotEmpty(aggregateConf.detectWord)) {
                detectWordLength = aggregateConf.detectWord.length();
            }
            semanticQuery.getParseInfo()
                    .setScore(semanticQuery.getParseInfo().getScore() + detectWordLength);
        }
    }

    public AggregateTypeEnum resolveAggregateType(String queryText) {
        AggregateConf aggregateConf = resolveAggregateConf(queryText);
        return aggregateConf.type;
    }

    private AggregateConf resolveAggregateConf(String queryText) {
        Map<AggregateTypeEnum, Integer> aggregateCount = new HashMap<>(REGX_MAP.size());
        Map<AggregateTypeEnum, String> aggregateWord = new HashMap<>(REGX_MAP.size());

        for (Map.Entry<AggregateTypeEnum, Pattern> entry : REGX_MAP.entrySet()) {
            Matcher matcher = entry.getValue().matcher(queryText);
            int count = 0;
            String detectWord = null;
            while (matcher.find()) {
                count++;
                detectWord = matcher.group();
            }
            if (count > 0) {
                aggregateCount.put(entry.getKey(), count);
                aggregateWord.put(entry.getKey(), detectWord);
            }
        }

        AggregateTypeEnum type =
                aggregateCount.entrySet().stream().max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse(AggregateTypeEnum.NONE);
        String detectWord = aggregateWord.get(type);
        return new AggregateConf(type, detectWord);
    }

    @AllArgsConstructor
    static class AggregateConf {
        public AggregateTypeEnum type;
        public String detectWord;
    }
}
