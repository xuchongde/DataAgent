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
package com.alibaba.cloud.ai.headless.api.pojo.request;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import com.alibaba.cloud.ai.headless.common.pojo.DateConf;
import com.alibaba.cloud.ai.headless.common.pojo.Filter;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class QueryMetricReq {

    private Long domainId;

    private List<Long> metricIds = Lists.newArrayList();

    private List<String> metricNames = Lists.newArrayList();

    private List<Long> dimensionIds = Lists.newArrayList();

    private List<String> dimensionNames = Lists.newArrayList();

    private List<Filter> filters = Lists.newArrayList();

    private DateConf dateInfo = new DateConf();

    private long limit = Constants.DEFAULT_METRIC_LIMIT;

    private boolean innerLayerNative = false;
}
