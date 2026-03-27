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
package com.alibaba.cloud.ai.headless.api.pojo.enums;

import com.alibaba.cloud.ai.headless.api.pojo.Measure;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByMeasureParams;

import java.util.List;
import java.util.Objects;

public enum MetricType {
    ATOMIC, DERIVED;

    public static MetricType of(String src) {
        for (MetricType metricType : MetricType.values()) {
            if (Objects.nonNull(src) && src.equalsIgnoreCase(metricType.name())) {
                return metricType;
            }
        }
        return null;
    }

    public static Boolean isDerived(MetricDefineType metricDefineType,
            MetricDefineByMeasureParams typeParams) {
        if (MetricDefineType.METRIC.equals(metricDefineType)) {
            return true;
        }
        if (MetricDefineType.FIELD.equals(metricDefineType)) {
            return true;
        }
        if (MetricDefineType.MEASURE.equals(metricDefineType)) {
            List<Measure> measures = typeParams.getMeasures();
            if (measures.size() > 1) {
                return true;
            }
            if (measures.size() == 1
                    && measures.get(0).getBizName().equalsIgnoreCase(typeParams.getExpr())) {
                return false;
            }
        }
        return false;
    }
}
