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

import java.util.Objects;
import java.util.Set;

public class FunctionAliasVisitor extends SelectItemVisitorAdapter {

    private Set<String> fieldNames;

    public FunctionAliasVisitor(Set<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public void visit(SelectItem selectExpressionItem) {
        if (selectExpressionItem.getExpression() instanceof Function) {
            if (Objects.nonNull(selectExpressionItem.getAlias())) {
                fieldNames.add(selectExpressionItem.getAlias().getName());
            }
        }
    }
}
