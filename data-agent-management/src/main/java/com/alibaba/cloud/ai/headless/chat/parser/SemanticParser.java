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
package com.alibaba.cloud.ai.headless.chat.parser;

import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;

/**
 * A semantic parser understands user queries and generates semantic query statement. SuperSonic
 * leverages a combination of rule-based and LLM-based parsers, each of which deals with specific
 * scenarios.
 */
public interface SemanticParser {

    void parse(ChatQueryContext chatQueryContext);
}
