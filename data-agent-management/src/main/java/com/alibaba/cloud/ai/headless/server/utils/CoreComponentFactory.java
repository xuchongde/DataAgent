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
package com.alibaba.cloud.ai.headless.server.utils;

import com.alibaba.cloud.ai.headless.chat.utils.ComponentFactory;
import com.alibaba.cloud.ai.headless.server.modeller.SemanticModeller;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryConverter QueryOptimizer QueryExecutor object factory
 */
@Slf4j
public class CoreComponentFactory extends ComponentFactory {

    private static List<SemanticModeller> semanticModellers = new ArrayList<>();

    static {
        init(SemanticModeller.class, semanticModellers);
    }

    public static List<SemanticModeller> getSemanticModellers() {
        return semanticModellers;
    }

}
