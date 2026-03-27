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
package com.alibaba.cloud.ai.headless.server.service;

import com.github.pagehelper.PageInfo;
import com.alibaba.cloud.ai.headless.common.pojo.DataEvent;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.pojo.enums.EventType;
import com.alibaba.cloud.ai.headless.api.pojo.DrillDownDimension;
import com.alibaba.cloud.ai.headless.api.pojo.MetaFilter;
import com.alibaba.cloud.ai.headless.api.pojo.MetricQueryDefaultConfig;
import com.alibaba.cloud.ai.headless.api.pojo.request.MetaBatchReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.MetricBaseReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.MetricReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.PageMetricReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryMetricReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryStructReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.MetricResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import com.alibaba.cloud.ai.headless.server.pojo.MetricsFilter;

import java.util.List;
import java.util.Set;

public interface MetricService {

    MetricResp createMetric(MetricReq metricReq, User user) throws Exception;

    void createMetricBatch(List<MetricReq> metricReqs, User user) throws Exception;

    void alterMetricBatch(List<MetricReq> metricReqs, Long modelId, User user) throws Exception;

    MetricResp updateMetric(MetricReq metricReq, User user) throws Exception;

    void updateMetricBatch(List<MetricReq> metricReqs, User user) throws Exception;

    void batchUpdateStatus(MetaBatchReq metaBatchReq, User user);

    void batchPublish(List<Long> metricIds, User user);

    void batchUnPublish(List<Long> metricIds, User user);

    void batchUpdateClassifications(MetaBatchReq metaBatchReq, User user);

    void batchUpdateSensitiveLevel(MetaBatchReq metaBatchReq, User user);

    void deleteMetric(Long id, User user) throws Exception;

    void deleteMetricBatch(List<Long> idList, User user);

    PageInfo<MetricResp> queryMetricMarket(PageMetricReq pageMetricReq, User user);

    PageInfo<MetricResp> queryMetric(PageMetricReq pageMetricReq, User user);

    List<MetricResp> getMetrics(MetaFilter metaFilter);

    List<MetricResp> getMetricsToCreateNewMetric(Long modelId);

    MetricResp getMetric(Long modelId, String bizName);

    MetricResp getMetric(Long id, User user);

    MetricResp getMetric(Long id);

    List<String> mockAlias(MetricBaseReq metricReq, String mockType, User user);

    Set<String> getMetricTags();

    List<DrillDownDimension> getDrillDownDimension(Long metricId);

    void saveMetricQueryDefaultConfig(MetricQueryDefaultConfig defaultConfig, User user);

    MetricQueryDefaultConfig getMetricQueryDefaultConfig(Long metricId, User user);

    void sendMetricEventBatch(List<Long> modelIds, EventType eventType, User user);

    List<MetricResp> queryMetrics(MetricsFilter metricsFilter);

    void batchFillMetricDefaultAgg(List<MetricResp> metricResps, List<ModelResp> modelResps);

    QueryStructReq convert(QueryMetricReq queryMetricReq);

    DataEvent getDataEvent();
}
