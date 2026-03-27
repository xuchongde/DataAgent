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

import com.alibaba.cloud.ai.headless.api.pojo.Field;
import com.alibaba.cloud.ai.headless.api.pojo.SqlVariable;
import com.alibaba.cloud.ai.headless.api.pojo.enums.ModelSourceType;
import lombok.Data;

import java.util.List;

@Data
public class DataModelYamlTpl {

    private Long id;

    private String name;

    private Long sourceId;

    private String type;

    private String sqlQuery;

    private String tableQuery;

    private String filterSql;

    private List<IdentifyYamlTpl> identifiers;

    private List<DimensionYamlTpl> dimensions;

    private List<MeasureYamlTpl> measures;

    private List<Field> fields;

    private ModelSourceType modelSourceTypeEnum;

    private List<SqlVariable> sqlVariables;
}
