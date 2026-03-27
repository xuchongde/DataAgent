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
package com.alibaba.cloud.ai.headless.common.jsqlparser;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum AggregateEnum {
    MOST("最多", "max"),
    HIGHEST("最高", "max"),
    MAXIMUN("最大", "max"),
    LEAST("最少", "min"),
    SMALLEST("最小", "min"),
    LOWEST("最低", "min"),
    AVERAGE("平均", "avg");

    private String aggregateCh;
    private String aggregateEN;

    AggregateEnum(String aggregateCh, String aggregateEN) {
        this.aggregateCh = aggregateCh;
        this.aggregateEN = aggregateEN;
    }

    public String getAggregateCh() {
        return aggregateCh;
    }

    public String getAggregateEN() {
        return aggregateEN;
    }

    public static Map<String, String> getAggregateEnum() {
        return Arrays.stream(AggregateEnum.values()).collect(
                Collectors.toMap(AggregateEnum::getAggregateCh, AggregateEnum::getAggregateEN));
    }
}
