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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DateInfoReq {

    private String type;
    private Long itemId;
    private String dateFormat;
    private String startDate;
    private String endDate;
    private String datePeriod;
    private List<String> unavailableDateList = new ArrayList<>();

    public DateInfoReq(String type, Long itemId, String dateFormat, String startDate,
            String endDate) {
        this.type = type;
        this.itemId = itemId;
        this.dateFormat = dateFormat;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public DateInfoReq(String type, Long itemId, String dateFormat, String startDate,
            String endDate, List<String> unavailableDateList) {
        this.type = type;
        this.itemId = itemId;
        this.dateFormat = dateFormat;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unavailableDateList = unavailableDateList;
    }
}
