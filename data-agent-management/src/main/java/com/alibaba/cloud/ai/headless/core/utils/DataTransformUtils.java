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
package com.alibaba.cloud.ai.headless.core.utils;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.pojo.DateConf;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** transform query results to return the users */
public class DataTransformUtils {

    public static List<Map<String, Object>> transform(List<Map<String, Object>> originalData,
            String metric, List<String> groups, DateConf dateConf) {
        List<String> dateList = dateConf.getDateList();
        List<Map<String, Object>> transposedData = new ArrayList<>();
        for (Map<String, Object> originalRow : originalData) {
            Map<String, Object> transposedRow = new HashMap<>();
            for (String key : originalRow.keySet()) {
                if (groups.contains(key)) {
                    transposedRow.put(key, originalRow.get(key));
                }
            }
            transposedRow.put(String.valueOf(originalRow.get(getTimeDimension(dateConf))),
                    originalRow.get(metric));
            transposedData.add(transposedRow);
        }
        Map<String, List<Map<String, Object>>> dataMerge = transposedData.stream()
                .collect(Collectors.groupingBy(row -> getRowKey(row, groups)));
        List<Map<String, Object>> resultData = Lists.newArrayList();
        for (List<Map<String, Object>> data : dataMerge.values()) {
            Map<String, Object> rowData = new HashMap<>();
            for (Map<String, Object> row : data) {
                for (String key : row.keySet()) {
                    rowData.put(key, row.get(key));
                }
            }
            for (String date : dateList) {
                if (!rowData.containsKey(date)) {
                    rowData.put(date, "");
                }
            }
            resultData.add(rowData);
        }
        return resultData;
    }

    private static String getRowKey(Map<String, Object> originalRow, List<String> groups) {
        List<Object> values = Lists.newArrayList();
        for (String key : originalRow.keySet()) {
            if (groups.contains(key)) {
                values.add(originalRow.get(key));
            }
        }
        return StringUtils.join(values, "_");
    }

    private static String getTimeDimension(DateConf dateConf) {
        return dateConf.getDateField();
    }
}
