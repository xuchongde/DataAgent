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

public enum AggOperatorEnum {
    NONE(""),

    MAX("MAX"),

    MIN("MIN"),

    AVG("AVG"),

    SUM("SUM"),

    COUNT("COUNT"),

    COUNT_DISTINCT("COUNT_DISTINCT"),

    DISTINCT("DISTINCT"),

    TOPN("TOPN"),

    PERCENTILE("PERCENTILE"),

    RATIO_ROLL("RATIO_ROLL"),

    RATIO_OVER("RATIO_OVER"),

    UNKNOWN("UNKNOWN");

    private String operator;

    AggOperatorEnum(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public static AggOperatorEnum of(String agg) {
        for (AggOperatorEnum aggOperatorEnum : AggOperatorEnum.values()) {
            if (aggOperatorEnum.getOperator().equalsIgnoreCase(agg)) {
                return aggOperatorEnum;
            }
        }
        return AggOperatorEnum.UNKNOWN;
    }

    /**
     * Determine if aggType is count_Distinct type 1.outer SQL parses the count_distinct(field)
     * operator as count(DISTINCT field). 2.tableSQL generates aggregation that ignores the
     * count_distinct operator.
     *
     * @param aggType aggType
     * @return is count_Distinct type or not
     */
    public static boolean isCountDistinct(String aggType) {
        return null != aggType && aggType.toUpperCase().equals(COUNT_DISTINCT.getOperator());
    }
}
