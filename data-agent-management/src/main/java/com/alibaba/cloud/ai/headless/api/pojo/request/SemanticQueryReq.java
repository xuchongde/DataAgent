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
package com.alibaba.cloud.ai.headless.api.pojo.request;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.api.pojo.Cache;
import com.alibaba.cloud.ai.headless.api.pojo.Param;
import com.alibaba.cloud.ai.headless.api.pojo.SqlInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Slf4j
public abstract class SemanticQueryReq {

    protected boolean needAuth = true;

    protected boolean innerLayerNative = false;

    protected Long dataSetId;

    protected String dataSetName;

    protected Set<Long> modelIds = new HashSet<>();

    protected List<Param> params = new ArrayList<>();

    protected Cache cacheInfo = new Cache();

    protected SqlInfo sqlInfo = new SqlInfo();

    public void addModelId(Long modelId) {
        modelIds.add(modelId);
    }

    public String generateCommandMd5() {
        return DigestUtils.md5Hex(this.toCustomizedString());
    }

    public abstract String toCustomizedString();

    public List<Long> getModelIds() {
        return Lists.newArrayList(modelIds);
    }

    public Set<Long> getModelIdSet() {
        return modelIds;
    }
}
