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
package com.alibaba.cloud.ai.headless.server.pojo.yaml;

import com.alibaba.cloud.ai.headless.common.pojo.enums.DataTypeEnums;
import com.alibaba.cloud.ai.headless.api.pojo.DimensionTimeTypeParams;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DimensionYamlTpl {

    private String name;

    private String bizName;

    private String owners;

    private String type;

    private String expr;

    private DimensionTimeTypeParams typeParams;

    private DataTypeEnums dataType;

    private List<String> defaultValues;

    private Map<String, Object> ext;
}
