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
package com.alibaba.cloud.ai.headless.server.pojo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class ModelCluster {

    private static final String split = "_";
    private Set<Long> modelIds = new LinkedHashSet<>();
    private String key;
    private boolean containsPartitionDimensions;

    public static ModelCluster build(Set<Long> modelIds, Boolean containsPartitionDimensions) {
        ModelCluster modelCluster = new ModelCluster();
        modelCluster.setModelIds(modelIds);
        modelCluster.setKey(StringUtils.join(modelIds, split));
        modelCluster.setContainsPartitionDimensions(containsPartitionDimensions);
        return modelCluster;
    }
}
