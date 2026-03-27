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
package com.alibaba.cloud.ai.headless.server.facade.service;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.MetaFilter;
import com.alibaba.cloud.ai.headless.api.pojo.request.DimensionValueReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.SemanticQueryReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DimensionResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ItemResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.MetricResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SemanticQueryResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SemanticTranslateResp;

import java.util.List;

/** This interface abstracts functionalities provided by a semantic layer. */
public interface SemanticLayerService {

    SemanticTranslateResp translate(SemanticQueryReq queryReq, User user) throws Exception;

    SemanticQueryResp queryByReq(SemanticQueryReq queryReq, User user) throws Exception;

    SemanticQueryResp queryDimensionValue(DimensionValueReq dimensionValueReq, User user);

    DataSetSchema getDataSetSchema(Long id);

    List<ItemResp> getDomainDataSetTree(User user);

    List<DimensionResp> getDimensions(MetaFilter metaFilter);

    List<MetricResp> getMetrics(MetaFilter metaFilter);
}
