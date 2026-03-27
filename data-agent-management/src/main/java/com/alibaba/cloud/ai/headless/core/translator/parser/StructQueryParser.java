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
package com.alibaba.cloud.ai.headless.core.translator.parser;

import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.core.pojo.QueryStatement;
import com.alibaba.cloud.ai.headless.core.pojo.SqlQuery;
import com.alibaba.cloud.ai.headless.core.pojo.StructQuery;
import com.alibaba.cloud.ai.headless.core.utils.SqlGenerateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * This parser converts struct semantic query into sql query by generating S2SQL based on structured
 * semantic information.
 */
@Component("StructQueryParser")
@Slf4j
public class StructQueryParser implements QueryParser {

    @Override
    public boolean accept(QueryStatement queryStatement) {
        return Objects.nonNull(queryStatement.getStructQuery()) && !queryStatement.getIsS2SQL();
    }

    @Override
    public void parse(QueryStatement queryStatement) throws Exception {
        SqlGenerateUtils sqlGenerateUtils = ContextUtils.getBean(SqlGenerateUtils.class);
        StructQuery structQuery = queryStatement.getStructQuery();

        String dsTable = queryStatement.getDataSetName();
        if (Objects.isNull(dsTable)) {
            dsTable = "t_ds_temp";
        }
        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setTable(dsTable);
        String sql = String.format("select %s from %s %s %s %s %s",
                sqlGenerateUtils.getSelect(structQuery), dsTable,
                sqlGenerateUtils.generateWhere(structQuery, null),
                sqlGenerateUtils.getGroupBy(structQuery), sqlGenerateUtils.getOrderBy(structQuery),
                sqlGenerateUtils.getLimit(structQuery));
        if (!sqlGenerateUtils.isSupportWith(queryStatement.getOntology().getDatabaseType(),
                queryStatement.getOntology().getDatabaseVersion())) {
            sqlQuery.setSupportWith(false);
            sql = String.format("select %s from %s t0 %s %s %s",
                    sqlGenerateUtils.getSelect(structQuery), dsTable,
                    sqlGenerateUtils.getGroupBy(structQuery),
                    sqlGenerateUtils.getOrderBy(structQuery),
                    sqlGenerateUtils.getLimit(structQuery));
        }
        sqlQuery.setSql(sql);
        queryStatement.setSqlQuery(sqlQuery);
        queryStatement.setIsS2SQL(true);

        log.info("parse structQuery [{}] ", queryStatement.getSqlQuery());
    }

}
