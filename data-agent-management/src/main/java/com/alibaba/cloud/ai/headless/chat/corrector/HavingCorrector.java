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
package com.alibaba.cloud.ai.headless.chat.corrector;

import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlAddHelper;
import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlSelectFunctionHelper;
import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlSelectHelper;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElement;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticSchema;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Perform SQL corrections on the "Having" section in S2SQL. */
@Slf4j
public class HavingCorrector extends BaseSemanticCorrector {

    @Override
    public void doCorrect(ChatQueryContext chatQueryContext, SemanticParseInfo semanticParseInfo) {
        // add aggregate to all metric
        addHaving(chatQueryContext, semanticParseInfo);
    }

    private void addHaving(ChatQueryContext chatQueryContext, SemanticParseInfo semanticParseInfo) {
        Long dataSet = semanticParseInfo.getDataSet().getDataSetId();

        SemanticSchema semanticSchema = chatQueryContext.getSemanticSchema();

        Map<String, String> metrics = semanticSchema.getMetrics(dataSet).stream()
                .collect(Collectors.toMap(SchemaElement::getName,
                        e -> Optional.ofNullable(e.getDefaultAgg()).orElse("")));

        if (CollectionUtils.isEmpty(metrics)) {
            return;
        }
        String havingSql =
                SqlAddHelper.addHaving(semanticParseInfo.getSqlInfo().getCorrectedS2SQL(), metrics);
        semanticParseInfo.getSqlInfo().setCorrectedS2SQL(havingSql);
    }

    private void addHavingToSelect(SemanticParseInfo semanticParseInfo) {
        String correctS2SQL = semanticParseInfo.getSqlInfo().getCorrectedS2SQL();
        if (!SqlSelectFunctionHelper.hasAggregateFunction(correctS2SQL)) {
            return;
        }
        List<Expression> havingExpressionList = SqlSelectHelper.getHavingExpression(correctS2SQL);
        if (!CollectionUtils.isEmpty(havingExpressionList)) {
            String replaceSql =
                    SqlAddHelper.addFunctionToSelect(correctS2SQL, havingExpressionList);
            semanticParseInfo.getSqlInfo().setCorrectedS2SQL(replaceSql);
        }
    }
}
