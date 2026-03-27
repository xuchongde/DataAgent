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

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;

import java.util.Map;
import java.util.Objects;

public class ExpressionReplaceVisitor extends ExpressionVisitorAdapter {

    private Map<String, String> fieldExprMap;

    public ExpressionReplaceVisitor(Map<String, String> fieldExprMap) {
        this.fieldExprMap = fieldExprMap;
    }

    public void visit(WhenClause expr) {
        expr.getWhenExpression().accept(this);
        if (expr.getThenExpression() instanceof Column) {
            Column column = (Column) expr.getThenExpression();
            Expression expression = QueryExpressionReplaceVisitor.getExpression(
                    QueryExpressionReplaceVisitor.getReplaceExpr(column, fieldExprMap));
            if (Objects.nonNull(expression)) {
                expr.setThenExpression(expression);
            }
        } else {
            expr.getThenExpression().accept(this);
        }
    }

    protected void visitBinaryExpression(BinaryExpression expr) {
        Expression left = expr.getLeftExpression();
        Expression right = expr.getRightExpression();
        Boolean leftVisited = false;
        Boolean rightVisited = false;
        if (left instanceof Function) {
            Function leftFunc = (Function) left;
            if (visitFunction(leftFunc)) {
                leftVisited = true;
            }
        }
        if (right instanceof Function) {
            Function function = (Function) right;
            if (visitFunction(function)) {
                rightVisited = true;
            }
        }
        if (left instanceof Column) {
            Expression expression = QueryExpressionReplaceVisitor.getExpression(
                    QueryExpressionReplaceVisitor.getReplaceExpr((Column) left, fieldExprMap));
            if (Objects.nonNull(expression)) {
                expr.setLeftExpression(expression);
                leftVisited = true;
            }
        }
        if (right instanceof Column) {
            Expression expression = QueryExpressionReplaceVisitor.getExpression(
                    QueryExpressionReplaceVisitor.getReplaceExpr((Column) right, fieldExprMap));
            if (Objects.nonNull(expression)) {
                expr.setRightExpression(expression);
                rightVisited = true;
            }
        }
        if (!leftVisited) {
            expr.getLeftExpression().accept(this);
        }
        if (!rightVisited) {
            expr.getRightExpression().accept(this);
        }
    }

    private boolean visitFunction(Function function) {
        if (function.getParameters().getExpressions().get(0) instanceof Column) {
            Expression expression = QueryExpressionReplaceVisitor.getExpression(
                    QueryExpressionReplaceVisitor.getReplaceExpr(function, fieldExprMap));
            if (Objects.nonNull(expression)) {
                ExpressionList<Expression> expressions = new ExpressionList<>();
                expressions.add(expression);
                for (int i = 1; i < function.getParameters().size(); i++) {
                    expressions.add((Expression) function.getParameters().get(i));
                }
                function.setParameters(expressions);
                return true;
            }
        }
        return false;
    }
}
