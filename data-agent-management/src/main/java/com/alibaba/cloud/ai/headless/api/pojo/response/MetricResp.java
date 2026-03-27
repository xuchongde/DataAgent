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
package com.alibaba.cloud.ai.headless.api.pojo.response;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.pojo.DataFormat;
import com.alibaba.cloud.ai.headless.api.pojo.*;
import com.alibaba.cloud.ai.headless.api.pojo.enums.MetricDefineType;
import com.alibaba.cloud.ai.headless.api.pojo.enums.MetricType;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
public class MetricResp extends SchemaItem {

    private Long modelId;

    private Long domainId;

    private String modelBizName;

    private String modelName;

    // ATOMIC DERIVED
    private String type;

    private String dataFormatType;

    private DataFormat dataFormat;

    private String alias;

    private List<String> classifications;

    private RelateDimension relateDimension;

    private boolean hasAdminRes = false;

    private Boolean isCollect;

    private Map<String, Object> ext = new HashMap<>();

    private MetricDefineType metricDefineType = MetricDefineType.MEASURE;

    private MetricDefineByMeasureParams metricDefineByMeasureParams;

    private MetricDefineByFieldParams metricDefineByFieldParams;

    private MetricDefineByMetricParams metricDefineByMetricParams;

    private int isTag;

    private Integer isPublish;

    private double similarity;

    private String defaultAgg;

    private boolean containsPartitionDimensions;

    public void setMetricDefinition(MetricDefineType type, MetricDefineParams params) {
        if (MetricDefineType.MEASURE.equals(type)) {
            assert params instanceof MetricDefineByMeasureParams;
            metricDefineByMeasureParams = (MetricDefineByMeasureParams) params;
        } else if (MetricDefineType.FIELD.equals(type)) {
            assert params instanceof MetricDefineByFieldParams;
            metricDefineByFieldParams = (MetricDefineByFieldParams) params;
        } else if (MetricDefineType.METRIC.equals(type)) {
            assert params instanceof MetricDefineByMetricParams;
            metricDefineByMetricParams = (MetricDefineByMetricParams) params;
        }
    }

    public void setClassifications(String tag) {
        if (StringUtils.isBlank(tag)) {
            classifications = Lists.newArrayList();
        } else {
            classifications = Arrays.asList(tag.split(","));
        }
    }

    public String getRelaDimensionIdKey() {
        if (relateDimension == null
                || CollectionUtils.isEmpty(relateDimension.getDrillDownDimensions())) {
            return "";
        }
        return relateDimension.getDrillDownDimensions().stream()
                .map(DrillDownDimension::getDimensionId).map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public List<DrillDownDimension> getDrillDownDimensions() {
        if (relateDimension == null
                || CollectionUtils.isEmpty(relateDimension.getDrillDownDimensions())) {
            return Lists.newArrayList();
        }
        return relateDimension.getDrillDownDimensions();
    }

    public String getExpr() {
        if (MetricDefineType.MEASURE.equals(metricDefineType)) {
            return metricDefineByMeasureParams.getExpr();
        } else if (MetricDefineType.METRIC.equals(metricDefineType)) {
            return metricDefineByMetricParams.getExpr();
        } else if (MetricDefineType.FIELD.equals(metricDefineType)) {
            return metricDefineByFieldParams.getExpr();
        }
        return "";
    }

    public boolean isDerived() {
        return MetricType.isDerived(metricDefineType, metricDefineByMeasureParams);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
