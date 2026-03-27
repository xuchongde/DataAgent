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

import com.alibaba.cloud.ai.dataagent.constant.Constant;
import com.alibaba.cloud.ai.dataagent.constant.DocumentMetadataConstant;
import com.alibaba.cloud.ai.dataagent.service.vectorstore.AgentVectorStoreService;
import com.alibaba.cloud.ai.headless.common.config.EmbeddingConfig;
import com.alibaba.cloud.ai.headless.common.pojo.Text2SQLExemplar;
import com.alibaba.cloud.ai.headless.common.service.EmbeddingService;
import com.alibaba.cloud.ai.headless.common.service.ExemplarService;
import com.alibaba.cloud.ai.headless.common.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Order(0)
public class ExemplarServiceImpl implements ExemplarService, CommandLineRunner {

    private static final String SYS_EXEMPLAR_FILE = "s2-exemplar.json";

    private TypeReference<List<Text2SQLExemplar>> valueTypeRef =
            new TypeReference<List<Text2SQLExemplar>>() {};

    private final ObjectMapper objectMapper = JsonUtil.INSTANCE.getObjectMapper();

    @Autowired
    private EmbeddingConfig embeddingConfig;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private AgentVectorStoreService agentVectorStoreService;

    public void storeExemplar(String collection, Text2SQLExemplar exemplar) {
        Map metadata = JsonUtil.toObject(JsonUtil.toString(exemplar),Map.class);
        metadata.put(Constant.AGENT_ID,collection);
        metadata.put(DocumentMetadataConstant.VECTOR_TYPE, DocumentMetadataConstant.VECTOR_TYPE_EXEMPLAR);
        metadata.put(DocumentMetadataConstant.REF_DB_ID,exemplar.getQuestion());
        Document document = new Document(exemplar.getQuestion(),metadata);
        List<Document> documents = new ArrayList<>();
        documents.add(document);
        agentVectorStoreService.addDocuments(collection, documents);
    }

    public void removeExemplar(String collection, Text2SQLExemplar exemplar) {
        Map<String,Object> metadata = new HashMap();
        metadata.put(Constant.AGENT_ID,collection);
        metadata.put(DocumentMetadataConstant.VECTOR_TYPE, DocumentMetadataConstant.VECTOR_TYPE_EXEMPLAR);
        metadata.put(DocumentMetadataConstant.REF_DB_ID,exemplar.getQuestion());
        agentVectorStoreService.deleteDocumentsByMetedata(collection,metadata);
    }

    public List<Text2SQLExemplar> recallExemplars(String query, int num) {
        String collection = embeddingConfig.getText2sqlCollectionName();
        return recallExemplars(collection, query, num);
    }

    public List<Text2SQLExemplar> recallExemplars(String collection, String query, int num) {
        List<Text2SQLExemplar> exemplars = Lists.newArrayList();
        /*RetrieveQuery retrieveQuery =
                RetrieveQuery.builder().queryTextsList(Lists.newArrayList(query)).build();
        List<RetrieveQueryResult> results =
                embeddingService.retrieveQuery(collection, retrieveQuery, num);
        results.forEach(ret -> {
            ret.getRetrieval().forEach(r -> {
                Text2SQLExemplar tmp = // 传递相似度，可以作为样本筛选的依据
                        JsonUtil.mapToObject(r.getMetadata(), Text2SQLExemplar.class);
                tmp.setSimilarity(r.getSimilarity());
                exemplars.add(tmp);
            });
        });

        return exemplars;*/
        return exemplars;
    }

    @Override
    public void run(String... args) {
        loadSysExemplars();
    }

    public void loadSysExemplars() {
        try {
            ClassPathResource resource = new ClassPathResource(SYS_EXEMPLAR_FILE);
            InputStream inputStream = resource.getInputStream();
            List<Text2SQLExemplar> exemplars = objectMapper.readValue(inputStream, valueTypeRef);
            String collection = embeddingConfig.getText2sqlCollectionName();
            exemplars.stream().forEach(e -> storeExemplar(collection, e));
        } catch (Exception e) {
            log.error("Failed to load system exemplars", e);
        }
    }
}
