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

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

import java.util.Set;

public class CustomExpressionDeParser extends ExpressionDeParser {

    private Set<String> removeFieldNames;
    private boolean dealNull;
    private boolean dealNotNull;

    public CustomExpressionDeParser(Set<String> removeFieldNames, boolean dealNull,
            boolean dealNotNull) {
        this.removeFieldNames = removeFieldNames;
        this.dealNull = dealNull;
        this.dealNotNull = dealNotNull;
    }

    @Override
    public void visit(AndExpression andExpression) {
        processBinaryExpression(andExpression, " AND ");
    }

    @Override
    public void visit(OrExpression orExpression) {
        processBinaryExpression(orExpression, " OR ");
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        if (shouldSkip(isNullExpression)) {
            // Skip this expression
        } else {
            super.visit(isNullExpression);
        }
    }

    private void processBinaryExpression(Expression binaryExpression, String operator) {
        Expression leftExpression = ((AndExpression) binaryExpression).getLeftExpression();
        Expression rightExpression = ((AndExpression) binaryExpression).getRightExpression();

        boolean leftIsNull = leftExpression instanceof IsNullExpression
                && shouldSkip((IsNullExpression) leftExpression);
        boolean rightIsNull = rightExpression instanceof IsNullExpression
                && shouldSkip((IsNullExpression) rightExpression);

        if (leftIsNull && rightIsNull) {
            // Skip both expressions
        } else if (leftIsNull) {
            rightExpression.accept(this);
        } else if (rightIsNull) {
            leftExpression.accept(this);
        } else {
            leftExpression.accept(this);
            buffer.append(operator);
            rightExpression.accept(this);
        }
    }

    private boolean shouldSkip(IsNullExpression isNullExpression) {
        if (isNullExpression.getLeftExpression() instanceof Column) {
            Column column = (Column) isNullExpression.getLeftExpression();
            String columnName = column.getColumnName();
            // Add your target column names here
            if (removeFieldNames.contains(columnName)) {
                if (isNullExpression.isNot() && dealNotNull) {
                    return true;
                } else if (!isNullExpression.isNot() && dealNull) {
                    return true;
                }
            }
        }
        return false;
    }
}
