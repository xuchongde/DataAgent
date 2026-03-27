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
package com.alibaba.cloud.ai.headless.server.listener;

import com.alibaba.cloud.ai.dataagent.constant.Constant;
import com.alibaba.cloud.ai.dataagent.constant.DocumentMetadataConstant;
import com.alibaba.cloud.ai.dataagent.service.vectorstore.AgentVectorStoreService;
import com.alibaba.cloud.ai.headless.common.config.EmbeddingConfig;
import com.alibaba.cloud.ai.headless.common.embedding.TextSegmentConvert;
import com.alibaba.cloud.ai.headless.common.pojo.DataEvent;
import com.alibaba.cloud.ai.headless.common.pojo.DataItem;
import com.alibaba.cloud.ai.headless.common.pojo.enums.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MetaEmbeddingListener {

    @Autowired
    private EmbeddingConfig embeddingConfig;

    @Autowired
    private AgentVectorStoreService agentVectorStoreService;

    @Value("${s2.embedding.operation.sleep.time:3000}")
    private Integer embeddingOperationSleepTime;

    @Async("eventExecutor")
    @EventListener
    public void onApplicationEvent(DataEvent event) {
        List<DataItem> dataItems = event.getDataItems();
        if (CollectionUtils.isEmpty(dataItems)) {
            return;
        }
        List<Document> documents = TextSegmentConvert.convertToEmbedding(dataItems);
        if (CollectionUtils.isEmpty(documents)) {
            return;
        }
        sleep();
        String agentId = (String)documents.get(0).getMetadata().get(Constant.AGENT_ID);
        if (event.getEventType().equals(EventType.ADD)) {
            agentVectorStoreService.addDocuments(agentId, documents);
        } else if (event.getEventType().equals(EventType.DELETE)) {
            for(Document document:documents){
                Map<String, Object> metadata = new HashMap<>();
                metadata.put(Constant.AGENT_ID, agentId.toString());
                metadata.put(DocumentMetadataConstant.VECTOR_TYPE, document.getMetadata().get(DocumentMetadataConstant.VECTOR_TYPE));
                metadata.put(DocumentMetadataConstant.REF_DB_ID, document.getMetadata().get(DocumentMetadataConstant.REF_DB_ID));
                agentVectorStoreService.deleteDocumentsByMetedata(agentId.toString(), metadata);
            }
        } else if (event.getEventType().equals(EventType.UPDATE)) {
            for(Document document:documents){
                Map<String, Object> metadata = new HashMap<>();
                metadata.put(Constant.AGENT_ID, agentId.toString());
                metadata.put(DocumentMetadataConstant.VECTOR_TYPE, document.getMetadata().get(DocumentMetadataConstant.VECTOR_TYPE));
                metadata.put(DocumentMetadataConstant.REF_DB_ID, document.getMetadata().get(DocumentMetadataConstant.REF_DB_ID));
                agentVectorStoreService.deleteDocumentsByMetedata(agentId.toString(), metadata);
            }
            agentVectorStoreService.addDocuments(agentId, documents);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(embeddingOperationSleepTime);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }
}
