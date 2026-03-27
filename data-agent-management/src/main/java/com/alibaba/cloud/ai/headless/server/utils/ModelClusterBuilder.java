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

import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelSchemaResp;
import com.alibaba.cloud.ai.headless.server.pojo.ModelCluster;
import com.alibaba.cloud.ai.headless.server.service.HeadlessSchemaService;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelClusterBuilder {

    public static Map<String, ModelCluster> buildModelClusters(List<Long> modelIds) {
        HeadlessSchemaService schemaService = ContextUtils.getBean(HeadlessSchemaService.class);
        List<ModelSchemaResp> modelSchemaResps = schemaService.fetchModelSchemaResps(modelIds);
        Map<Long, ModelSchemaResp> modelIdToModelSchema = modelSchemaResps.stream()
                .collect(Collectors.toMap(ModelSchemaResp::getId, value -> value, (k1, k2) -> k1));

        Set<Long> visited = new HashSet<>();
        List<Set<Long>> modelClusters = new ArrayList<>();
        for (ModelSchemaResp model : modelSchemaResps) {
            if (!visited.contains(model.getId())) {
                Set<Long> modelCluster = new HashSet<>();
                dfs(model, modelIdToModelSchema, visited, modelCluster);
                modelClusters.add(modelCluster);
            }
        }

        return modelClusters.stream()
                .map(modelCluster -> getModelCluster(modelIdToModelSchema, modelCluster))
                .collect(Collectors.toMap(ModelCluster::getKey, value -> value, (k1, k2) -> k1));
    }

    private static ModelCluster getModelCluster(Map<Long, ModelSchemaResp> modelIdToModelSchema,
            Set<Long> modelIds) {
        boolean containsPartitionDimensions = modelIds.stream().map(modelIdToModelSchema::get)
                .filter(Objects::nonNull).anyMatch(modelSchemaResp -> CollectionUtils
                        .isNotEmpty(modelSchemaResp.getTimeDimension()));

        return ModelCluster.build(modelIds, containsPartitionDimensions);
    }

    private static void dfs(ModelSchemaResp model, Map<Long, ModelSchemaResp> modelMap,
            Set<Long> visited, Set<Long> modelCluster) {
        if (Objects.isNull(model)) {
            return;
        }
        visited.add(model.getId());
        modelCluster.add(model.getId());
        for (Long neighborId : model.getModelClusterSet()) {
            if (!visited.contains(neighborId)) {
                dfs(modelMap.get(neighborId), modelMap, visited, modelCluster);
            }
        }
    }
}
