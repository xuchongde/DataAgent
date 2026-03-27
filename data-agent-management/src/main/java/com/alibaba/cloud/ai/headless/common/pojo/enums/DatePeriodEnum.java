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
package com.alibaba.cloud.ai.headless.common.pojo.enums;

public enum DatePeriodEnum {
    DAY("天"), WEEK("周"), MONTH("月"), QUARTER("季度"), YEAR("年");

    private String chName;

    DatePeriodEnum(String chName) {
        this.chName = chName;
    }

    public String getChName() {
        return chName;
    }

    public static DatePeriodEnum get(String period) {
        for (DatePeriodEnum value : values()) {
            if (value.name().equalsIgnoreCase(period)) {
                return value;
            }
        }
        return null;
    }

    public static DatePeriodEnum fromChName(String chName) {
        for (DatePeriodEnum value : values()) {
            if (value.chName.equals(chName)) {
                return value;
            }
        }
        return null;
    }
}
