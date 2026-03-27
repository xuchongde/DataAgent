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
package com.alibaba.cloud.ai.headless.api.pojo;

import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import com.alibaba.cloud.ai.headless.api.pojo.enums.DimensionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dimension {

    private String name;

    private DimensionType type;

    private String expr;

    private String dateFormat = Constants.DAY_FORMAT;

    private DimensionTimeTypeParams typeParams;

    private Integer isCreateDimension = 0;

    private String bizName;

    private String description;

    public Dimension(String name, String bizName, DimensionType type, Integer isCreateDimension) {
        this.name = name;
        this.type = type;
        this.isCreateDimension = isCreateDimension;
        this.bizName = bizName;
        this.expr = bizName;
    }

    public Dimension(String name, String bizName, String expr, DimensionType type,
            Integer isCreateDimension) {
        this.name = name;
        this.type = type;
        this.isCreateDimension = isCreateDimension;
        this.bizName = bizName;
        this.expr = expr;
    }

    public Dimension(String name, String bizName, DimensionType type, Integer isCreateDimension,
            String expr, String dateFormat, DimensionTimeTypeParams typeParams) {
        this.name = name;
        this.type = type;
        this.expr = expr;
        this.dateFormat = dateFormat;
        this.typeParams = typeParams;
        this.isCreateDimension = isCreateDimension;
        this.bizName = bizName;
    }

    public String getFieldName() {
        return expr;
    }
}
