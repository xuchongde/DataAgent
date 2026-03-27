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

import org.springframework.stereotype.Component;

import static com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType.DIMENSION;
import static com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType.ID;
import static com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType.VALUE;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.OptionType.OPTIONAL;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.OptionType.REQUIRED;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.RequireNumberType.AT_LEAST;

@Component
public class DetailDimensionQuery extends DetailSemanticQuery {

    public static final String QUERY_MODE = "DETAIL_DIMENSION";

    public DetailDimensionQuery() {
        super();
        queryMatcher.addOption(DIMENSION, REQUIRED, AT_LEAST, 1);
        queryMatcher.addOption(VALUE, OPTIONAL, AT_LEAST, 0);
        queryMatcher.addOption(ID, OPTIONAL, AT_LEAST, 0);
    }

    @Override
    public String getQueryMode() {
        return QUERY_MODE;
    }
}
