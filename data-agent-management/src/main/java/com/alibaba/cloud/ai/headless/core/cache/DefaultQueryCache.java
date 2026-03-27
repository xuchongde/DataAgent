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

import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.api.pojo.request.SemanticQueryReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultQueryCache implements QueryCache {

    public Object query(SemanticQueryReq semanticQueryReq, String cacheKey) {
        CacheManager cacheManager = ContextUtils.getBean(CacheManager.class);
        if (isCache(semanticQueryReq)) {
            Object result = cacheManager.get(cacheKey);
            if (Objects.nonNull(result)) {
                log.debug("query from cache, key:{},result:{}", cacheKey,
                        StringUtils.normalizeSpace(result.toString()));
            }
            return result;
        }
        return null;
    }

    public Boolean put(String cacheKey, Object value) {
        CacheManager cacheManager = ContextUtils.getBean(CacheManager.class);
        CacheCommonConfig cacheCommonConfig = ContextUtils.getBean(CacheCommonConfig.class);
        if (cacheCommonConfig.getCacheEnable() && Objects.nonNull(value)) {
            CompletableFuture.supplyAsync(() -> cacheManager.put(cacheKey, value))
                    .exceptionally(exception -> {
                        log.warn("exception:", exception);
                        return null;
                    });
            log.debug("put to cache, key: {}", cacheKey);
            return true;
        }
        return false;
    }

    public String getCacheKey(SemanticQueryReq semanticQueryReq) {
        CacheManager cacheManager = ContextUtils.getBean(CacheManager.class);
        String commandMd5 = semanticQueryReq.generateCommandMd5();
        String keyByModelIds = getKeyByModelIds(semanticQueryReq.getModelIds());
        return cacheManager.generateCacheKey(keyByModelIds, commandMd5);
    }

    private String getKeyByModelIds(List<Long> modelIds) {
        return String.join(",",
                modelIds.stream().map(Object::toString).collect(Collectors.toList()));
    }

    private boolean isCache(SemanticQueryReq semanticQueryReq) {
        CacheCommonConfig cacheCommonConfig = ContextUtils.getBean(CacheCommonConfig.class);
        if (!cacheCommonConfig.getCacheEnable()) {
            return false;
        }
        if (semanticQueryReq.getCacheInfo() != null) {
            return semanticQueryReq.getCacheInfo().getCache();
        }
        return false;
    }
}
