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
package com.alibaba.cloud.ai.headless.chat.mapper;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElement;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElementMatch;
import com.alibaba.cloud.ai.headless.api.pojo.enums.MapModeEnum;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class AllFieldMapper extends BaseMapper {

    @Override
    public boolean accept(ChatQueryContext chatQueryContext) {
        return MapModeEnum.ALL.equals(chatQueryContext.getRequest().getMapModeEnum());
    }

    @Override
    public void doMap(ChatQueryContext chatQueryContext) {
        Map<Long, DataSetSchema> schemaMap =
                chatQueryContext.getSemanticSchema().getDataSetSchemaMap();
        for (Map.Entry<Long, DataSetSchema> entry : schemaMap.entrySet()) {
            List<SchemaElement> schemaElements = Lists.newArrayList();
            schemaElements.addAll(entry.getValue().getDimensions());
            schemaElements.addAll(entry.getValue().getMetrics());

            List<SchemaElementMatch> allMatches = Lists.newArrayList();
            for (SchemaElement schemaElement : schemaElements) {
                allMatches.add(SchemaElementMatch.builder().word(schemaElement.getName())
                        .element(schemaElement).detectWord(schemaElement.getName()).similarity(0.1)
                        .build());
            }
            chatQueryContext.getMapInfo().setMatchedElements(entry.getKey(), allMatches);
        }
    }

}
