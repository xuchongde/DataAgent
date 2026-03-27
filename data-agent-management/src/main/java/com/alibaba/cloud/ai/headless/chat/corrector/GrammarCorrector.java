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

import com.alibaba.cloud.ai.headless.common.jsqlparser.SqlRemoveHelper;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Correcting SQL syntax, primarily including fixes to select, where, groupBy, and Having clauses
 */
@Slf4j
public class GrammarCorrector extends BaseSemanticCorrector {

    private List<BaseSemanticCorrector> correctors;

    public GrammarCorrector() {
        correctors = new ArrayList<>();
        correctors.add(new SelectCorrector());
        correctors.add(new WhereCorrector());
        correctors.add(new GroupByCorrector());
        correctors.add(new AggCorrector());
        correctors.add(new HavingCorrector());
    }

    @Override
    public void doCorrect(ChatQueryContext chatQueryContext, SemanticParseInfo semanticParseInfo) {
        for (BaseSemanticCorrector corrector : correctors) {
            corrector.correct(chatQueryContext, semanticParseInfo);
        }
        removeSameFieldFromSelect(semanticParseInfo);
    }

    public void removeSameFieldFromSelect(SemanticParseInfo semanticParseInfo) {
        String correctS2SQL = semanticParseInfo.getSqlInfo().getCorrectedS2SQL();
        correctS2SQL = SqlRemoveHelper.removeSameFieldFromSelect(correctS2SQL);
        semanticParseInfo.getSqlInfo().setCorrectedS2SQL(correctS2SQL);
    }
}
