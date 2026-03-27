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

import com.alibaba.cloud.ai.headless.common.pojo.enums.DataTypeEnums;
import com.alibaba.cloud.ai.headless.api.pojo.DimValueMap;
import com.alibaba.cloud.ai.headless.api.pojo.DimensionTimeTypeParams;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DimensionReq extends SchemaItem {

    private Long modelId;

    private String type;

    @NotNull(message = "expr can not be null")
    private String expr;

    // DATE ID CATEGORY
    private String semanticType = "CATEGORY";

    private String alias;

    private List<String> defaultValues;

    private List<DimValueMap> dimValueMaps;

    private DataTypeEnums dataType;

    private Map<String, Object> ext = new HashMap();

    private DimensionTimeTypeParams typeParams;
}
