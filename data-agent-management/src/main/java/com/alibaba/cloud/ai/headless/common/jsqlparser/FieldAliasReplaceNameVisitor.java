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

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class FieldAliasReplaceNameVisitor extends SelectItemVisitorAdapter {
    private Map<String, String> fieldNameMap;

    private Map<String, String> aliasToActualExpression = new HashMap<>();

    public FieldAliasReplaceNameVisitor(Map<String, String> fieldNameMap) {
        this.fieldNameMap = fieldNameMap;
    }

    @Override
    public void visit(SelectItem selectExpressionItem) {
        Alias alias = selectExpressionItem.getAlias();
        if (alias == null) {
            return;
        }
        String aliasName = alias.getName();
        String replaceValue = fieldNameMap.get(aliasName);
        if (StringUtils.isBlank(replaceValue)) {
            return;
        }

        aliasToActualExpression.put(aliasName, replaceValue);
        alias.setName(replaceValue);
    }

    public Map<String, String> getAliasToActualExpression() {
        return aliasToActualExpression;
    }
}
