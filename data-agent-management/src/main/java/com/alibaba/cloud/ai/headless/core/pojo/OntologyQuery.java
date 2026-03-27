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
package com.alibaba.cloud.ai.headless.core.pojo;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.alibaba.cloud.ai.headless.common.pojo.ColumnOrder;
import com.alibaba.cloud.ai.headless.api.pojo.enums.AggOption;
import com.alibaba.cloud.ai.headless.api.pojo.response.DimSchemaResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.MetricSchemaResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An ontology query comprises metrics/dimensions that are relevant to the semantic query. Note that
 * metrics/dimensions in the ontology query must be a subset of an ontology.
 */
@Data
public class OntologyQuery {

    private Map<String, ModelResp> modelMap = Maps.newHashMap();
    private Map<String, Set<MetricSchemaResp>> metricMap = Maps.newHashMap();
    private Map<String, Set<DimSchemaResp>> dimensionMap = Maps.newHashMap();
    private Set<String> fields = Sets.newHashSet();
    private Long limit;
    private List<ColumnOrder> order;
    private boolean nativeQuery = true;
    private AggOption aggOption = AggOption.NATIVE;
    private String sql;

    public Set<ModelResp> getModels() {
        return modelMap.values().stream().collect(Collectors.toSet());
    }

    public Set<DimSchemaResp> getDimensions() {
        Set<DimSchemaResp> dimensions = Sets.newHashSet();
        dimensionMap.entrySet().forEach(entry -> {
            dimensions.addAll(entry.getValue());
        });
        return dimensions;
    }

    public Set<MetricSchemaResp> getMetrics() {
        Set<MetricSchemaResp> metrics = Sets.newHashSet();
        metricMap.entrySet().forEach(entry -> {
            metrics.addAll(entry.getValue());
        });
        return metrics;
    }

    public Set<MetricSchemaResp> getMetricsByModel(String modelName) {
        return metricMap.get(modelName);
    }

    public Set<DimSchemaResp> getDimensionsByModel(String modelName) {
        return dimensionMap.get(modelName);
    }
}
