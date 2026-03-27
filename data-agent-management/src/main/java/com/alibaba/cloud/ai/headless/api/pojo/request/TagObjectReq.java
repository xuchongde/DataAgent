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
import com.alibaba.cloud.ai.headless.api.pojo.SchemaItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class TagObjectReq extends SchemaItem {

    @NotNull
    private Long domainId;
    private Map<String, Object> ext = new HashMap<>();

    public String getExtJson() {
        return Objects.nonNull(ext) && ext.size() > 0 ? JSONObject.toJSONString(ext) : "";
    }
}
