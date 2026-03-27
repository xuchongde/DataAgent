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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.cloud.ai.headless.api.pojo.Cache;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@ToString
public class QueryMultiStructReq extends SemanticQueryReq {

    List<QueryStructReq> queryStructReqs;

    public String toCustomizedString() {
        return JSONObject.toJSONString(queryStructReqs);
    }

    public String generateCommandMd5() {
        return DigestUtils.md5Hex(this.toCustomizedString());
    }

    public Long getViewId() {
        if (CollectionUtils.isEmpty(this.getQueryStructReqs())) {
            return null;
        }
        return this.getQueryStructReqs().get(0).getDataSetId();
    }

    public Cache getCacheInfo() {
        if (CollectionUtils.isEmpty(this.getQueryStructReqs())) {
            return getCacheInfo();
        }
        return this.getQueryStructReqs().get(0).getCacheInfo();
    }
}
