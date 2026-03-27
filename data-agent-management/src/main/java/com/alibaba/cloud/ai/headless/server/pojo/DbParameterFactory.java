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
package com.alibaba.cloud.ai.headless.server.pojo;

import com.alibaba.cloud.ai.headless.common.pojo.enums.EngineType;

import java.util.LinkedHashMap;
import java.util.Map;

public class DbParameterFactory {

    private static Map<String, DbParametersBuilder> parametersBuilder;

    static {
        parametersBuilder = new LinkedHashMap<>();
        parametersBuilder.put(EngineType.H2.getName(), new H2ParametersBuilder());
        parametersBuilder.put(EngineType.CLICKHOUSE.getName(), new ClickHouseParametersBuilder());
        parametersBuilder.put(EngineType.MYSQL.getName(), new MysqlParametersBuilder());
        parametersBuilder.put(EngineType.POSTGRESQL.getName(), new PostgresqlParametersBuilder());
        parametersBuilder.put(EngineType.HANADB.getName(), new HanadbParametersBuilder());
        parametersBuilder.put(EngineType.STARROCKS.getName(), new StarrocksParametersBuilder());
        parametersBuilder.put(EngineType.KYUUBI.getName(), new KyuubiParametersBuilder());
        parametersBuilder.put(EngineType.PRESTO.getName(), new PrestoParametersBuilder());
        parametersBuilder.put(EngineType.TRINO.getName(), new TrinoParametersBuilder());
        parametersBuilder.put(EngineType.OTHER.getName(), new OtherParametersBuilder());
    }

    public static DbParametersBuilder get(String engineType) {
        return parametersBuilder.get(engineType);
    }

    public static Map<String, DbParametersBuilder> getMap() {
        return parametersBuilder;
    }
}
