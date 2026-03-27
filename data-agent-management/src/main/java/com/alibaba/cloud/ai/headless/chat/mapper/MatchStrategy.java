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
package com.alibaba.cloud.ai.headless.chat.mapper;

import com.alibaba.cloud.ai.headless.api.pojo.response.S2Term;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.knowledge.MapResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MatchStrategy encapsulates a concrete matching algorithm executed during query or search process.
 */
public interface MatchStrategy<T extends MapResult> {

    Map<MatchText, List<T>> match(ChatQueryContext chatQueryContext, List<S2Term> terms,
            Set<Long> detectDataSetIds);
}
