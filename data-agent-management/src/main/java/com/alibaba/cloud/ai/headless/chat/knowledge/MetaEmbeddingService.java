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
package com.alibaba.cloud.ai.headless.chat.knowledge;

import com.alibaba.cloud.ai.headless.common.config.EmbeddingConfig;
import com.alibaba.cloud.ai.headless.common.embedding.Retrieval;
import com.alibaba.cloud.ai.headless.common.embedding.RetrieveQuery;
import com.alibaba.cloud.ai.headless.common.embedding.RetrieveQueryResult;
import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import com.alibaba.cloud.ai.headless.common.pojo.enums.DictWordType;
import com.alibaba.cloud.ai.headless.common.service.EmbeddingService;
import com.alibaba.cloud.ai.headless.chat.knowledge.helper.NatureHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MetaEmbeddingService {

    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private EmbeddingConfig embeddingConfig;

    public List<RetrieveQueryResult> retrieveQuery(RetrieveQuery retrieveQuery, int num,
                                                   Map<Long, List<Long>> modelIdToDataSetIds, Set<Long> detectDataSetIds) {
        // dataSetIds->modelIds
        Set<Long> allModels = NatureHelper.getModelIds(modelIdToDataSetIds, detectDataSetIds);

        if (CollectionUtils.isNotEmpty(allModels)) {
            Map<String, Object> filterCondition = new HashMap<>();
            filterCondition.put("modelId",
                    allModels.stream().map(modelId -> modelId + DictWordType.NATURE_SPILT)
                            .collect(Collectors.toList()));
            retrieveQuery.setFilterCondition(filterCondition);
        }

        String collectionName = embeddingConfig.getMetaCollectionName();
        List<RetrieveQueryResult> resultList =
                embeddingService.retrieveQuery(collectionName, retrieveQuery, num);
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }
        // Filter and process query results.
        return resultList.stream()
                .map(result -> getRetrieveQueryResult(modelIdToDataSetIds, result))
                .filter(result -> CollectionUtils.isNotEmpty(result.getRetrieval()))
                .collect(Collectors.toList());
    }

    private static RetrieveQueryResult getRetrieveQueryResult(
            Map<Long, List<Long>> modelIdToDataSetIds, RetrieveQueryResult result) {
        List<Retrieval> retrievals = result.getRetrieval();
        if (CollectionUtils.isEmpty(retrievals)) {
            return result;
        }
        // Process each Retrieval object.
        List<Retrieval> updatedRetrievals = retrievals.stream().flatMap(retrieval -> {
            Long modelId = Retrieval.getLongId(retrieval.getMetadata().get("modelId"));
            List<Long> dataSetIds = modelIdToDataSetIds.get(modelId);

            if (CollectionUtils.isEmpty(dataSetIds)) {
                return Stream.of(retrieval);
            }

            return dataSetIds.stream().map(dataSetId -> {
                Retrieval newRetrieval = new Retrieval();
                BeanUtils.copyProperties(retrieval, newRetrieval);
                HashMap<String, Object> newMetadata = new HashMap<>(retrieval.getMetadata());
                newRetrieval.setMetadata(newMetadata);
                newRetrieval.getMetadata().putIfAbsent("dataSetId",
                        dataSetId + Constants.UNDERLINE);
                return newRetrieval;
            });
        }).collect(Collectors.toList());
        result.setRetrieval(updatedRetrievals);
        return result;
    }
}
