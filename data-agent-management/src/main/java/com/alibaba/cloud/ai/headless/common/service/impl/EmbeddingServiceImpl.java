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
package com.alibaba.cloud.ai.headless.common.service.impl;

import com.alibaba.cloud.ai.dataagent.constant.DocumentMetadataConstant;
import com.alibaba.cloud.ai.dataagent.dto.search.AgentSearchRequest;
import com.alibaba.cloud.ai.dataagent.service.vectorstore.AgentVectorStoreService;
import com.alibaba.cloud.ai.dataagent.service.vectorstore.DynamicFilterService;
import com.alibaba.cloud.ai.headless.common.embedding.Retrieval;
import com.alibaba.cloud.ai.headless.common.embedding.RetrieveQuery;
import com.alibaba.cloud.ai.headless.common.embedding.RetrieveQueryResult;
import com.alibaba.cloud.ai.headless.common.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {
    @Autowired
    private AgentVectorStoreService agentVectorStoreService;
    @Autowired
    private DynamicFilterService dynamicFilterService;

    @Override
    public List<RetrieveQueryResult> retrieveQuery(String agentId,
                                                   RetrieveQuery retrieveQuery, int num) {
        List<RetrieveQueryResult> resultList  =new ArrayList<>();
        for(String text:retrieveQuery.getQueryTextsList()){
            AgentSearchRequest searchRequest=AgentSearchRequest.builder().agentId(agentId)
                    .filterCondition(retrieveQuery.getFilterCondition()).topK(num)
                    .docVectorType((String)retrieveQuery.getFilterCondition().get(DocumentMetadataConstant.VECTOR_TYPE))
                    .build();
            List<Document> documentList = agentVectorStoreService.search(searchRequest);
            if(CollectionUtils.isEmpty(documentList)){
                continue;
            }
            RetrieveQueryResult result = new RetrieveQueryResult();
            result.setQuery(text);
            List<Retrieval> retrievalList = new ArrayList<>();
            for (Document doc : documentList){
                Retrieval retrieval = new Retrieval();
                retrieval.setMetadata(doc.getMetadata());
                retrieval.setSimilarity(doc.getScore());
                retrieval.setQuery(text);
                retrieval.setId(doc.getMetadata().get(DocumentMetadataConstant.REF_DB_ID).toString());
                retrievalList.add(retrieval);
            }
            result.setRetrieval(retrievalList);
            resultList.add(result);
        }
        return resultList;
    }
}
