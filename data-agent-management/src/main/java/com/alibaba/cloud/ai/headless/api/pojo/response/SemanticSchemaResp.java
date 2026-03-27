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
import com.alibaba.cloud.ai.headless.common.pojo.ModelRela;
import com.alibaba.cloud.ai.headless.common.pojo.enums.QueryType;
import com.alibaba.cloud.ai.headless.api.pojo.enums.SchemaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SemanticSchemaResp {

    private Long dataSetId;
    private List<Long> modelIds;
    private SchemaType schemaType;
    private List<MetricSchemaResp> metrics = Lists.newArrayList();
    private List<DimSchemaResp> dimensions = Lists.newArrayList();
    private List<ModelRela> modelRelas = Lists.newArrayList();
    private List<ModelResp> modelResps = Lists.newArrayList();
    private DataSetResp dataSetResp;
    private DatabaseResp databaseResp;
    private QueryType queryType;

    public MetricSchemaResp getMetric(String bizName) {
        return metrics.stream().filter(metric -> bizName.equalsIgnoreCase(metric.getBizName()))
                .findFirst().orElse(null);
    }

    public MetricSchemaResp getMetric(Long id) {
        return metrics.stream().filter(metric -> id.equals(metric.getId())).findFirst()
                .orElse(null);
    }

    public DimSchemaResp getDimension(String bizName) {
        return dimensions.stream()
                .filter(dimension -> bizName.equalsIgnoreCase(dimension.getBizName())).findFirst()
                .orElse(null);
    }

    public DimSchemaResp getDimension(Long id) {
        return dimensions.stream().filter(dimension -> id.equals(dimension.getId())).findFirst()
                .orElse(null);
    }

    public Set<String> getNameFromBizNames(Set<String> bizNames) {
        Set<String> names = new HashSet<>();
        for (String bizName : bizNames) {
            DimSchemaResp dimSchemaResp = getDimension(bizName);
            if (dimSchemaResp != null) {
                names.add(dimSchemaResp.getName());
                continue;
            }
            MetricSchemaResp metricSchemaResp = getMetric(bizName);
            if (metricSchemaResp != null) {
                names.add(metricSchemaResp.getName());
            }
        }
        return names;
    }

}
