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
package com.alibaba.cloud.ai.headless.core.translator.parser.calcite;

import com.alibaba.cloud.ai.headless.api.pojo.response.DimSchemaResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.MetricSchemaResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import com.alibaba.cloud.ai.headless.core.pojo.JoinRelation;
import com.alibaba.cloud.ai.headless.core.pojo.Ontology;
import com.alibaba.cloud.ai.headless.core.translator.parser.RuntimeOptions;
import lombok.Builder;
import lombok.Data;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaVersion;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class S2CalciteSchema extends AbstractSchema {

    private String schemaKey;

    private Ontology ontology;

    private RuntimeOptions runtimeOptions;

    @Override
    public Schema snapshot(SchemaVersion version) {
        return this;
    }

    public Map<String, ModelResp> getDataModels() {
        return ontology.getModelMap();
    }

    public List<MetricSchemaResp> getMetrics() {
        return ontology.getMetrics();
    }

    public Map<String, List<DimSchemaResp>> getDimensions() {
        return ontology.getDimensionMap();
    }

    public List<JoinRelation> getJoinRelations() {
        return ontology.getJoinRelations();
    }

}
