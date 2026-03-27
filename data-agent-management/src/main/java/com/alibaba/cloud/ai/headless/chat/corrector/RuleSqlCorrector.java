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

import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.parser.ParserConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.cloud.ai.headless.chat.parser.ParserConfig.PARSER_RULE_CORRECTOR_ENABLE;

@Slf4j
public class RuleSqlCorrector extends BaseSemanticCorrector {
    private List<BaseSemanticCorrector> correctors;

    public RuleSqlCorrector() {
        correctors = new ArrayList<>();
        correctors.add(new SchemaCorrector());
        correctors.add(new TimeCorrector());
        correctors.add(new GrammarCorrector());
    }

    @Override
    public void doCorrect(ChatQueryContext chatQueryContext, SemanticParseInfo semanticParseInfo) {
        ParserConfig parserConfig = ContextUtils.getBean(ParserConfig.class);
        if (!Boolean.parseBoolean(parserConfig.getParameterValue(PARSER_RULE_CORRECTOR_ENABLE))) {
            return;
        }

        for (BaseSemanticCorrector corrector : correctors) {
            corrector.correct(chatQueryContext, semanticParseInfo);
        }
    }
}
