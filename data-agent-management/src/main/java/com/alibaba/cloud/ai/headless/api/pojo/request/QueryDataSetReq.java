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

import com.alibaba.cloud.ai.headless.common.pojo.Aggregator;
import com.alibaba.cloud.ai.headless.common.pojo.DateConf;
import com.alibaba.cloud.ai.headless.common.pojo.Filter;
import com.alibaba.cloud.ai.headless.common.pojo.Order;
import com.alibaba.cloud.ai.headless.common.pojo.enums.QueryType;
import com.alibaba.cloud.ai.headless.api.pojo.Cache;
import com.alibaba.cloud.ai.headless.api.pojo.Param;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class QueryDataSetReq {

    private Long dataSetId;
    private String dataSetName;
    private String sql;
    private boolean needAuth = true;
    private List<Param> params = new ArrayList<>();
    private Cache cacheInfo = new Cache();
    private List<String> groups = new ArrayList<>();
    private List<Aggregator> aggregators = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private List<Filter> dimensionFilters = new ArrayList<>();
    private List<Filter> metricFilters = new ArrayList<>();
    private DateConf dateInfo;
    private Long limit = 2000L;
    private Long offset = 0L;
    private QueryType queryType = QueryType.DETAIL;
    private boolean innerLayerNative = false;
}
