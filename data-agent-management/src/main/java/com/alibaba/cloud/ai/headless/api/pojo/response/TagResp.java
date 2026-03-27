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
package com.alibaba.cloud.ai.headless.api.pojo.response;

import com.alibaba.cloud.ai.headless.common.pojo.RecordInfo;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString(callSuper = true)
public class TagResp extends RecordInfo {

    private Long id;

    private Long domainId;

    private String domainName;

    private Long modelId;

    private String modelName;

    private Long tagObjectId;

    private String tagObjectName;

    private Boolean isCollect;

    private boolean hasAdminRes;

    private String tagDefineType;

    private Long itemId;

    private String name;

    private String bizName;

    private String description;

    private Integer sensitiveLevel;

    private Map<String, Object> ext = new HashMap();
}
