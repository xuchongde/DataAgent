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
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.schema.Column;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FiledNameReplaceVisitor extends ExpressionVisitorAdapter {
    private Map<String, Set<String>> fieldValueToFieldNames;

    public FiledNameReplaceVisitor(Map<String, Set<String>> fieldValueToFieldNames) {
        this.fieldValueToFieldNames = fieldValueToFieldNames;
    }

    @Override
    public void visit(EqualsTo expr) {
        replaceFieldNameByFieldValue(expr);
    }

    @Override
    public void visit(LikeExpression expr) {
        replaceFieldNameByFieldValue(expr);
    }

    private void replaceFieldNameByFieldValue(BinaryExpression expr) {
        Expression leftExpression = expr.getLeftExpression();
        Expression rightExpression = expr.getRightExpression();

        if (!(rightExpression instanceof StringValue) || !(leftExpression instanceof Column)
                || CollectionUtils.isEmpty(fieldValueToFieldNames)
                || Objects.isNull(rightExpression) || Objects.isNull(leftExpression)) {
            return;
        }

        Column leftColumn = (Column) leftExpression;
        StringValue rightStringValue = (StringValue) rightExpression;

        Set<String> fieldNames = fieldValueToFieldNames.get(rightStringValue.getValue());
        if (!CollectionUtils.isEmpty(fieldNames)
                && !fieldNames.contains(leftColumn.getColumnName())) {
            leftColumn.setColumnName(fieldNames.stream().findFirst().get());
        }
    }
}
