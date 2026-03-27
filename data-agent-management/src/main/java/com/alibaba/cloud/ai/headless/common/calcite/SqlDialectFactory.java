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
package com.alibaba.cloud.ai.headless.common.calcite;

import com.alibaba.cloud.ai.headless.common.pojo.enums.EngineType;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlDialect.Context;
import org.apache.calcite.sql.SqlDialect.DatabaseProduct;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SqlDialectFactory {

    public static final Context DEFAULT_CONTEXT =
            SqlDialect.EMPTY_CONTEXT.withDatabaseProduct(DatabaseProduct.BIG_QUERY)
                    .withLiteralQuoteString("'").withLiteralEscapedQuoteString("''")
                    .withIdentifierQuoteString("`").withUnquotedCasing(Casing.UNCHANGED)
                    .withQuotedCasing(Casing.UNCHANGED).withCaseSensitive(false);
    public static final Context POSTGRESQL_CONTEXT = SqlDialect.EMPTY_CONTEXT
            .withDatabaseProduct(DatabaseProduct.BIG_QUERY).withLiteralQuoteString("'")
            .withLiteralEscapedQuoteString("''").withUnquotedCasing(Casing.UNCHANGED)
            .withQuotedCasing(Casing.UNCHANGED).withCaseSensitive(false);
    public static final Context HANADB_CONTEXT =
            SqlDialect.EMPTY_CONTEXT.withDatabaseProduct(DatabaseProduct.BIG_QUERY)
                    .withLiteralQuoteString("'").withIdentifierQuoteString("\"")
                    .withLiteralEscapedQuoteString("''").withUnquotedCasing(Casing.UNCHANGED)
                    .withQuotedCasing(Casing.UNCHANGED).withCaseSensitive(true);
    public static final Context PRESTO_CONTEXT =
            SqlDialect.EMPTY_CONTEXT.withDatabaseProduct(DatabaseProduct.PRESTO)
                    .withLiteralQuoteString("'").withIdentifierQuoteString("\"")
                    .withLiteralEscapedQuoteString("''").withUnquotedCasing(Casing.UNCHANGED)
                    .withQuotedCasing(Casing.UNCHANGED).withCaseSensitive(true);
    public static final Context KYUUBI_CONTEXT =
            SqlDialect.EMPTY_CONTEXT.withDatabaseProduct(DatabaseProduct.BIG_QUERY)
                    .withLiteralQuoteString("'").withIdentifierQuoteString("`")
                    .withLiteralEscapedQuoteString("''").withUnquotedCasing(Casing.UNCHANGED)
                    .withQuotedCasing(Casing.UNCHANGED).withCaseSensitive(false);
    private static Map<EngineType, SemanticSqlDialect> sqlDialectMap;

    static {
        sqlDialectMap = new HashMap<>();
        sqlDialectMap.put(EngineType.CLICKHOUSE, new SemanticSqlDialect(DEFAULT_CONTEXT));
        sqlDialectMap.put(EngineType.MYSQL, new SemanticSqlDialect(DEFAULT_CONTEXT));
        sqlDialectMap.put(EngineType.H2, new SemanticSqlDialect(DEFAULT_CONTEXT));
        sqlDialectMap.put(EngineType.POSTGRESQL, new SemanticSqlDialect(POSTGRESQL_CONTEXT));
        sqlDialectMap.put(EngineType.HANADB, new SemanticSqlDialect(HANADB_CONTEXT));
        sqlDialectMap.put(EngineType.STARROCKS, new SemanticSqlDialect(DEFAULT_CONTEXT));
        sqlDialectMap.put(EngineType.KYUUBI, new SemanticSqlDialect(KYUUBI_CONTEXT));
        sqlDialectMap.put(EngineType.PRESTO, new SemanticSqlDialect(PRESTO_CONTEXT));
        sqlDialectMap.put(EngineType.TRINO, new SemanticSqlDialect(PRESTO_CONTEXT));
    }

    public static SemanticSqlDialect getSqlDialect(EngineType engineType) {
        SemanticSqlDialect semanticSqlDialect = sqlDialectMap.get(engineType);
        if (Objects.isNull(semanticSqlDialect)) {
            return new SemanticSqlDialect(DEFAULT_CONTEXT);
        }
        return semanticSqlDialect;
    }
}
