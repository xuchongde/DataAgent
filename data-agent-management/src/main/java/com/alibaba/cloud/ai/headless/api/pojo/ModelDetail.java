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
package com.alibaba.cloud.ai.headless.api.pojo;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.api.pojo.enums.DimensionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelDetail {

    private String queryType;

    private String dbType;

    private String sqlQuery;

    private String tableQuery;

    private String filterSql;

    private List<Identify> identifiers = Lists.newArrayList();

    private List<Dimension> dimensions = Lists.newArrayList();

    private List<Measure> measures = Lists.newArrayList();

    private List<Field> fields = Lists.newArrayList();

    private List<SqlVariable> sqlVariables = Lists.newArrayList();

    public String getSqlQuery() {
        if (StringUtils.isNotBlank(sqlQuery) && sqlQuery.endsWith(";")) {
            sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 1);
        }
        return sqlQuery;
    }

    public List<Dimension> filterTimeDims() {
        if (CollectionUtils.isEmpty(dimensions)) {
            return Lists.newArrayList();
        }
        return dimensions.stream().filter(dim -> DimensionType.partition_time.equals(dim.getType()))
                .collect(Collectors.toList());
    }

}
