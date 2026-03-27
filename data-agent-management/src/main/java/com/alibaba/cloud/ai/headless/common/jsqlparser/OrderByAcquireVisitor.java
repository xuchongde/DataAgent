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

import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitorAdapter;

import java.util.Set;

public class OrderByAcquireVisitor extends OrderByVisitorAdapter {

    private Set<FieldExpression> fields;

    public OrderByAcquireVisitor(Set<FieldExpression> fields) {
        this.fields = fields;
    }

    @Override
    public void visit(OrderByElement orderBy) {
        Expression expression = orderBy.getExpression();
        FieldExpression fieldExpression = new FieldExpression();
        if (expression instanceof Column) {
            fieldExpression.setFieldName(((Column) expression).getColumnName());
        }
        if (expression instanceof Function) {
            Function function = (Function) expression;
            // List<Expression> expressions = function.getParameters().getExpressions();
            ExpressionList<?> expressions = function.getParameters();
            for (Expression column : expressions) {
                if (column instanceof Column) {
                    fieldExpression.setFieldName(((Column) column).getColumnName());
                }
            }
        }
        String operator = Constants.ASC_UPPER;
        if (!orderBy.isAsc()) {
            operator = Constants.DESC_UPPER;
        }
        fieldExpression.setOperator(operator);
        fields.add(fieldExpression);
        super.visit(orderBy);
    }
}
