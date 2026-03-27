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
import com.alibaba.cloud.ai.headless.common.pojo.PageBaseReq;
import com.alibaba.cloud.ai.headless.api.pojo.enums.AppStatus;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class AppQueryReq extends PageBaseReq {

    private String name;

    private List<AppStatus> appStatus;

    private String createdBy;

    public List<Integer> getStatus() {
        if (CollectionUtils.isEmpty(appStatus)) {
            return Lists.newArrayList();
        }
        return appStatus.stream().map(AppStatus::getCode).collect(Collectors.toList());
    }
}
