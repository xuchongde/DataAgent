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
package com.alibaba.cloud.ai.headless.core.cache;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CacheCommonConfig {

    @Value("${s2.cache.common.app:supersonic}")
    private String cacheCommonApp;

    @Value("${s2.cache.common.env:dev}")
    private String cacheCommonEnv;

    @Value("${s2.cache.common.version:0}")
    private Integer cacheCommonVersion;

    @Value("${s2.cache.common.expire.after.write:10}")
    private Integer cacheCommonExpireAfterWrite;

    @Value("${s2.query.cache.enable:true}")
    private Boolean cacheEnable;
}
