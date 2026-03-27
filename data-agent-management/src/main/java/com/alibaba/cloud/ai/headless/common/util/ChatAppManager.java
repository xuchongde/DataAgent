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
package com.alibaba.cloud.ai.headless.common.util;

import com.google.common.collect.Maps;
import com.alibaba.cloud.ai.headless.common.pojo.ChatApp;
import com.alibaba.cloud.ai.headless.common.pojo.enums.AppModule;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatAppManager {
    private static final Map<String, ChatApp> chatApps = Maps.newConcurrentMap();

    public static void register(String key, ChatApp app) {
        chatApps.put(key, app);
    }

    public static Map<String, ChatApp> getAllApps(AppModule appType) {
        return chatApps.entrySet().stream().filter(e -> e.getValue().getAppModule().equals(appType))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public static Optional<ChatApp> getApp(String appKey) {
        return chatApps.entrySet().stream().filter(e -> e.getKey().equals(appKey))
                .map(Map.Entry::getValue).findFirst();
    }
}
