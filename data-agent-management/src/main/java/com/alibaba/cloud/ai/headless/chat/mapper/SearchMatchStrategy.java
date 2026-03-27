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
package com.alibaba.cloud.ai.headless.chat.mapper;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.api.pojo.response.S2Term;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.knowledge.HanlpMapResult;
import com.alibaba.cloud.ai.headless.chat.knowledge.KnowledgeBaseService;
import com.alibaba.cloud.ai.headless.chat.knowledge.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SearchMatchStrategy encapsulates a concrete matching algorithm executed during search process.
 */
@Service
public class SearchMatchStrategy extends BaseMatchStrategy<HanlpMapResult> {

    private static final int SEARCH_SIZE = 3;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private MapperHelper mapperHelper;

    @Override
    public Map<MatchText, List<HanlpMapResult>> match(ChatQueryContext chatQueryContext,
            List<S2Term> originals, Set<Long> detectDataSetIds) {
        String text = chatQueryContext.getRequest().getQueryText();
        Map<Integer, Integer> regOffsetToLength = mapperHelper.getRegOffsetToLength(originals);

        List<Integer> detectIndexList = Lists.newArrayList();

        for (Integer index = 0; index < text.length();) {

            if (index < text.length()) {
                detectIndexList.add(index);
            }
            Integer regLength = regOffsetToLength.get(index);
            if (Objects.nonNull(regLength)) {
                index = index + regLength;
            } else {
                index++;
            }
        }
        Map<MatchText, List<HanlpMapResult>> regTextMap = new ConcurrentHashMap<>();
        detectIndexList.stream().parallel().forEach(detectIndex -> {
            String regText = text.substring(0, detectIndex);
            String detectSegment = text.substring(detectIndex);

            if (StringUtils.isNotEmpty(detectSegment)) {
                List<HanlpMapResult> hanlpMapResults =
                        knowledgeBaseService.prefixSearch(detectSegment, SearchService.SEARCH_SIZE,
                                chatQueryContext.getModelIdToDataSetIds(), detectDataSetIds);
                List<HanlpMapResult> suffixHanlpMapResults =
                        knowledgeBaseService.suffixSearch(detectSegment, SEARCH_SIZE,
                                chatQueryContext.getModelIdToDataSetIds(), detectDataSetIds);
                hanlpMapResults.addAll(suffixHanlpMapResults);
                MatchText matchText =
                        MatchText.builder().regText(regText).detectSegment(detectSegment).build();
                regTextMap.put(matchText, hanlpMapResults);
            }
        });
        return regTextMap;
    }
}
