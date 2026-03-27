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
package com.alibaba.cloud.ai.headless.core.translator.parser.calcite;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import lombok.Data;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class TableView {

    private Set<String> fields = Sets.newHashSet();
    private List<SqlNode> select = Lists.newArrayList();
    private SqlNodeList order;
    private SqlNode fetch;
    private SqlNode offset;
    private SqlNode table;
    private String alias;
    private List<String> primary;
    private ModelResp dataModel;

    public SqlNode build() {
        List<SqlNode> selectNodeList = new ArrayList<>();
        if (select.isEmpty()) {
            return new SqlSelect(SqlParserPos.ZERO, null,
                    new SqlNodeList(SqlNodeList.SINGLETON_STAR, SqlParserPos.ZERO), table, null,
                    null, null, null, null, order, offset, fetch, null);
        } else {
            selectNodeList.addAll(select);
            return new SqlSelect(SqlParserPos.ZERO, null,
                    new SqlNodeList(selectNodeList, SqlParserPos.ZERO), table, null, null, null,
                    null, null, order, offset, fetch, null);
        }
    }

}
