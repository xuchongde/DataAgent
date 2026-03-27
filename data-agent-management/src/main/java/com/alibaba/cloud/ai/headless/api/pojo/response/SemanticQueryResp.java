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
package com.alibaba.cloud.ai.headless.api.pojo.response;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.pojo.QueryAuthorization;
import com.alibaba.cloud.ai.headless.common.pojo.QueryColumn;
import com.alibaba.cloud.ai.headless.common.util.StringUtil;
import com.alibaba.cloud.ai.headless.api.pojo.QueryResult;
import com.alibaba.cloud.ai.headless.api.pojo.enums.SemanticType;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString
public class SemanticQueryResp extends QueryResult<Map<String, Object>> {

    List<QueryColumn> columns = Lists.newArrayList();
    String sql;
    QueryAuthorization queryAuthorization;
    boolean useCache;
    private String errorMsg;

    public List<QueryColumn> getMetricColumns() {
        return columns.stream()
                .filter(queryColumn -> SemanticType.NUMBER.name().equals(queryColumn.getShowType()))
                .collect(Collectors.toList());
    }

    public List<QueryColumn> getDimensionColumns() {
        return columns.stream().filter(
                queryColumn -> !SemanticType.NUMBER.name().equals(queryColumn.getShowType()))
                .collect(Collectors.toList());
    }

    public void appendErrorMsg(String msg) {
        errorMsg = StringUtil.append(errorMsg, msg);
    }
}
