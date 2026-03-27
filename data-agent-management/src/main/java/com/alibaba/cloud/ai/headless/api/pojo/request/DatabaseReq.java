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
import com.alibaba.cloud.ai.headless.common.pojo.RecordInfo;
import lombok.Data;

import java.util.List;

@Data
public class DatabaseReq extends RecordInfo {

    private Long id;

    private String name;

    private String type;

    private String host;

    private String port;

    private String username;

    private String password;

    private String database;

    private String databaseType;

    private String version;

    private String description;

    private String schema;

    private String url;

    private List<String> admins = Lists.newArrayList();

    private List<String> viewers = Lists.newArrayList();

    private Integer isOpen = 0;
}
