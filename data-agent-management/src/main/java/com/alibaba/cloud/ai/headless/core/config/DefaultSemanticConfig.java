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
package com.alibaba.cloud.ai.headless.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DefaultSemanticConfig {

    @Value("${s2.semantic.url.prefix:http://localhost:8081}")
    private String semanticUrl;

    @Value("${s2.searchByStruct.path:/api/semantic/query/struct}")
    private String searchByStructPath;

    @Value("${s2.searchByStruct.path:/api/semantic/query/multiStruct}")
    private String searchByMultiStructPath;

    @Value("${s2.searchByStruct.path:/api/semantic/query/sql}")
    private String searchBySqlPath;

    @Value("${s2.searchByStruct.path:/api/semantic/query/queryDimValue}")
    private String queryDimValuePath;

    @Value("${s2.fetchModelSchemaPath.path:/api/semantic/schema}")
    private String fetchModelSchemaPath;

    @Value("${s2.fetchModelList.path:/api/semantic/schema/dimension/page}")
    private String fetchDimensionPagePath;

    @Value("${s2.fetchModelList.path:/api/semantic/schema/metric/page}")
    private String fetchMetricPagePath;

    @Value("${s2.fetchModelList.path:/api/semantic/schema/domain/list}")
    private String fetchDomainListPath;

    @Value("${s2.fetchModelList.path:/api/semantic/schema/model/list}")
    private String fetchModelListPath;

    @Value("${s2.explain.path:/api/semantic/query/explain}")
    private String explainPath;
}
