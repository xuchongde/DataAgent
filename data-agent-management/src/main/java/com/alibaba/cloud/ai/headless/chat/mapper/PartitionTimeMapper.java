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

import com.alibaba.cloud.ai.headless.common.pojo.enums.Text2SQLType;
import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElement;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElementMatch;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class PartitionTimeMapper extends BaseMapper {

    @Override
    public boolean accept(ChatQueryContext chatQueryContext) {
        return !(chatQueryContext.getRequest().getText2SQLType().equals(Text2SQLType.ONLY_RULE)
                || chatQueryContext.getMapInfo().isEmpty());
    }

    @Override
    public void doMap(ChatQueryContext chatQueryContext) {
        Map<Long, DataSetSchema> schemaMap =
                chatQueryContext.getSemanticSchema().getDataSetSchemaMap();
        for (Map.Entry<Long, DataSetSchema> entry : schemaMap.entrySet()) {
            List<SchemaElement> timeDims = entry.getValue().getDimensions().stream()
                    .filter(SchemaElement::isPartitionTime).toList();
            for (SchemaElement schemaElement : timeDims) {
                chatQueryContext.getMapInfo().getMatchedElements(entry.getKey())
                        .add(SchemaElementMatch.builder().word(schemaElement.getName())
                                .element(schemaElement).detectWord(schemaElement.getName())
                                .similarity(1.0).build());
            }
        }
    }

}
