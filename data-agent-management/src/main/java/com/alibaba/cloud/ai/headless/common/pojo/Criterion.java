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
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Criterion {

    private String column;

    private FilterOperatorEnum operator;

    private Object value;

    private List<Object> values;

    private String dataType;

    public Criterion(String column, FilterOperatorEnum operator, Object value, String dataType) {
        super();
        this.column = column;
        this.operator = operator;
        this.value = value;
        this.dataType = dataType;

        if (FilterOperatorEnum.BETWEEN.name().equals(operator)
                || FilterOperatorEnum.IN.name().equals(operator)
                || FilterOperatorEnum.NOT_IN.name().equals(operator)) {
            this.values = (List) value;
        }
    }

    public boolean isNeedApostrophe() {
        return Arrays.stream(StringDataType.values())
                .filter(value -> this.dataType.equalsIgnoreCase(value.getType())).findFirst()
                .isPresent();
    }

    public enum NumericDataType {
        TINYINT("TINYINT"),
        SMALLINT("SMALLINT"),
        MEDIUMINT("MEDIUMINT"),
        INT("INT"),
        INTEGER("INTEGER"),
        BIGINT("BIGINT"),
        FLOAT("FLOAT"),
        DOUBLE("DOUBLE"),
        DECIMAL("DECIMAL"),
        NUMERIC("NUMERIC"),;

        private String type;

        NumericDataType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public enum StringDataType {
        VARCHAR("VARCHAR"), STRING("STRING"),;

        private String type;

        StringDataType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
