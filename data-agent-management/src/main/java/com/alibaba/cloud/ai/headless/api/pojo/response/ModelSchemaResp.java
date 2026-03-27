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

import com.google.common.collect.Sets;
import com.alibaba.cloud.ai.headless.common.pojo.ModelRela;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelSchemaResp extends ModelResp {

    private List<MetricSchemaResp> metrics;
    private List<DimSchemaResp> dimensions;
    private List<ModelRela> modelRelas;

    public Set<Long> getModelClusterSet() {
        if (CollectionUtils.isEmpty(this.modelRelas)) {
            return Sets.newHashSet();
        } else {
            Set<Long> modelClusterSet = new HashSet();
            this.modelRelas.forEach((modelRela) -> {
                modelClusterSet.add(modelRela.getToModelId());
                modelClusterSet.add(modelRela.getFromModelId());
            });
            return modelClusterSet;
        }
    }
}
