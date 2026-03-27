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
package com.alibaba.cloud.ai.headless.chat.parser;

import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlSelectFunctionHelper;
import com.alibaba.cloud.ai.headless.common.pojo.enums.QueryType;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import lombok.extern.slf4j.Slf4j;

/** QueryTypeParser resolves query type as either AGGREGATE or DETAIL */
@Slf4j
public class QueryTypeParser implements SemanticParser {

    @Override
    public void parse(ChatQueryContext chatQueryContext) {
        chatQueryContext.getCandidateQueries().forEach(query -> {
            SemanticParseInfo parseInfo = query.getParseInfo();
            String s2SQL = parseInfo.getSqlInfo().getParsedS2SQL();
            QueryType queryType = QueryType.DETAIL;

            if (SqlSelectFunctionHelper.hasAggregateFunction(s2SQL)) {
                queryType = QueryType.AGGREGATE;
            }

            parseInfo.setQueryType(queryType);
        });
    }

}
