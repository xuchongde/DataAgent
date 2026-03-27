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
package com.alibaba.cloud.ai.headless.server.task;


import com.alibaba.cloud.ai.dataagent.constant.Constant;
import com.alibaba.cloud.ai.dataagent.service.vectorstore.AgentVectorStoreService;
import com.alibaba.cloud.ai.headless.common.config.EmbeddingConfig;
import com.alibaba.cloud.ai.headless.common.embedding.TextSegmentConvert;
import com.alibaba.cloud.ai.headless.common.pojo.DataItem;
import com.alibaba.cloud.ai.headless.common.pojo.enums.EventType;
import com.alibaba.cloud.ai.headless.common.service.EmbeddingService;
import com.alibaba.cloud.ai.headless.server.service.DimensionService;
import com.alibaba.cloud.ai.headless.server.service.MetricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@Order(2)
public class MetaEmbeddingTask implements CommandLineRunner {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private EmbeddingConfig embeddingConfig;

    @Autowired
    private MetricService metricService;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private AgentVectorStoreService agentVectorStoreService;

    /*@Destroy
    public void onShutdown() {
        embeddingStorePersistFile();
    }

    private void embeddingStorePersistFile() {
        EmbeddingStoreFactory embeddingStoreFactory = EmbeddingStoreFactoryProvider.getFactory();
        if (embeddingStoreFactory instanceof InMemoryEmbeddingStoreFactory inMemoryFactory) {
            long startTime = System.currentTimeMillis();
            inMemoryFactory.persistFile();
            long duration = System.currentTimeMillis() - startTime;
            log.info("Embedding file has been regularly persisted in {} milliseconds", duration);
        }
    }*/

    /*@Scheduled(cron = "${s2.inMemoryEmbeddingStore.persist.cron:0 0 * * * ?}")
    public void executePersistFileTask() {
        embeddingStorePersistFile();
    }*/

    /** * reload meta embedding */
    @Scheduled(cron = "${s2.reload.meta.embedding.corn:0 0 */2 * * ?}")
    public void reloadMetaEmbedding() {
        long startTime = System.currentTimeMillis();
        try {
            List<DataItem> metricDataItems = metricService.getDataEvent().getDataItems();
            if(!CollectionUtils.isEmpty(metricDataItems)) {
                List<Document> metricDocuments = TextSegmentConvert.convertToEmbedding(metricDataItems);
                embeddingProcess(metricDocuments);
            }

            List<DataItem> dimensionDataItems = dimensionService.getAllDataEvents().getDataItems();
            if(!CollectionUtils.isEmpty(dimensionDataItems)){
                List<Document> dimensionDocuments = TextSegmentConvert.convertToEmbedding(dimensionDataItems);
                embeddingProcess(dimensionDocuments);
            }
        } catch (Exception e) {
            log.error("Failed to reload meta embedding.", e);
        }
        long duration = System.currentTimeMillis() - startTime;
        log.info("Embedding has been regularly reloaded  in {} milliseconds", duration);
    }

    private void embeddingProcess(List<Document> list){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        //分agentId
        Set<String> agentIdSet = new HashSet<>();
        list.stream().forEach(metric->{
            agentIdSet.add((String)metric.getMetadata().get(Constant.AGENT_ID));
        });
        //按agentId 向量化
        for(String agentId : agentIdSet){
            List<Document> subList = new ArrayList<>();
            list.stream().forEach(metric->{
                if(agentId.equals(metric.getMetadata().get(Constant.AGENT_ID))){
                    subList.add(metric);
                }
            });
            if(!CollectionUtils.isEmpty(subList)){
                agentVectorStoreService.addDocuments(agentId, subList);
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            reloadMetaEmbedding();
        } catch (Exception e) {
            log.error("initMetaEmbedding error", e);
        }
    }
}
