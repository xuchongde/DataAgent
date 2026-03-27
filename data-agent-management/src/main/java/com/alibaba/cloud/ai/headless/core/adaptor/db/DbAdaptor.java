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

import com.alibaba.cloud.ai.headless.api.pojo.DBColumn;
import com.alibaba.cloud.ai.headless.api.pojo.enums.FieldType;
import com.alibaba.cloud.ai.headless.core.pojo.ConnectInfo;

import java.sql.SQLException;
import java.util.List;

/** Adapters for different query engines to obtain table, field, and time formatting methods */
public interface DbAdaptor {

    String getDateFormat(String dateType, String dateFormat, String column);

    String rewriteSql(String sql);

    List<String> getCatalogs(ConnectInfo connectInfo) throws SQLException;

    List<String> getDBs(ConnectInfo connectInfo, String catalog) throws SQLException;

    List<String> getTables(ConnectInfo connectInfo, String catalog, String schemaName)
            throws SQLException;

    List<DBColumn> getColumns(ConnectInfo connectInfo, String catalog, String schemaName,
            String tableName) throws SQLException;

    FieldType classifyColumnType(String typeName);
}
