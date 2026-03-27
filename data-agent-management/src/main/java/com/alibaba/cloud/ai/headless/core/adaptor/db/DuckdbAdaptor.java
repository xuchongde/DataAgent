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
import com.alibaba.cloud.ai.headless.api.pojo.DBColumn;
import com.alibaba.cloud.ai.headless.api.pojo.enums.FieldType;
import com.alibaba.cloud.ai.headless.core.pojo.ConnectInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Slf4j
public class DuckdbAdaptor extends DefaultDbAdaptor {

    protected ResultSet getResultSet(String schemaName, DatabaseMetaData metaData)
            throws SQLException {
        return metaData.getTables(schemaName, null, null, new String[] {"TABLE", "VIEW"});
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
    public String rewriteSql(String sql) {
        if (sql == null) {
            return null;
        }
        return sql.replaceAll("`", "");
    }

    @Override
    public Properties getProperties(ConnectInfo connectionInfo) {
        return new Properties();
    }

}
