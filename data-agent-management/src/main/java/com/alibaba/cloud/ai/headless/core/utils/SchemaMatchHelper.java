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
package com.alibaba.cloud.ai.headless.core.utils;

import com.alibaba.cloud.ai.headless.api.pojo.SchemaElementMatch;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Schema Match Helper */
@Slf4j
public class SchemaMatchHelper {

    public static void removeSamePrefixDetectWord(List<SchemaElementMatch> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return;
        }

        Set<String> metricDimensionDetectWordSet =
                matches.stream().filter(SchemaMatchHelper::isMetricOrDimension)
                        .map(SchemaElementMatch::getDetectWord).collect(Collectors.toSet());

        matches.removeIf(elementMatch -> {
            if (!isMetricOrDimension(elementMatch)) {
                return false;
            }
            for (String detectWord : metricDimensionDetectWordSet) {
                if (detectWord.startsWith(elementMatch.getDetectWord())
                        && detectWord.length() > elementMatch.getDetectWord().length()) {
                    return true;
                }
            }
            return false;
        });
    }

    private static boolean isMetricOrDimension(SchemaElementMatch elementMatch) {
        return SchemaElementType.METRIC.equals(elementMatch.getElement().getType())
                || SchemaElementType.DIMENSION.equals(elementMatch.getElement().getType());
    }
}
