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

import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlSelectFunctionHelper;
import com.alibaba.cloud.ai.headless.common.pojo.exception.InvalidArgumentException;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByFieldParams;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByMeasureParams;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByMetricParams;
import com.alibaba.cloud.ai.headless.api.pojo.enums.MetricDefineType;
import com.alibaba.cloud.ai.headless.api.pojo.request.MetricReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class MetricCheckUtils {

    public static void checkParam(List<MetricReq> metricReqList) {
        metricReqList.forEach(metricReq -> {
            String expr = "";
            if (MetricDefineType.METRIC.equals(metricReq.getMetricDefineType())) {
                MetricDefineByMetricParams typeParams = metricReq.getMetricDefineByMetricParams();
                if (typeParams == null) {
                    throw new InvalidArgumentException("指标定义参数不可为空");
                }
                expr = typeParams.getExpr();
                if (CollectionUtils.isEmpty(typeParams.getMetrics())) {
                    throw new InvalidArgumentException("定义指标的指标列表参数不可为空");
                }
                if (hasAggregateFunction(expr)) {
                    throw new InvalidArgumentException("基于指标来创建指标,表达式中不可再包含聚合函数");
                }
            }
            if (MetricDefineType.MEASURE.equals(metricReq.getMetricDefineType())) {
                MetricDefineByMeasureParams typeParams = metricReq.getMetricDefineByMeasureParams();
                if (typeParams == null) {
                    throw new InvalidArgumentException("指标定义参数不可为空");
                }
                expr = typeParams.getExpr();
                if (hasAggregateFunction(expr)) {
                    throw new InvalidArgumentException("基于度量来创建指标,表达式中不可再包含聚合函数");
                }
            }
            if (MetricDefineType.FIELD.equals(metricReq.getMetricDefineType())) {
                MetricDefineByFieldParams typeParams = metricReq.getMetricDefineByFieldParams();
                if (typeParams == null) {
                    throw new InvalidArgumentException("指标定义参数不可为空");
                }
                expr = typeParams.getExpr();
                // if (CollectionUtils.isEmpty(typeParams.getFields())) {
                // throw new InvalidArgumentException("定义指标的字段列表参数不可为空");
                // }
                if (!hasAggregateFunction(expr)) {
                    throw new InvalidArgumentException("基于字段来创建指标,表达式中必须包含聚合函数");
                }
            }
            if (StringUtils.isBlank(expr)) {
                throw new InvalidArgumentException("表达式不可为空");
            }
            String forbiddenCharacters =
                    NameCheckUtils.findForbiddenCharacters(metricReq.getName());
            if (StringUtils.isNotBlank(forbiddenCharacters)) {
                throw new InvalidArgumentException(
                        String.format("名称包含特殊字符%s, 请修改", forbiddenCharacters));
            }
        });
    }

    private static boolean hasAggregateFunction(String expr) {
        return !CollectionUtils.isEmpty(SqlSelectFunctionHelper.getAggregateFunctions(expr));
    }

}
