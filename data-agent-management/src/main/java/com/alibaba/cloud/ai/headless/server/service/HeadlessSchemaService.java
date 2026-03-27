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

import com.alibaba.cloud.ai.headless.common.pojo.ItemDateResp;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.pojo.enums.AuthType;
import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.ItemDateFilter;
import com.alibaba.cloud.ai.headless.api.pojo.MetaFilter;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticSchema;
import com.alibaba.cloud.ai.headless.api.pojo.request.ItemUseReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.SchemaFilterReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.*;
import com.alibaba.cloud.ai.headless.server.pojo.yaml.DataModelYamlTpl;
import com.alibaba.cloud.ai.headless.server.pojo.yaml.DimensionYamlTpl;
import com.alibaba.cloud.ai.headless.server.pojo.yaml.MetricYamlTpl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface HeadlessSchemaService {

    DataSetSchema getDataSetSchema(Long dataSetId);

    SemanticSchema getSemanticSchema();

    SemanticSchema getSemanticSchema(Set<Long> dataSetIds);

    SemanticSchemaResp fetchSemanticSchema(SchemaFilterReq schemaFilterReq);

    List<ModelSchemaResp> fetchModelSchemaResps(List<Long> modelIds);

    List<DimensionResp> getDimensions(MetaFilter metaFilter);

    DimensionResp getDimension(String bizName, Long modelId);

    DimensionResp getDimension(Long id);

    List<MetricResp> getMetrics(MetaFilter metaFilter);

    List<DomainResp> getDomainList(User user);

    List<ModelResp> getModelList(User user, AuthType authType, Long domainId);

    List<ModelResp> getModelList(List<Long> modelIds);

    List<ItemUseResp> getStatInfo(ItemUseReq itemUseReq) throws ExecutionException;

    List<ItemResp> getDomainDataSetTree();

    void getSchemaYamlTpl(SemanticSchemaResp semanticSchemaResp,
            Map<String, List<DimensionYamlTpl>> dimensionYamlMap,
            List<DataModelYamlTpl> dataModelYamlTplList, List<MetricYamlTpl> metricYamlTplList,
            Map<Long, String> modelIdName);

    ItemDateResp getItemDate(ItemDateFilter dimension, ItemDateFilter metric);

}
