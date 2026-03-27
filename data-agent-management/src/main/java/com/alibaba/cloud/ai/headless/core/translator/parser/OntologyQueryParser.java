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
package com.alibaba.cloud.ai.headless.core.translator.parser;

import com.alibaba.cloud.ai.headless.core.pojo.Ontology;
import com.alibaba.cloud.ai.headless.core.pojo.QueryStatement;
import com.alibaba.cloud.ai.headless.core.translator.parser.calcite.S2CalciteSchema;
import com.alibaba.cloud.ai.headless.core.translator.parser.calcite.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * This parser generates inner sql statement for the ontology query, which would be selected by the
 * parsed sql query.
 */
@Component("OntologyQueryParser")
@Slf4j
public class OntologyQueryParser implements QueryParser {

    @Override
    public boolean accept(QueryStatement queryStatement) {
        return Objects.nonNull(queryStatement.getOntologyQuery());
    }

    @Override
    public void parse(QueryStatement queryStatement) throws Exception {
        Ontology ontology = queryStatement.getOntology();
        S2CalciteSchema semanticSchema = S2CalciteSchema.builder()
                .schemaKey("DATASET_" + queryStatement.getDataSetId()).ontology(ontology)
                .runtimeOptions(RuntimeOptions.builder().minMaxTime(queryStatement.getMinMaxTime())
                        .enableOptimize(queryStatement.getEnableOptimize()).build())
                .build();
        SqlBuilder sqlBuilder = new SqlBuilder(semanticSchema);
        String sql = sqlBuilder.buildOntologySql(queryStatement);
        queryStatement.getOntologyQuery().setSql(sql);
    }

}
