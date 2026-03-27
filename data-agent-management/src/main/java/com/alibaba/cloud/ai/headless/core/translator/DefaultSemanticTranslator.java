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
package com.alibaba.cloud.ai.headless.core.translator;

import com.alibaba.cloud.ai.headless.common.calcite.SqlMergeWithUtils;
import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlSelectHelper;
import com.alibaba.cloud.ai.headless.common.pojo.enums.EngineType;
import com.alibaba.cloud.ai.headless.api.pojo.response.QueryState;
import com.alibaba.cloud.ai.headless.core.pojo.OntologyQuery;
import com.alibaba.cloud.ai.headless.core.pojo.QueryStatement;
import com.alibaba.cloud.ai.headless.core.pojo.SqlQuery;
import com.alibaba.cloud.ai.headless.core.translator.optimizer.QueryOptimizer;
import com.alibaba.cloud.ai.headless.core.translator.parser.QueryParser;
import com.alibaba.cloud.ai.headless.core.utils.ComponentFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultSemanticTranslator implements SemanticTranslator {

    public void translate(QueryStatement queryStatement) throws Exception {
        if (queryStatement.isTranslated()) {
            return;
        }
        for (QueryParser parser : ComponentFactory.getQueryParsers()) {
            if (parser.accept(queryStatement)) {
                log.debug("QueryConverter accept [{}]", parser.getClass().getName());
                parser.parse(queryStatement);
                if (!queryStatement.getStatus().equals(QueryState.SUCCESS)) {
                    break;
                }
            }
        }
        mergeOntologyQuery(queryStatement);

        if (StringUtils.isNotBlank(queryStatement.getSqlQuery().getSimplifiedSql())) {
            queryStatement.setSql(queryStatement.getSqlQuery().getSimplifiedSql());
        }
        if (StringUtils.isBlank(queryStatement.getSql())) {
            throw new RuntimeException("parse exception: " + queryStatement.getErrMsg());
        }

        for (QueryOptimizer optimizer : ComponentFactory.getQueryOptimizers()) {
            if (optimizer.accept(queryStatement)) {
                optimizer.rewrite(queryStatement);
            }
        }
        log.debug("translated query SQL: [{}]",
                StringUtils.normalizeSpace(queryStatement.getSql()));
    }

    private void mergeOntologyQuery(QueryStatement queryStatement) throws Exception {
        OntologyQuery ontologyQuery = queryStatement.getOntologyQuery();
        if (Objects.isNull(ontologyQuery) || StringUtils.isBlank(ontologyQuery.getSql())) {
            throw new Exception(String.format("parse ontology sql [%s] error [%s]",
                    StringUtils.normalizeSpace(queryStatement.getSqlQuery().getSql()),
                    queryStatement.getErrMsg()));
        }
        log.info("parse with ontologyQuery fields: [{}]", ontologyQuery.getFields());

        SqlQuery sqlQuery = queryStatement.getSqlQuery();
        String ontologyOuterSql = sqlQuery.getSql();
        String ontologyInnerTable = sqlQuery.getTable();
        String ontologyInnerSql = ontologyQuery.getSql();

        List<Pair<String, String>> tables = new ArrayList<>();
        tables.add(Pair.of(ontologyInnerTable, ontologyInnerSql));
        String finalSql = null;
        if (sqlQuery.isSupportWith()) {
            EngineType engineType = queryStatement.getOntology().getDatabaseType();
            if (!SqlSelectHelper.hasWith(ontologyOuterSql)) {
                finalSql = "with " + tables.stream()
                        .map(t -> String.format("%s as (%s)", t.getLeft(), t.getRight()))
                        .collect(Collectors.joining(",")) + "\n" + ontologyOuterSql;
            } else {
                List<String> withTableList =
                        tables.stream().map(Pair::getLeft).collect(Collectors.toList());
                List<String> withSqlList =
                        tables.stream().map(Pair::getRight).collect(Collectors.toList());
                finalSql = SqlMergeWithUtils.mergeWith(engineType, ontologyOuterSql, withSqlList,
                        withTableList);
            }
        } else {
            for (Pair<String, String> tb : tables) {
                finalSql = StringUtils.replace(ontologyOuterSql, tb.getLeft(),
                        "(" + tb.getRight() + ") " + (sqlQuery.isWithAlias() ? "" : tb.getLeft()),
                        -1);
            }
        }
        queryStatement.setSql(finalSql);
    }

}
