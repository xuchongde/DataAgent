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
import com.alibaba.cloud.ai.headless.common.pojo.Aggregator;
import com.alibaba.cloud.ai.headless.common.pojo.DateConf;
import com.alibaba.cloud.ai.headless.common.pojo.Filter;
import com.alibaba.cloud.ai.headless.common.pojo.Order;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
@ToString
public class QueryTagReq extends SemanticQueryReq {

    private List<String> groups = new ArrayList<>();
    private List<Aggregator> aggregators = new ArrayList<>();
    private List<Filter> tagFilters = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    private Long limit = 20L;
    private Long offset = 0L;

    private String tagFiltersDate;
    private DateConf dateInfo;

    @Override
    public String toCustomizedString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("\"dataSetId\":").append(dataSetId);
        stringBuilder.append("\"modelIds\":").append(modelIds);
        stringBuilder.append(",\"groups\":").append(groups);
        stringBuilder.append(",\"aggregators\":").append(aggregators);
        stringBuilder.append(",\"orders\":").append(orders);
        stringBuilder.append(",\"tagFilters\":").append(tagFilters);
        stringBuilder.append(",\"dateInfo\":").append(dateInfo);
        stringBuilder.append(",\"params\":").append(params);
        stringBuilder.append(",\"limit\":").append(limit);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public List<String> getMetrics() {
        List<String> metrics = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(this.aggregators)) {
            metrics = aggregators.stream().map(Aggregator::getColumn).collect(Collectors.toList());
        }
        return metrics;
    }
}
