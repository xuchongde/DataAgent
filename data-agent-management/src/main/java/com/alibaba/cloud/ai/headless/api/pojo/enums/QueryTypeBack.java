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
package com.alibaba.cloud.ai.headless.api.pojo.enums;

public enum QueryTypeBack {
    NORMAL("NORMAL", 0),

    PRE_FLUSH("PRE_FLUSH", 1);

    private String value;
    private Integer state;

    QueryTypeBack(String value, Integer state) {
        this.value = value;
        this.state = state;
    }

    public static QueryTypeBack of(String src) {
        for (QueryTypeBack operatorEnum : QueryTypeBack.values()) {
            if (src.toUpperCase().contains(operatorEnum.value)) {
                return operatorEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public Integer getState() {
        return state;
    }
}
