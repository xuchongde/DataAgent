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

import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlReplaceHelper;
import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlSelectHelper;
import com.alibaba.cloud.ai.headless.api.pojo.response.DimSchemaResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SemanticSchemaResp;
import com.alibaba.cloud.ai.headless.core.pojo.OntologyQuery;
import com.alibaba.cloud.ai.headless.core.pojo.QueryStatement;
import com.alibaba.cloud.ai.headless.core.pojo.SqlQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This parser replaces dimension bizName in the S2SQL with calculation expression (if configured).
 */
@Component("DimExpressionParser")
@Slf4j
public class DimExpressionParser implements QueryParser {
    @Override
    public boolean accept(QueryStatement queryStatement) {
        return Objects.nonNull(queryStatement.getSqlQuery())
                && Objects.nonNull(queryStatement.getOntologyQuery())
                && StringUtils.isNotBlank(queryStatement.getSqlQuery().getSql())
                && !CollectionUtils.isEmpty(queryStatement.getOntologyQuery().getDimensions());
    }

    @Override
    public void parse(QueryStatement queryStatement) throws Exception {

        SemanticSchemaResp semanticSchema = queryStatement.getSemanticSchema();
        SqlQuery sqlQuery = queryStatement.getSqlQuery();
        OntologyQuery ontologyQuery = queryStatement.getOntologyQuery();

        Map<String, String> bizName2Expr = getDimensionExpressions(semanticSchema, ontologyQuery);
        if (!CollectionUtils.isEmpty(bizName2Expr)) {
            String sql = SqlReplaceHelper.replaceSqlByExpression(sqlQuery.getTable(),
                    sqlQuery.getSql(), bizName2Expr);
            sqlQuery.setSql(sql);
        }
    }

    private Map<String, String> getDimensionExpressions(SemanticSchemaResp semanticSchema,
            OntologyQuery ontologyQuery) {

        Set<DimSchemaResp> queryDimensions = ontologyQuery.getDimensions();
        Set<String> queryFields = ontologyQuery.getFields();
        log.debug("begin to generateDerivedMetric {} [{}]", queryDimensions);

        Map<String, String> dim2Expr = new HashMap<>();
        for (DimSchemaResp queryDim : queryDimensions) {
            queryDim.getFields().addAll(SqlSelectHelper.getFieldsFromExpr(queryDim.getExpr()));
            queryFields.addAll(queryDim.getFields());
            if (!queryDim.getBizName().equals(queryDim.getExpr())) {
                dim2Expr.put(queryDim.getBizName(), queryDim.getExpr());
            }
        }

        return dim2Expr;
    }

}
