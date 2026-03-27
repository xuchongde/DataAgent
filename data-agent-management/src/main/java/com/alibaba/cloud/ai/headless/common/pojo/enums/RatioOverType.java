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

public enum RatioOverType {
    DAY_ON_DAY("日环比"),
    WEEK_ON_DAY("周环比"),
    WEEK_ON_WEEK("周环比"),
    MONTH_ON_WEEK("月环比"),
    MONTH_ON_MONTH("月环比"),
    YEAR_ON_MONTH("年同比"),
    YEAR_ON_YEAR("年环比");

    private String showName;

    RatioOverType(String showName) {
        this.showName = showName;
    }

    public String getShowName() {
        return showName;
    }
}
