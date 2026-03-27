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

import com.alibaba.cloud.ai.headless.common.pojo.enums.DataTypeEnums;
import com.alibaba.cloud.ai.headless.api.pojo.DimValueMap;
import com.alibaba.cloud.ai.headless.api.pojo.DimensionTimeTypeParams;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaItem;
import com.alibaba.cloud.ai.headless.api.pojo.enums.DimensionType;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
public class DimensionResp extends SchemaItem {

    private Long modelId;

    private Long domainId;

    private DimensionType type;

    private String expr;

    private String modelName;

    private String modelBizName;

    private String modelFilterSql;
    // DATE ID CATEGORY
    private String semanticType;

    private String alias;

    private List<String> defaultValues;

    private List<DimValueMap> dimValueMaps;

    private DataTypeEnums dataType;

    private int isTag;

    private DimensionTimeTypeParams typeParams;

    private Map<String, Object> ext = new HashMap<>();

    public boolean isTimeDimension() {
        return DimensionType.isTimeDimension(type);
    }

    public boolean isPartitionTime() {
        return DimensionType.isPartitionTime(type);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
