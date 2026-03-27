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

import com.alibaba.cloud.ai.headless.common.pojo.enums.EventType;
import lombok.Data;

import java.util.List;

@Data
public class MetaBatchReq {

    private List<Long> ids;

    private List<String> bizNames;

    private List<Long> modelIds;

    /** 最后变更的状态 */
    private Integer status;

    /** 批量执行分类信息 */
    private EventType type;

    private List<String> classifications;

    private Integer sensitiveLevel;
}
