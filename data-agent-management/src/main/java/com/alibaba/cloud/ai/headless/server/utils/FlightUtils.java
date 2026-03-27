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
package com.alibaba.cloud.ai.headless.server.utils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Pattern;

/** tools for arrow flight sql */
public class FlightUtils {

    public static int resolveType(Object value) {
        if (value instanceof Long) {
            return Types.BIGINT;
        }
        if (value instanceof Integer) {
            return Types.INTEGER;
        }
        if (value instanceof Double) {
            return Types.DOUBLE;
        }
        if (value instanceof String) {
            String val = String.valueOf(value);
            if (Pattern.matches("^\\d+$", val)) {
                return Types.BIGINT;
            } else if (Pattern.matches("^\\d+\\.\\d+$", val)) {
                return Types.DECIMAL;
            } else if (Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", val)) {
                return Types.DATE;
            } else if (Pattern.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", val)) {
                return Types.TIME;
            }
        }
        return Types.VARCHAR;
    }

    public static int isNullable(int sqlType) throws SQLException {
        switch (sqlType) {
            case Types.VARCHAR:
            case Types.DECIMAL:
                return ResultSetMetaData.columnNullable;
            default:
                return ResultSetMetaData.columnNullableUnknown;
        }
    }
}
