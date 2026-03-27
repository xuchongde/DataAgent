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
package com.alibaba.cloud.ai.headless.server.modeller;

import com.alibaba.cloud.ai.headless.api.pojo.DBColumn;
import com.alibaba.cloud.ai.headless.api.pojo.DbSchema;
import com.alibaba.cloud.ai.headless.api.pojo.ModelSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticColumn;
import com.alibaba.cloud.ai.headless.api.pojo.request.ModelBuildReq;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RuleSemanticModeller implements SemanticModeller {

    @Override
    public void build(DbSchema dbSchema, List<DbSchema> dbSchemas, ModelSchema modelSchema,
            ModelBuildReq modelBuildReq) {
        List<SemanticColumn> semanticColumns =
                dbSchema.getDbColumns().stream().map(this::convert).collect(Collectors.toList());
        modelSchema.setSemanticColumns(semanticColumns);
    }

    private SemanticColumn convert(DBColumn dbColumn) {
        SemanticColumn semanticColumn = new SemanticColumn();
        semanticColumn.setName(dbColumn.getColumnName());
        semanticColumn.setColumnName(dbColumn.getColumnName());
        semanticColumn.setExpr(dbColumn.getColumnName());
        semanticColumn.setComment(dbColumn.getComment());
        semanticColumn.setDataType(dbColumn.getDataType());
        semanticColumn.setFiledType(dbColumn.getFieldType());
        return semanticColumn;
    }

}
