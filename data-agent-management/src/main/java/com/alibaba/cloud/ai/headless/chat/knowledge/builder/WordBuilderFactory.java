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
package com.alibaba.cloud.ai.headless.chat.knowledge.builder;

import com.alibaba.cloud.ai.headless.common.pojo.enums.DictWordType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** DictWord Strategy Factory */
public class WordBuilderFactory {

    private static Map<DictWordType, BaseWordBuilder> wordNatures = new ConcurrentHashMap<>();

    static {
        wordNatures.put(DictWordType.DIMENSION, new DimensionWordBuilder());
        wordNatures.put(DictWordType.METRIC, new MetricWordBuilder());
        wordNatures.put(DictWordType.DATASET, new DataSetWordBuilder());
        wordNatures.put(DictWordType.VALUE, new ValueWordBuilder());
        wordNatures.put(DictWordType.TERM, new TermWordBuilder());
    }

    public static BaseWordBuilder get(DictWordType strategyType) {
        return wordNatures.get(strategyType);
    }
}
