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
package com.alibaba.cloud.ai.headless.chat.query.llm.s2sql;

import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SqlInfo;
import com.alibaba.cloud.ai.headless.chat.query.QueryManager;
import com.alibaba.cloud.ai.headless.chat.query.llm.LLMSemanticQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LLMSqlQuery extends LLMSemanticQuery {

    public static final String QUERY_MODE = "LLM_S2SQL";

    public LLMSqlQuery() {
        QueryManager.register(this);
    }

    @Override
    public String getQueryMode() {
        return QUERY_MODE;
    }

    @Override
    public void buildS2Sql(DataSetSchema dataSetSchema) {
        SqlInfo sqlInfo = parseInfo.getSqlInfo();
        sqlInfo.setCorrectedS2SQL(sqlInfo.getParsedS2SQL());
    }
}
