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
package com.alibaba.cloud.ai.headless.common.calcite;

import com.google.common.base.Preconditions;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.fun.SqlMonotonicBinaryOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.validate.SqlConformance;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * customize the SqlDialect
 */
public class SemanticSqlDialect extends SqlDialect {

    private static final SqlConformance tagTdwSqlConformance = new SemanticSqlConformance();

    public SemanticSqlDialect(Context context) {
        super(context);
    }

    public static void unparseFetchUsingAnsi(SqlWriter writer, @Nullable SqlNode offset,
            @Nullable SqlNode fetch) {
        Preconditions.checkArgument(fetch != null || offset != null);
        SqlWriter.Frame fetchFrame;
        writer.newlineAndIndent();
        fetchFrame = writer.startList(SqlWriter.FrameTypeEnum.OFFSET);
        writer.keyword("LIMIT");
        boolean hasOffset = false;
        if (offset != null) {
            offset.unparse(writer, -1, -1);
            hasOffset = true;
        }

        if (fetch != null) {
            if (hasOffset) {
                writer.keyword(",");
            }
            fetch.unparse(writer, -1, -1);
        }

        writer.endList(fetchFrame);
    }

    @Override
    public void quoteStringLiteralUnicode(StringBuilder buf, String val) {
        buf.append("'");
        buf.append(val);
        buf.append("'");
    }

    @Override
    public void quoteStringLiteral(StringBuilder buf, String charsetName, String val) {
        buf.append(literalQuoteString);
        buf.append(val.replace(literalEndQuoteString, literalEscapedQuote));
        buf.append(literalEndQuoteString);
    }

    @Override
    public boolean supportsCharSet() {
        return false;
    }

    @Override
    public boolean requiresAliasForFromItems() {
        return true;
    }

    @Override
    public SqlConformance getConformance() {
        // mysql_5
        return tagTdwSqlConformance;
    }

    public boolean supportsGroupByWithCube() {
        return true;
    }

    public void unparseSqlIntervalLiteral(SqlWriter writer, SqlIntervalLiteral literal,
            int leftPrec, int rightPrec) {}

    public void unparseOffsetFetch(SqlWriter writer, @Nullable SqlNode offset,
            @Nullable SqlNode fetch) {
        unparseFetchUsingAnsi(writer, offset, fetch);
    }

    public boolean supportsNestedAggregations() {
        return false;
    }


    public void unparseCall(SqlWriter writer, SqlCall call, int leftPrec, int rightPrec) {
        if (modifyIntervalTime(call, writer, leftPrec, rightPrec)) {
            return;
        }
        super.unparseCall(writer, call, leftPrec, rightPrec);

    }

    private Boolean modifyIntervalTime(SqlCall call, SqlWriter writer, int leftPrec,
            int rightPrec) {
        SqlOperator operator = call.getOperator();
        if (operator instanceof SqlMonotonicBinaryOperator
                && call.getKind().equals(SqlKind.TIMES)) {
            if (call.getOperandList() != null && call.getOperandList().size() == 2
                    && call.getOperandList().get(1) instanceof SqlIntervalLiteral) {
                SqlIntervalLiteral intervalOperand =
                        (SqlIntervalLiteral) call.getOperandList().get(1);
                SqlIntervalLiteral.IntervalValue interval =
                        (SqlIntervalLiteral.IntervalValue) intervalOperand.getValue();
                call.setOperand(1, SqlNumericLiteral.createExactNumeric(interval.toString(),
                        SqlParserPos.ZERO));
                writer.keyword(SqlKind.INTERVAL.name());
                call.unparse(writer, leftPrec, rightPrec);
                unparseSqlIntervalQualifier(writer, interval.getIntervalQualifier(),
                        RelDataTypeSystem.DEFAULT);
                return true;
            }
        }
        return false;
    }
}
