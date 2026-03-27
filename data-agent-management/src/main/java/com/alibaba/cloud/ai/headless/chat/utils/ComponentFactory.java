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
package com.alibaba.cloud.ai.headless.chat.utils;

import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.chat.corrector.SemanticCorrector;
import com.alibaba.cloud.ai.headless.chat.mapper.SchemaMapper;
import com.alibaba.cloud.ai.headless.chat.parser.SemanticParser;
import com.alibaba.cloud.ai.headless.chat.parser.llm.DataSetResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * QueryConverter QueryOptimizer QueryExecutor object factory
 */
@Slf4j
public class ComponentFactory {
    private static List<SchemaMapper> schemaMappers = new ArrayList<>();
    private static List<SemanticParser> semanticParsers = new ArrayList<>();
    private static List<SemanticCorrector> semanticCorrectors = new ArrayList<>();
    private static DataSetResolver modelResolver;

    public static List<SchemaMapper> getSchemaMappers() {
        return CollectionUtils.isEmpty(schemaMappers) ? init(SchemaMapper.class, schemaMappers)
                : schemaMappers;
    }

    public static List<SemanticParser> getSemanticParsers() {
        return CollectionUtils.isEmpty(semanticParsers)
                ? init(SemanticParser.class, semanticParsers)
                : semanticParsers;
    }

    public static List<SemanticCorrector> getSemanticCorrectors() {
        return CollectionUtils.isEmpty(semanticCorrectors)
                ? init(SemanticCorrector.class, semanticCorrectors)
                : semanticCorrectors;
    }

    public static DataSetResolver getModelResolver() {
        if (Objects.isNull(modelResolver)) {
            modelResolver = init(DataSetResolver.class);
        }
        return modelResolver;
    }

    public static <T> T getBean(String name, Class<T> tClass) {
        return ContextUtils.getContext().getBean(name, tClass);
    }

    protected static <T> List<T> init(Class<T> factoryType, List list) {
        list.addAll(SpringFactoriesLoader.loadFactories(factoryType,
                Thread.currentThread().getContextClassLoader()));
        return list;
    }

    protected static <T> T init(Class<T> factoryType) {
        return SpringFactoriesLoader
                .loadFactories(factoryType, Thread.currentThread().getContextClassLoader()).get(0);
    }
}
