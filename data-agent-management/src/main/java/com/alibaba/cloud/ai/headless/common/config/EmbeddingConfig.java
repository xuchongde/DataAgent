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
package com.alibaba.cloud.ai.headless.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class EmbeddingConfig {

    @Value("${s2.embedding.memory.collection.prefix:memory_}")
    private String memoryCollectionPrefix;

    @Value("${s2.embedding.preset.collection:preset_query_collection}")
    private String presetCollection;

    @Value("${s2.embedding.meta.collection:meta_collection}")
    private String metaCollectionName;

    @Value("${s2.embedding.nResult:1}")
    private int nResult;

    @Value("${s2.embedding.metric.analyzeQuery.collection:solved_query_collection}")
    private String metricAnalyzeQueryCollection;

    @Value("${text2sql.collection.name:text2dsl_agent_collection}")
    private String text2sqlCollectionName;

    @Value("${s2.embedding.metric.analyzeQuery.nResult:5}")
    private int metricAnalyzeQueryResultNum;

    public String getMemoryCollectionName(Integer agentId) {
        return memoryCollectionPrefix + agentId;
    }
}
