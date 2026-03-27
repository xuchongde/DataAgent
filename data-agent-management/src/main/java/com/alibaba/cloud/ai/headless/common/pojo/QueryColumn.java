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
package com.alibaba.cloud.ai.headless.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryColumn {

    private String name;
    private String type;
    private String bizName;
    private String nameEn;
    private String showType;
    private Boolean authorized = true;
    private String dataFormatType;
    private DataFormat dataFormat;
    private String comment;
    private Long modelId;

    public QueryColumn(String bizName, String type) {
        this.type = type;
        this.bizName = bizName;
        this.nameEn = bizName;
        this.name = bizName;
    }

    public QueryColumn(String name, String type, String bizName) {
        this.name = name;
        this.type = type;
        this.bizName = bizName;
        this.nameEn = bizName;
        this.showType = "CATEGORY";
    }

    public void setType(String type) {
        this.type = type == null ? null : type;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
        this.nameEn = bizName;
    }
}
