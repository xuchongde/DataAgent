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

import com.alibaba.cloud.ai.headless.common.pojo.enums.AggOperatorEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class Aggregator {

    @NotBlank(message = "Invalid aggregator column")
    private String column;

    private AggOperatorEnum func = AggOperatorEnum.SUM;

    private String nameCh;

    private List<String> args;

    private String alias;

    public Aggregator() {}

    public Aggregator(String column, AggOperatorEnum func) {
        this.column = column;
        this.func = func;
    }

    public Aggregator(String column, AggOperatorEnum func, List<String> args) {
        this.column = column;
        this.func = func;
        this.args = args;
    }

    public Aggregator(String column, AggOperatorEnum func, String alias) {
        this.column = column;
        this.func = func;
        this.alias = alias;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"column\":\"").append(column).append('\"');
        sb.append(",\"func\":").append(func);
        sb.append(",\"nameCh\":\"").append(nameCh).append('\"');
        sb.append(",\"args\":").append(args);
        sb.append(",\"alias\":").append(alias);
        sb.append('}');
        return sb.toString();
    }
}
