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


import com.alibaba.cloud.ai.headless.common.pojo.enums.StatusEnum;
import com.alibaba.cloud.ai.headless.common.pojo.enums.TypeEnums;
import com.alibaba.cloud.ai.headless.api.pojo.ItemValueConfig;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictItemReq {

    private Long id;
    @NotNull
    private TypeEnums type;
    @NotNull
    private Long itemId;
    private ItemValueConfig config;

    /** ONLINE - 正常更新 OFFLINE - 停止更新,但字典文件不删除 DELETED - 停止更新,且删除字典文件 */
    @NotNull
    private StatusEnum status;
}
