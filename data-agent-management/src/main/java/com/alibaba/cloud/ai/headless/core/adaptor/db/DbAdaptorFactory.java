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

import com.alibaba.cloud.ai.headless.common.pojo.enums.EngineType;

import java.util.HashMap;
import java.util.Map;

public class DbAdaptorFactory {

    private static Map<String, DbAdaptor> dbAdaptorMap;

    static {
        dbAdaptorMap = new HashMap<>();
        dbAdaptorMap.put(EngineType.CLICKHOUSE.getName(), new ClickHouseAdaptor());
        dbAdaptorMap.put(EngineType.MYSQL.getName(), new MysqlAdaptor());
        dbAdaptorMap.put(EngineType.H2.getName(), new H2Adaptor());
        dbAdaptorMap.put(EngineType.POSTGRESQL.getName(), new PostgresqlAdaptor());
        dbAdaptorMap.put(EngineType.OTHER.getName(), new DefaultDbAdaptor());
        dbAdaptorMap.put(EngineType.DUCKDB.getName(), new DuckdbAdaptor());
        dbAdaptorMap.put(EngineType.HANADB.getName(), new HanadbAdaptor());
        dbAdaptorMap.put(EngineType.STARROCKS.getName(), new StarrocksAdaptor());
        dbAdaptorMap.put(EngineType.KYUUBI.getName(), new KyuubiAdaptor());
        dbAdaptorMap.put(EngineType.PRESTO.getName(), new PrestoAdaptor());
        dbAdaptorMap.put(EngineType.TRINO.getName(), new TrinoAdaptor());
        dbAdaptorMap.put(EngineType.ORACLE.getName(), new OracleAdaptor());
    }

    public static DbAdaptor getEngineAdaptor(String engineType) {
        return dbAdaptorMap.get(engineType.toUpperCase());
    }
}
