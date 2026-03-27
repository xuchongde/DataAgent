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
package com.alibaba.cloud.ai.headless.core.executor;

import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.api.pojo.response.DatabaseResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SemanticQueryResp;
import com.alibaba.cloud.ai.headless.core.pojo.QueryStatement;
import com.alibaba.cloud.ai.headless.core.utils.ComponentFactory;
import com.alibaba.cloud.ai.headless.core.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("JdbcExecutor")
@Slf4j
public class JdbcExecutor implements QueryExecutor {
    @Override
    public boolean accept(QueryStatement queryStatement) {
        return true;
    }

    @Override
    public SemanticQueryResp execute(QueryStatement queryStatement) {
        // accelerate query if possible
        for (QueryAccelerator queryAccelerator : ComponentFactory.getQueryAccelerators()) {
            if (queryAccelerator.check(queryStatement)) {
                SemanticQueryResp semanticQueryResp = queryAccelerator.query(queryStatement);
                if (Objects.nonNull(semanticQueryResp)
                        && !semanticQueryResp.getResultList().isEmpty()) {
                    log.info("query by Accelerator {}",
                            queryAccelerator.getClass().getSimpleName());
                    return semanticQueryResp;
                }
            }
        }

        SqlUtils sqlUtils = ContextUtils.getBean(SqlUtils.class);
        String sql = StringUtils.normalizeSpace(queryStatement.getSql());
        log.info("executing SQL: {}", sql);
        DatabaseResp database = queryStatement.getOntology().getDatabase();
        SemanticQueryResp queryResultWithColumns = new SemanticQueryResp();
        try {
            SqlUtils sqlUtil = sqlUtils.init(database);
            sqlUtil.queryInternal(queryStatement.getSql(), queryResultWithColumns);
            queryResultWithColumns.setSql(sql);
        } catch (Exception e) {
            log.error("queryInternal with error ", e);
            queryResultWithColumns.setErrorMsg(e.getMessage());
        }
        return queryResultWithColumns;
    }
}
