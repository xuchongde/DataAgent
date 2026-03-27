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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class ItemUseReq {

    private String startTime;
    private Long modelId;
    private Long dataSetId;
    private List<Long> dataSetIds;
    private List<Long> modelIds;
    private Boolean cacheEnable = true;
    private String metric;

    public ItemUseReq(String startTime, Long modelId) {
        this.startTime = startTime;
        this.modelId = modelId;
    }

    public ItemUseReq(String startTime, List<Long> modelIds) {
        this.startTime = startTime;
        this.modelIds = modelIds;
    }
}
