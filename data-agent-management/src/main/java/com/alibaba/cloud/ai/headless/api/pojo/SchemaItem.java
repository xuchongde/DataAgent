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

import com.alibaba.cloud.ai.headless.common.pojo.RecordInfo;
import com.alibaba.cloud.ai.headless.common.pojo.enums.SensitiveLevelEnum;
import com.alibaba.cloud.ai.headless.common.pojo.enums.TypeEnums;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@ToString(callSuper = true)
public class SchemaItem extends RecordInfo {

    private static String aliasSplit = ",";

    protected Long id;

    protected String name;

    protected String bizName;

    protected String description;

    protected Integer status;

    protected TypeEnums typeEnum;

    protected Integer sensitiveLevel = SensitiveLevelEnum.LOW.getCode();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchemaItem that = (SchemaItem) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(bizName, that.bizName) && typeEnum == that.typeEnum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, bizName, typeEnum);
    }

    public static List<String> getAliasList(String alias) {
        if (StringUtils.isEmpty(alias)) {
            return new ArrayList<>();
        }
        return Arrays.asList(alias.split(aliasSplit));
    }
}
