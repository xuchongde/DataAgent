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
import com.alibaba.cloud.ai.headless.api.pojo.ItemDateFilter;
import com.alibaba.cloud.ai.headless.api.pojo.MetaFilter;
import com.alibaba.cloud.ai.headless.api.pojo.ModelSchema;
import com.alibaba.cloud.ai.headless.api.pojo.request.*;
import com.alibaba.cloud.ai.headless.api.pojo.response.DatabaseResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.UnAvailableItemResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.DimensionDO;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.MetricDO;
import com.alibaba.cloud.ai.headless.server.pojo.ModelFilter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ModelService {

    ModelResp createModel(ModelReq datasourceReq, User user) throws Exception;

    List<ModelResp> createModel(ModelBuildReq modelBuildReq, User user) throws Exception;

    ModelResp updateModel(ModelReq datasourceReq, User user) throws Exception;

    List<ModelResp> getModelList(MetaFilter metaFilter);

    Map<Long, ModelResp> getModelMap(ModelFilter modelFilter);

    void deleteModel(Long id, User user);

    ItemDateResp getItemDate(ItemDateFilter dimension, ItemDateFilter metric);

    UnAvailableItemResp getUnAvailableItem(FieldRemovedReq fieldRemovedReq);

    Map<String, ModelSchema> buildModelSchema(ModelBuildReq modelBuildReq) throws SQLException;

    List<ModelResp> getModelListWithAuth(User user, Long domainId, AuthType authType);

    List<ModelResp> getModelAuthList(User user, Long domainId, AuthType authTypeEnum);

    List<ModelResp> getModelByDomainIds(List<Long> domainIds);

    List<ModelResp> getAllModelByDomainIds(List<Long> domainIds);

    ModelResp getModel(Long id);

    List<String> getModelAdmin(Long id);

    DatabaseResp getDatabaseByModelId(Long modelId);

    void batchUpdateStatus(MetaBatchReq metaBatchReq, User user);

    void updateModelByDimAndMetric(Long modelId, List<DimensionReq> dimensionReqList,
            List<MetricReq> metricReqList, User user);

    void deleteModelDetailByDimAndMetric(Long modelId, List<DimensionDO> dimensionReqList,
            List<MetricDO> metricReqList);
}
