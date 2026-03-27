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
package com.alibaba.cloud.ai.headless.common.jsqlparser;

import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FunctionAliasReplaceVisitor extends SelectItemVisitorAdapter {

    private Map<String, String> aliasToActualExpression = new HashMap<>();

    @Override
    public void visit(SelectItem selectExpressionItem) {
        if (selectExpressionItem.getExpression() instanceof Function) {
            Function function = (Function) selectExpressionItem.getExpression();
            String columnName = SqlSelectHelper.getColumnName(function);
            // 1.exist alias. as
            // 2.alias's fieldName not equal. "sum(pv) as pv" cannot be replaced.
            if (Objects.nonNull(selectExpressionItem.getAlias())
                    && !selectExpressionItem.getAlias().getName().equalsIgnoreCase(columnName)) {
                aliasToActualExpression.put(selectExpressionItem.getAlias().getName(),
                        function.toString());
                selectExpressionItem.setAlias(null);
            }
        }
    }

    public Map<String, String> getAliasToActualExpression() {
        return aliasToActualExpression;
    }
}
