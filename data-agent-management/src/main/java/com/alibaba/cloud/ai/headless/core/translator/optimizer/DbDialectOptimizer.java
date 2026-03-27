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
package com.alibaba.cloud.ai.headless.core.translator.optimizer;

import com.alibaba.cloud.ai.headless.api.pojo.response.DatabaseResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SemanticSchemaResp;
import com.alibaba.cloud.ai.headless.core.adaptor.db.DbAdaptor;
import com.alibaba.cloud.ai.headless.core.adaptor.db.DbAdaptorFactory;
import com.alibaba.cloud.ai.headless.core.pojo.QueryStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component("DbDialectOptimizer")
public class DbDialectOptimizer implements QueryOptimizer {

    @Override
    public boolean accept(QueryStatement queryStatement) {
        SemanticSchemaResp semanticSchemaResp = queryStatement.getSemanticSchema();
        DatabaseResp database = semanticSchemaResp.getDatabaseResp();
        return Objects.nonNull(database) && Objects.nonNull(database.getType());
    }

    @Override
    public void rewrite(QueryStatement queryStatement) {
        SemanticSchemaResp semanticSchemaResp = queryStatement.getSemanticSchema();
        DatabaseResp database = semanticSchemaResp.getDatabaseResp();
        String sql = queryStatement.getSql();
        DbAdaptor engineAdaptor = DbAdaptorFactory.getEngineAdaptor(database.getType());
        if (Objects.nonNull(engineAdaptor)) {
            String adaptedSql = engineAdaptor.rewriteSql(sql);
            queryStatement.setSql(adaptedSql);
        }
    }
}
