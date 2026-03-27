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
package com.alibaba.cloud.ai.headless.core.utils;

import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.core.cache.QueryCache;
import com.alibaba.cloud.ai.headless.core.executor.QueryAccelerator;
import com.alibaba.cloud.ai.headless.core.executor.QueryExecutor;
import com.alibaba.cloud.ai.headless.core.translator.optimizer.QueryOptimizer;
import com.alibaba.cloud.ai.headless.core.translator.parser.QueryParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** QueryConverter QueryOptimizer QueryExecutor object factory */
@Slf4j
public class ComponentFactory {

    private static Map<String, QueryOptimizer> queryOptimizers = new HashMap<>();
    private static List<QueryExecutor> queryExecutors = new ArrayList<>();
    private static List<QueryAccelerator> queryAccelerators = new ArrayList<>();
    private static List<QueryParser> queryParsers = new ArrayList<>();
    private static QueryCache queryCache;

    static {
        initQueryOptimizer();
        initQueryExecutors();
        initQueryAccelerators();
        initQueryParser();
        initQueryCache();
    }

    public static List<QueryOptimizer> getQueryOptimizers() {
        if (queryOptimizers.isEmpty()) {
            initQueryOptimizer();
        }
        return queryOptimizers.values().stream().collect(Collectors.toList());
    }

    public static List<QueryExecutor> getQueryExecutors() {
        if (queryExecutors.isEmpty()) {
            initQueryExecutors();
        }
        return queryExecutors;
    }

    public static List<QueryAccelerator> getQueryAccelerators() {
        if (queryAccelerators.isEmpty()) {
            initQueryAccelerators();
        }
        return queryAccelerators;
    }

    public static List<QueryParser> getQueryParsers() {
        if (queryParsers == null) {
            initQueryParser();
        }
        return queryParsers;
    }

    public static QueryCache getQueryCache() {
        if (queryCache == null) {
            initQueryCache();
        }
        return queryCache;
    }

    public static void addQueryOptimizer(String name, QueryOptimizer queryOptimizer) {
        queryOptimizers.put(name, queryOptimizer);
    }

    private static void initQueryOptimizer() {
        List<QueryOptimizer> queryOptimizerList = new ArrayList<>();
        init(QueryOptimizer.class, queryOptimizerList);
        if (!queryOptimizerList.isEmpty()) {
            queryOptimizerList.stream()
                    .forEach(q -> addQueryOptimizer(q.getClass().getSimpleName(), q));
        }
    }

    private static void initQueryExecutors() {
        // queryExecutors.add(ContextUtils.getContext().getBean("JdbcExecutor",
        // JdbcExecutor.class));
        init(QueryExecutor.class, queryExecutors);
    }

    private static void initQueryAccelerators() {
        // queryExecutors.add(ContextUtils.getContext().getBean("JdbcExecutor",
        // JdbcExecutor.class));
        init(QueryAccelerator.class, queryAccelerators);
    }

    private static void initQueryParser() {
        init(QueryParser.class, queryParsers);
    }

    private static void initQueryCache() {
        queryCache = init(QueryCache.class);
    }

    public static <T> T getBean(String name, Class<T> tClass) {
        return ContextUtils.getContext().getBean(name, tClass);
    }

    private static <T> List<T> init(Class<T> factoryType, List list) {
        list.addAll(SpringFactoriesLoader.loadFactories(factoryType,
                Thread.currentThread().getContextClassLoader()));
        return list;
    }

    private static <T> T init(Class<T> factoryType) {
        return SpringFactoriesLoader
                .loadFactories(factoryType, Thread.currentThread().getContextClassLoader()).get(0);
    }
}
