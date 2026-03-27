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

import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.knowledge.HanlpMapResult;
import com.alibaba.cloud.ai.headless.chat.knowledge.KnowledgeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.alibaba.cloud.ai.headless.chat.mapper.MapperConfig.MAPPER_DETECTION_MAX_SIZE;
import static com.alibaba.cloud.ai.headless.chat.mapper.MapperConfig.MAPPER_DETECTION_SIZE;
import static com.alibaba.cloud.ai.headless.chat.mapper.MapperConfig.MAPPER_DIMENSION_VALUE_SIZE;

/**
 * HanlpDictMatchStrategy uses <a href="https://www.hanlp.com/">HanLP</a> to match schema elements.
 * It currently supports prefix and suffix matching against names, values and aliases.
 */
@Service
@Slf4j
public class HanlpDictMatchStrategy extends SingleMatchStrategy<HanlpMapResult> {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    public List<HanlpMapResult> detectByStep(ChatQueryContext chatQueryContext,
            Set<Long> detectDataSetIds, String detectSegment, int offset) {
        // step1. pre search
        Integer oneDetectionMaxSize =
                Integer.valueOf(mapperConfig.getParameterValue(MAPPER_DETECTION_MAX_SIZE));
        LinkedHashSet<HanlpMapResult> hanlpMapResults = knowledgeBaseService
                .prefixSearch(detectSegment, oneDetectionMaxSize,
                        chatQueryContext.getModelIdToDataSetIds(), detectDataSetIds)
                .stream().collect(Collectors.toCollection(LinkedHashSet::new));
        // step2. suffix search
        LinkedHashSet<HanlpMapResult> suffixHanlpMapResults = knowledgeBaseService
                .suffixSearch(detectSegment, oneDetectionMaxSize,
                        chatQueryContext.getModelIdToDataSetIds(), detectDataSetIds)
                .stream().collect(Collectors.toCollection(LinkedHashSet::new));

        hanlpMapResults.addAll(suffixHanlpMapResults);

        if (CollectionUtils.isEmpty(hanlpMapResults)) {
            return new ArrayList<>();
        }
        // step3. merge pre/suffix result
        hanlpMapResults = hanlpMapResults.stream()
                .sorted((a, b) -> -(b.getName().length() - a.getName().length()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // step4. filter by similarity
        hanlpMapResults = hanlpMapResults.stream()
                .filter(term -> term.getSimilarity() >= getThresholdMatch(term.getNatures(),
                        chatQueryContext))
                .filter(term -> CollectionUtils.isNotEmpty(term.getNatures())).map(parseResult -> {
                    parseResult.setOffset(offset);
                    return parseResult;
                }).collect(Collectors.toCollection(LinkedHashSet::new));

        log.debug("detectSegment:{},after isSimilarity parseResults:{}", detectSegment,
                hanlpMapResults);

        // step5. take only M dimensionValue or N-M metric/dimension value per rond.
        int oneDetectionValueSize =
                Integer.valueOf(mapperConfig.getParameterValue(MAPPER_DIMENSION_VALUE_SIZE));
        List<HanlpMapResult> dimensionValues = hanlpMapResults.stream()
                .filter(entry -> mapperHelper.existDimensionValues(entry.getNatures()))
                .limit(oneDetectionValueSize).collect(Collectors.toList());

        Integer oneDetectionSize =
                Integer.valueOf(mapperConfig.getParameterValue(MAPPER_DETECTION_SIZE));
        List<HanlpMapResult> oneRoundResults = new ArrayList<>();

        // add the dimensionValue if it exists
        if (CollectionUtils.isNotEmpty(dimensionValues)) {
            oneRoundResults.addAll(dimensionValues);
        }
        // fill the rest of the list with other results, excluding the dimensionValue if it was
        // added
        if (oneRoundResults.size() < oneDetectionSize) {
            List<HanlpMapResult> additionalResults = hanlpMapResults.stream()
                    .filter(entry -> !mapperHelper.existDimensionValues(entry.getNatures())
                            && !oneRoundResults.contains(entry))
                    .limit(oneDetectionSize - oneRoundResults.size()).collect(Collectors.toList());
            oneRoundResults.addAll(additionalResults);
        }
        return oneRoundResults;
    }

    public double getThresholdMatch(List<String> natures, ChatQueryContext chatQueryContext) {
        Double threshold =
                Double.valueOf(mapperConfig.getParameterValue(MapperConfig.MAPPER_NAME_THRESHOLD));
        Double minThreshold = Double
                .valueOf(mapperConfig.getParameterValue(MapperConfig.MAPPER_NAME_THRESHOLD_MIN));
        if (mapperHelper.existDimensionValues(natures)) {
            threshold = Double
                    .valueOf(mapperConfig.getParameterValue(MapperConfig.MAPPER_VALUE_THRESHOLD));
            minThreshold = Double.valueOf(
                    mapperConfig.getParameterValue(MapperConfig.MAPPER_VALUE_THRESHOLD_MIN));
        }

        return getThreshold(threshold, minThreshold,
                chatQueryContext.getRequest().getMapModeEnum());
    }
}
