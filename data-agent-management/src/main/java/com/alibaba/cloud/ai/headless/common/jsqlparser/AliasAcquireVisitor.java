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
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.Set;

public class AliasAcquireVisitor extends ExpressionVisitorAdapter {

    private Set<String> aliases;

    public AliasAcquireVisitor(Set<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public void visit(SelectItem selectItem) {
        Alias alias = selectItem.getAlias();
        if (alias != null) {
            aliases.add(alias.getName());
        }
    }
}
