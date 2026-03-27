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
package com.alibaba.cloud.ai.headless.api.pojo.request;

import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByFieldParams;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByMeasureParams;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByMetricParams;
import com.alibaba.cloud.ai.headless.api.pojo.enums.MetricDefineType;
import com.alibaba.cloud.ai.headless.api.pojo.enums.MetricType;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class MetricReq extends MetricBaseReq {

    private MetricDefineType metricDefineType = MetricDefineType.MEASURE;
    private MetricDefineByMeasureParams metricDefineByMeasureParams;
    private MetricDefineByFieldParams metricDefineByFieldParams;
    private MetricDefineByMetricParams metricDefineByMetricParams;

    public String getTypeParamsJson() {
        if (MetricDefineType.FIELD.equals(metricDefineType) && metricDefineByFieldParams != null) {
            return JSONObject.toJSONString(metricDefineByFieldParams);
        } else if (MetricDefineType.MEASURE.equals(metricDefineType)
                && metricDefineByMeasureParams != null) {
            return JSONObject.toJSONString(metricDefineByMeasureParams);
        } else if (MetricDefineType.METRIC.equals(metricDefineType)
                && metricDefineByMetricParams != null) {
            return JSONObject.toJSONString(metricDefineByMetricParams);
        }
        return null;
    }

    public MetricType getMetricType() {
        return MetricType.isDerived(metricDefineType, metricDefineByMeasureParams)
                ? MetricType.DERIVED
                : MetricType.ATOMIC;
    }
}
