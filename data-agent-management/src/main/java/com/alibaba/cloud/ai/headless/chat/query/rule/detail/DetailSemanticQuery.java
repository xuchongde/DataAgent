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
package com.alibaba.cloud.ai.headless.chat.query.rule.detail;

import com.alibaba.cloud.ai.headless.common.pojo.DateConf;
import com.alibaba.cloud.ai.headless.common.pojo.enums.TimeMode;
import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElement;
import com.alibaba.cloud.ai.headless.api.pojo.TimeDefaultConfig;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.query.rule.RuleSemanticQuery;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class DetailSemanticQuery extends RuleSemanticQuery {

    public DetailSemanticQuery() {
        super();
    }

    @Override
    public void fillParseInfo(ChatQueryContext chatQueryContext, Long dataSetId) {
        super.fillParseInfo(chatQueryContext, dataSetId);

        parseInfo.setLimit(parseInfo.getDetailLimit());
        if (!needFillDateConf(chatQueryContext)) {
            return;
        }
        Map<Long, DataSetSchema> dataSetSchemaMap =
                chatQueryContext.getSemanticSchema().getDataSetSchemaMap();
        DataSetSchema dataSetSchema = dataSetSchemaMap.get(parseInfo.getDataSetId());
        TimeDefaultConfig timeDefaultConfig = dataSetSchema.getDetailTypeTimeDefaultConfig();
        SchemaElement partitionDimension = dataSetSchema.getPartitionDimension();

        if (Objects.nonNull(partitionDimension) && Objects.nonNull(timeDefaultConfig)
                && Objects.nonNull(timeDefaultConfig.getUnit())
                && timeDefaultConfig.getUnit() != -1) {
            DateConf dateInfo = new DateConf();
            dateInfo.setDateField(partitionDimension.getName());
            int unit = timeDefaultConfig.getUnit();
            String startDate = LocalDate.now().minusDays(unit).toString();
            String endDate = startDate;
            dateInfo.setDateMode(DateConf.DateMode.BETWEEN);
            if (TimeMode.RECENT.equals(timeDefaultConfig.getTimeMode())) {
                endDate = LocalDate.now().toString();
            }
            dateInfo.setUnit(unit);
            dateInfo.setPeriod(timeDefaultConfig.getPeriod());
            dateInfo.setStartDate(startDate);
            dateInfo.setEndDate(endDate);
            parseInfo.setDateInfo(dateInfo);
        }
    }
}
