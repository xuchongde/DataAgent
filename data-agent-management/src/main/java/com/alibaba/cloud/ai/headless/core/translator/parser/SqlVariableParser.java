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

import com.alibaba.cloud.ai.headless.api.pojo.enums.ModelDefineType;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SemanticSchemaResp;
import com.alibaba.cloud.ai.headless.core.pojo.QueryStatement;
import com.alibaba.cloud.ai.headless.core.utils.SqlVariableParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component("SqlVariableParser")
public class SqlVariableParser implements QueryParser {

    @Override
    public boolean accept(QueryStatement queryStatement) {
        return Objects.nonNull(queryStatement.getStructQuery()) && !queryStatement.getIsS2SQL();
    }

    @Override
    public void parse(QueryStatement queryStatement) {
        SemanticSchemaResp semanticSchemaResp = queryStatement.getSemanticSchema();
        List<ModelResp> modelResps = semanticSchemaResp.getModelResps();
        if (CollectionUtils.isEmpty(modelResps)) {
            return;
        }
        for (ModelResp modelResp : modelResps) {
            if (ModelDefineType.SQL_QUERY.getName()
                    .equalsIgnoreCase(modelResp.getModelDetail().getQueryType())) {
                String sqlParsed =
                        SqlVariableParseUtils.parse(modelResp.getModelDetail().getSqlQuery(),
                                modelResp.getModelDetail().getSqlVariables(),
                                queryStatement.getStructQuery().getParams());
                ModelResp dataModel =
                        queryStatement.getOntology().getModelMap().get(modelResp.getBizName());
                dataModel.getModelDetail().setSqlQuery(sqlParsed);
            }
        }
    }
}
