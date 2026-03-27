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
package com.alibaba.cloud.ai.headless.server.persistence.mapper;

import com.alibaba.cloud.ai.headless.server.persistence.dataobject.MetricDO;
import com.alibaba.cloud.ai.headless.server.pojo.MetricsFilter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MetricDOCustomMapper {

    void batchInsert(List<MetricDO> metricDOS);

    void batchUpdateStatus(List<MetricDO> metricDOS);

    void batchUpdate(List<MetricDO> metricDOS);

    void batchPublish(List<MetricDO> metricDOS);

    void batchUnPublish(List<MetricDO> metricDOS);

    void updateClassificationsBatch(List<MetricDO> metricDOS);

    List<MetricDO> queryMetrics(MetricsFilter metricsFilter);
}
