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

import com.alibaba.cloud.ai.headless.common.pojo.enums.EngineType;
import com.alibaba.cloud.ai.headless.api.pojo.response.DatabaseResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.DimSchemaResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.MetricSchemaResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An ontology comprises a group of data models that can be joined together either in star schema or
 * snowflake schema.
 */
@Data
public class Ontology {

    private DatabaseResp database;
    private Map<String, ModelResp> modelMap = new HashMap<>();
    private Map<String, List<MetricSchemaResp>> metricMap = new HashMap<>();
    private Map<String, List<DimSchemaResp>> dimensionMap = new HashMap<>();
    private List<JoinRelation> joinRelations;

    public List<MetricSchemaResp> getMetrics() {
        return metricMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<DimSchemaResp> getDimensions() {
        return dimensionMap.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public EngineType getDatabaseType() {
        if (Objects.nonNull(database)) {
            return EngineType.fromString(database.getType().toUpperCase());
        }
        return null;
    }

    public String getDatabaseVersion() {
        if (Objects.nonNull(database)) {
            return database.getVersion();
        }
        return null;
    }

}
