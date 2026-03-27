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
package com.alibaba.cloud.ai.headless.chat.query.rule.metric;

import org.springframework.stereotype.Component;

import static com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType.DATASET;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.OptionType.OPTIONAL;
import static com.alibaba.cloud.ai.headless.chat.query.rule.QueryMatchOption.RequireNumberType.AT_MOST;

@Component
public class MetricModelQuery extends MetricSemanticQuery {

    public static final String QUERY_MODE = "METRIC_MODEL";

    public MetricModelQuery() {
        super();
        queryMatcher.addOption(DATASET, OPTIONAL, AT_MOST, 1);
    }

    @Override
    public String getQueryMode() {
        return QUERY_MODE;
    }
}
