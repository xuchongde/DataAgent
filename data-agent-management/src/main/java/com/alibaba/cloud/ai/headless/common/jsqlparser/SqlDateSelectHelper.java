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

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Date field parsing helper class */
@Slf4j
public class SqlDateSelectHelper {

    public static DateVisitor.DateBoundInfo getDateBoundInfo(String sql, String dateField) {
        List<PlainSelect> plainSelectList = SqlSelectHelper.getPlainSelect(sql);
        if (plainSelectList.size() != 1) {
            return null;
        }
        PlainSelect plainSelect = plainSelectList.get(0);
        if (Objects.isNull(plainSelect)) {
            return null;
        }
        Expression where = plainSelect.getWhere();
        if (Objects.isNull(where)) {
            return null;
        }
        DateVisitor dateVisitor = new DateVisitor(Collections.singletonList(dateField));
        where.accept(dateVisitor);
        return dateVisitor.getDateBoundInfo();
    }
}
