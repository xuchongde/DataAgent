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

import com.alibaba.cloud.ai.headless.common.pojo.enums.FilterOperatorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Filter {

    private Relation relation = Relation.FILTER;
    private Long id;
    private String bizName;
    private String name;
    private FilterOperatorEnum operator;
    private Object value;
    private List<Filter> children;

    public Filter(String bizName, FilterOperatorEnum operator, Object value) {
        this.bizName = bizName;
        this.operator = operator;
        this.value = value;
    }

    public Filter(Relation relation, String bizName, FilterOperatorEnum operator, Object value) {
        this.relation = relation;
        this.bizName = bizName;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"relation\":").append(relation);
        sb.append(",\"bizName\":\"").append(bizName).append('\"');
        sb.append(",\"name\":\"").append(name).append('\"');
        sb.append(",\"operator\":").append(operator);
        sb.append(",\"value\":").append(value);
        sb.append(",\"children\":").append(children);
        sb.append('}');
        return sb.toString();
    }

    public enum Relation {
        FILTER, OR, AND
    }
}
