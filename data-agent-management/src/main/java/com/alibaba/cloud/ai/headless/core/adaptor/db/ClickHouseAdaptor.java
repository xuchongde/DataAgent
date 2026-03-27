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

import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlReplaceHelper;
import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import com.alibaba.cloud.ai.headless.common.pojo.enums.TimeDimensionEnum;

import java.util.HashMap;
import java.util.Map;

public class ClickHouseAdaptor extends BaseDbAdaptor {

    @Override
    public String getDateFormat(String dateType, String dateFormat, String column) {
        if (dateFormat.equalsIgnoreCase(Constants.DAY_FORMAT_INT)) {
            if (TimeDimensionEnum.MONTH.name().equalsIgnoreCase(dateType)) {
                return "toYYYYMM(toDate(parseDateTimeBestEffort(toString(%s))))".replace("%s",
                        column);
            } else if (TimeDimensionEnum.WEEK.name().equalsIgnoreCase(dateType)) {
                return "toMonday(toDate(parseDateTimeBestEffort(toString(%s))))".replace("%s",
                        column);
            } else {
                return "toDate(parseDateTimeBestEffort(toString(%s)))".replace("%s", column);
            }
        } else if (dateFormat.equalsIgnoreCase(Constants.DAY_FORMAT)) {
            if (TimeDimensionEnum.MONTH.name().equalsIgnoreCase(dateType)) {
                return "formatDateTime(toDate(%s),'%Y-%m')".replace("%s", column);
            } else if (TimeDimensionEnum.WEEK.name().equalsIgnoreCase(dateType)) {
                return "toMonday(toDate(%s))".replace("%s", column);
            } else {
                return column;
            }
        }
        return column;
    }

    @Override
    public String rewriteSql(String sql) {
        Map<String, String> functionMap = new HashMap<>();
        functionMap.put("MONTH".toLowerCase(), "toMonth");
        functionMap.put("DAY".toLowerCase(), "toDayOfMonth");
        functionMap.put("YEAR".toLowerCase(), "toYear");
        return SqlReplaceHelper.replaceFunction(sql, functionMap);
    }
}
