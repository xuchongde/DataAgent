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

import com.google.common.base.Objects;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

import static com.alibaba.cloud.ai.headless.common.pojo.Constants.ASC_UPPER;

@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Invalid order column")
    private String column;

    private String direction = ASC_UPPER;

    public Order(String column, String direction) {
        this.column = column;
        this.direction = direction;
    }

    public Order() {}

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"column\":\"").append(column).append('\"');
        sb.append(",\"direction\":\"").append(direction).append('\"');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equal(column, order.column) && Objects.equal(direction, order.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(column, direction);
    }
}
