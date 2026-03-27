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

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.MetaFilter;
import com.alibaba.cloud.ai.headless.api.pojo.request.DataSetReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryDataSetReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.SemanticQueryReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DataSetResp;

import java.util.List;
import java.util.Map;

public interface DataSetService {

    DataSetResp save(DataSetReq dataSetReq, User user);

    DataSetResp update(DataSetReq dataSetReq, User user);

    DataSetResp getDataSet(Long id);

    List<DataSetResp> getDataSetList(MetaFilter metaFilter);

    List<DataSetResp> getDataSetList(Long domainId, List<Integer> statuCodesList);

    void delete(Long id, User user);

    Map<Long, List<Long>> getModelIdToDataSetIds(List<Long> dataSetIds, User user);

    Map<Long, List<Long>> getModelIdToDataSetIds();

    List<DataSetResp> getDataSets(String dataSetName, User user);

    List<DataSetResp> getDataSets(List<String> dataSetNames, User user);

    List<DataSetResp> getDataSetsInheritAuth(User user, Long domainId);

    SemanticQueryReq convert(QueryDataSetReq queryDataSetReq);

    Long getDataSetIdFromSql(String sql, User user);
}
