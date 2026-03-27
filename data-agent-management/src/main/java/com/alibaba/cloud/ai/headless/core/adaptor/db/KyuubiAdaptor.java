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
import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import com.alibaba.cloud.ai.headless.common.pojo.enums.TimeDimensionEnum;
import com.alibaba.cloud.ai.headless.core.pojo.ConnectInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class KyuubiAdaptor extends BaseDbAdaptor {

    /** transform YYYYMMDD to YYYY-MM-DD YYYY-MM YYYY-MM-DD(MONDAY) */
    @Override
    public String getDateFormat(String dateType, String dateFormat, String column) {
        if (dateFormat.equalsIgnoreCase(Constants.DAY_FORMAT_INT)) {
            if (TimeDimensionEnum.MONTH.name().equalsIgnoreCase(dateType)) {
                return String.format("date_format(%s, 'yyyy-MM')", column);
            } else if (TimeDimensionEnum.WEEK.name().equalsIgnoreCase(dateType)) {
                return String.format("date_format(date_sub(%s, (dayofweek(%s) - 2)), 'yyyy-MM-dd')",
                        column, column);
            } else {
                return String.format(
                        "date_format(to_date(cast(%s as string), 'yyyyMMdd'), 'yyyy-MM-dd')",
                        column);
            }
        } else if (dateFormat.equalsIgnoreCase(Constants.DAY_FORMAT)) {
            if (TimeDimensionEnum.MONTH.name().equalsIgnoreCase(dateType)) {
                return String.format("date_format(%s, 'yyyy-MM')", column);
            } else if (TimeDimensionEnum.WEEK.name().equalsIgnoreCase(dateType)) {
                return String.format("date_format(date_sub(%s, (dayofweek(%s) - 2)), 'yyyy-MM-dd')",
                        column, column);
            } else {
                return column;
            }
        }
        return column;
    }

    @Override
    public List<String> getDBs(ConnectInfo connectionInfo, String catalog) throws SQLException {
        List<String> dbs = Lists.newArrayList();
        final StringBuilder sql = new StringBuilder("SHOW DATABASES");
        if (StringUtils.isNotBlank(catalog)) {
            sql.append(" IN ").append(catalog);
        }
        try (Connection con = getConnection(connectionInfo);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql.toString())) {
            while (rs.next()) {
                dbs.add(rs.getString(1));
            }
        }
        return dbs;
    }

    @Override
    public List<String> getTables(ConnectInfo connectInfo, String catalog, String schemaName)
            throws SQLException {
        List<String> tablesAndViews = new ArrayList<>();

        try {
            try (ResultSet resultSet = getDatabaseMetaData(connectInfo).getTables(catalog,
                    schemaName, null, new String[] {"TABLE", "VIEW"})) {
                while (resultSet.next()) {
                    String name = resultSet.getString("TABLE_NAME");
                    tablesAndViews.add(name);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to get tables and views", e);
        }
        return tablesAndViews;
    }

    @Override
    public String rewriteSql(String sql) {
        return sql;
    }
}
