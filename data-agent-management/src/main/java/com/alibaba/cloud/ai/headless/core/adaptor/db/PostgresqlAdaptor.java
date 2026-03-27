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
package com.alibaba.cloud.ai.headless.core.adaptor.db;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlReplaceHelper;
import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import com.alibaba.cloud.ai.headless.common.pojo.enums.TimeDimensionEnum;
import com.alibaba.cloud.ai.headless.api.pojo.DBColumn;
import com.alibaba.cloud.ai.headless.api.pojo.enums.FieldType;
import com.alibaba.cloud.ai.headless.core.pojo.ConnectInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

@Slf4j
public class PostgresqlAdaptor extends BaseDbAdaptor {

    @Override
    public String getDateFormat(String dateType, String dateFormat, String column) {
        if (dateFormat.equalsIgnoreCase(Constants.DAY_FORMAT_INT)) {
            if (TimeDimensionEnum.MONTH.name().equalsIgnoreCase(dateType)) {
                return "to_char(to_date(%s,'yyyymmdd'), 'yyyy-mm')".replace("%s", column);
            } else if (TimeDimensionEnum.WEEK.name().equalsIgnoreCase(dateType)) {
                return "to_char(date_trunc('week',to_date(%s, 'yyyymmdd')),'yyyy-mm-dd')"
                        .replace("%s", column);
            } else {
                return "to_char(to_date(%s,'yyyymmdd'), 'yyyy-mm-dd')".replace("%s", column);
            }
        } else if (dateFormat.equalsIgnoreCase(Constants.DAY_FORMAT)) {
            if (TimeDimensionEnum.MONTH.name().equalsIgnoreCase(dateType)) {
                return "to_char(to_date(%s,'yyyy-mm-dd'), 'yyyy-mm')".replace("%s", column);
            } else if (TimeDimensionEnum.WEEK.name().equalsIgnoreCase(dateType)) {
                return "to_char(date_trunc('week',to_date(%s, 'yyyy-mm-dd')),'yyyy-mm-dd')"
                        .replace("%s", column);
            } else {
                return column;
            }
        }
        return column;
    }

    @Override
    public String rewriteSql(String sql) {
        Map<String, String> functionMap = new HashMap<>();
        functionMap.put("MONTH".toLowerCase(), "TO_CHAR");
        functionMap.put("DAY".toLowerCase(), "TO_CHAR");
        functionMap.put("YEAR".toLowerCase(), "TO_CHAR");
        Map<String, UnaryOperator> functionCall = new HashMap<>();
        functionCall.put("MONTH".toLowerCase(), o -> {
            if (Objects.nonNull(o) && o instanceof ExpressionList) {
                ExpressionList expressionList = (ExpressionList) o;
                expressionList.add(new StringValue("MM"));
                return expressionList;
            }
            return o;
        });
        functionCall.put("DAY".toLowerCase(), o -> {
            if (Objects.nonNull(o) && o instanceof ExpressionList) {
                ExpressionList expressionList = (ExpressionList) o;
                expressionList.add(new StringValue("dd"));
                return expressionList;
            }
            return o;
        });
        functionCall.put("YEAR".toLowerCase(), o -> {
            if (Objects.nonNull(o) && o instanceof ExpressionList) {
                ExpressionList expressionList = (ExpressionList) o;
                expressionList.add(new StringValue("YYYY"));
                return expressionList;
            }
            return o;
        });
        sql = SqlReplaceHelper.replaceFunction(sql, functionMap, functionCall);
        sql = sql.replaceAll("`", "\"");
        return sql;
    }

    public List<String> getTables(ConnectInfo connectionInfo, String schemaName)
            throws SQLException {
        List<String> tablesAndViews = Lists.newArrayList();
        DatabaseMetaData metaData = getDatabaseMetaData(connectionInfo);
        try (ResultSet resultSet =
                metaData.getTables(null, schemaName, null, new String[] {"TABLE", "VIEW"})) {
            while (resultSet.next()) {
                String name = resultSet.getString("TABLE_NAME");
                tablesAndViews.add(name);
            }
        } catch (SQLException e) {
            log.error("Failed to get tables and views", e);
        }
        return tablesAndViews;
    }

    public List<DBColumn> getColumns(ConnectInfo connectInfo, String catalog, String schemaName,
            String tableName) throws SQLException {
        List<DBColumn> dbColumns = Lists.newArrayList();
        DatabaseMetaData metaData = getDatabaseMetaData(connectInfo);
        ResultSet columns = metaData.getColumns(null, schemaName, tableName, null);
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String dataType = columns.getString("TYPE_NAME");
            String remarks = columns.getString("REMARKS");
            FieldType fieldType = classifyColumnType(dataType);
            dbColumns.add(new DBColumn(columnName, dataType, remarks, fieldType));
        }
        return dbColumns;
    }

    @Override
    public FieldType classifyColumnType(String typeName) {
        switch (typeName.toUpperCase()) {
            case "INT":
            case "INTEGER":
            case "BIGINT":
            case "SMALLINT":
            case "SERIAL":
            case "BIGSERIAL":
            case "SMALLSERIAL":
            case "REAL":
            case "DOUBLE PRECISION":
            case "NUMERIC":
            case "DECIMAL":
                return FieldType.measure;
            case "DATE":
            case "TIME":
            case "TIMESTAMP":
            case "TIMESTAMPTZ":
            case "INTERVAL":
                return FieldType.time;
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
            case "CHARACTER VARYING":
            case "CHARACTER":
            case "UUID":
            default:
                return FieldType.categorical;
        }
    }

}
