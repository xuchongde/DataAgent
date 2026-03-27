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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class DateVisitor extends ExpressionVisitorAdapter {

    private List<String> filedNames;

    private DateBoundInfo dateBoundInfo = new DateBoundInfo();

    public DateVisitor(List<String> filedNames) {
        this.filedNames = filedNames;
    }

    @Override
    public void visit(GreaterThan expr) {
        if (containsField(expr.getLeftExpression())) {
            dateBoundInfo.setColumName(SqlSelectHelper.getColumnName(expr.getLeftExpression()));
            dateBoundInfo.setLowerBound(expr.getStringExpression());
            String columnValue = SqlSelectHelper.getColumValue(expr.getRightExpression());
            dateBoundInfo.setLowerDate(columnValue);
        }
    }

    @Override
    public void visit(GreaterThanEquals expr) {
        if (containsField(expr.getLeftExpression())) {
            dateBoundInfo.setColumName(SqlSelectHelper.getColumnName(expr.getLeftExpression()));
            dateBoundInfo.setLowerBound(expr.getStringExpression());
            String columnValue = SqlSelectHelper.getColumValue(expr.getRightExpression());
            dateBoundInfo.setLowerDate(columnValue);
        }
    }

    @Override
    public void visit(MinorThanEquals expr) {
        if (containsField(expr.getLeftExpression())) {
            dateBoundInfo.setColumName(SqlSelectHelper.getColumnName(expr.getLeftExpression()));
            dateBoundInfo.setUpperBound(expr.getStringExpression());
            String columnValue = SqlSelectHelper.getColumValue(expr.getRightExpression());
            dateBoundInfo.setUpperDate(columnValue);
        }
    }

    @Override
    public void visit(MinorThan expr) {
        if (containsField(expr.getLeftExpression())) {
            dateBoundInfo.setColumName(SqlSelectHelper.getColumnName(expr.getLeftExpression()));
            dateBoundInfo.setUpperBound(expr.getStringExpression());
            String columnValue = SqlSelectHelper.getColumValue(expr.getRightExpression());
            dateBoundInfo.setUpperDate(columnValue);
        }
    }

    private boolean containsField(Expression expr) {
        if (expr instanceof Column) {
            Column column = (Column) expr;
            if (!CollectionUtils.isEmpty(filedNames)
                    && filedNames.contains(column.getColumnName())) {
                return true;
            }
        }
        return false;
    }

    public DateBoundInfo getDateBoundInfo() {
        return dateBoundInfo;
    }

    @Data
    public class DateBoundInfo {

        private String columName;
        private String lowerBound;
        private String lowerDate;
        private String upperBound;
        private String upperDate;
    }
}
