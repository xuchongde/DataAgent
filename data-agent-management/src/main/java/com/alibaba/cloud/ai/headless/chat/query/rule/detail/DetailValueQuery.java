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
package com.alibaba.cloud.ai.headless.chat.query.rule.detail;

import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElementMatch;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import org.springframework.stereotype.Component;

import static com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType.DIMENSION;
import static com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType.METRIC;
import static com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType.VALUE;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.OptionType.OPTIONAL;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.OptionType.REQUIRED;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.RequireNumberType.AT_LEAST;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.RequireNumberType.AT_MOST;

@Component
public class DetailValueQuery extends DetailSemanticQuery {

    public static final String QUERY_MODE = "DETAIL_VALUE";

    public DetailValueQuery() {
        super();
        queryMatcher.addOption(VALUE, REQUIRED, AT_LEAST, 1);
        queryMatcher.addOption(DIMENSION, OPTIONAL, AT_MOST, 0);
        queryMatcher.addOption(METRIC, OPTIONAL, AT_MOST, 0);
    }

    @Override
    public String getQueryMode() {
        return QUERY_MODE;
    }

    @Override
    public void fillParseInfo(ChatQueryContext chatQueryContext, Long dataSetId) {
        super.fillParseInfo(chatQueryContext, dataSetId);

        DataSetSchema dataSetSchema = chatQueryContext.getDataSetSchema(dataSetId);
        parseInfo.getDimensions().addAll(dataSetSchema.getDimensions());
        parseInfo.getDimensions().forEach(
                d -> parseInfo.getElementMatches().add(SchemaElementMatch.builder().element(d)
                        .word(d.getName()).similarity(0).detectWord(d.getName()).build()));

    }

}
