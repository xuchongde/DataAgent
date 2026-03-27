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

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CaffeineCacheManager implements CacheManager {

    @Autowired
    private CacheCommonConfig cacheCommonConfig;

    @Autowired
    @Qualifier("caffeineCache")
    private Cache<String, Object> caffeineCache;

    @Override
    public Boolean put(String key, Object value) {
        log.debug("[put caffeineCache] key:{}, value:{}", key, value);
        caffeineCache.put(key, value);
        return true;
    }

    @Override
    public Object get(String key) {
        Object value = caffeineCache.asMap().get(key);
        log.debug("[get caffeineCache] key:{}, value:{}", key, value);
        return value;
    }

    @Override
    public String generateCacheKey(String prefix, String body) {
        if (StringUtils.isEmpty(prefix)) {
            prefix = "-1";
        }
        return Joiner.on(":").join(cacheCommonConfig.getCacheCommonApp(),
                cacheCommonConfig.getCacheCommonEnv(), cacheCommonConfig.getCacheCommonVersion(),
                prefix, body);
    }

    @Override
    public Boolean removeCache(String key) {
        caffeineCache.asMap().remove(key);
        return true;
    }
}
