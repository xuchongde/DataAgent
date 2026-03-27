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
package com.alibaba.cloud.ai.headless.server.service;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.DBColumn;
import com.alibaba.cloud.ai.headless.api.pojo.enums.DataType;
import com.alibaba.cloud.ai.headless.api.pojo.request.DatabaseReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.ModelBuildReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.SqlExecuteReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DatabaseResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SemanticQueryResp;
import com.alibaba.cloud.ai.headless.server.pojo.DatabaseParameter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseService {

    SemanticQueryResp executeSql(String sql, DatabaseResp databaseResp);

    List<DatabaseResp> getDatabaseByType(DataType dataType);

    SemanticQueryResp executeSql(SqlExecuteReq sqlExecuteReq, User user);

    DatabaseResp getDatabase(Long id, User user);

    DatabaseResp getDatabase(Long id);

    Map<String, List<DatabaseParameter>> getDatabaseParameters(User user);

    boolean testConnect(DatabaseReq databaseReq, User user);

    DatabaseResp createOrUpdateDatabase(DatabaseReq databaseReq, User user);

    List<DatabaseResp> getDatabaseList(User user);

    void deleteDatabase(Long databaseId, User user);

    List<String> getCatalogs(Long id) throws SQLException;

    List<String> getDbNames(Long id, String catalog) throws SQLException;

    List<String> getTables(Long id, String catalog, String db) throws SQLException;

    Map<String, List<DBColumn>> getDbColumns(ModelBuildReq modelBuildReq) throws SQLException;

    List<DBColumn> getColumns(Long id, String catalog, String db, String table) throws SQLException;

    List<DBColumn> getColumns(Long id, String sql) throws SQLException;
}
