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

import com.google.common.base.Objects;
import com.alibaba.cloud.ai.headless.common.pojo.enums.FilterOperatorEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
public class QueryFilter implements Serializable {

    private String bizName;

    private String name;

    private FilterOperatorEnum operator = FilterOperatorEnum.EQUALS;

    private Object value;

    private Long elementID;

    private String function;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryFilter that = (QueryFilter) o;
        return Objects.equal(bizName, that.bizName) && Objects.equal(name, that.name)
                && operator == that.operator && Objects.equal(value, that.value)
                && Objects.equal(function, that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bizName, name, operator, value, function);
    }
}
