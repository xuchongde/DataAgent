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

import com.alibaba.cloud.ai.headless.common.util.DeepCopyUtil;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElement;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaMapInfo;
import com.alibaba.cloud.ai.headless.chat.ChatQueryContext;
import com.alibaba.cloud.ai.headless.chat.utils.ComponentFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * A mapper that map the description of the term.
 */
@Slf4j
public class TermDescMapper extends BaseMapper {

    @Override
    public boolean accept(ChatQueryContext chatQueryContext) {
        return !(CollectionUtils.isEmpty(chatQueryContext.getMapInfo().getTermDescriptionToMap())
                || chatQueryContext.getRequest().isDescriptionMapped());
    }

    @Override
    public void doMap(ChatQueryContext chatQueryContext) {
        List<SchemaElement> termElements = chatQueryContext.getMapInfo().getTermDescriptionToMap();
        for (SchemaElement schemaElement : termElements) {
            ChatQueryContext queryCtx =
                    buildQueryContext(chatQueryContext, schemaElement.getDescription());
            ComponentFactory.getSchemaMappers().forEach(mapper -> mapper.map(queryCtx));
            chatQueryContext.getMapInfo().addMatchedElements(queryCtx.getMapInfo());
        }
    }

    private static ChatQueryContext buildQueryContext(ChatQueryContext chatQueryContext,
            String queryText) {
        ChatQueryContext queryContext = DeepCopyUtil.deepCopy(chatQueryContext);
        queryContext.getRequest().setQueryText(queryText);
        queryContext.setMapInfo(new SchemaMapInfo());
        queryContext.setSemanticSchema(chatQueryContext.getSemanticSchema());
        queryContext.setModelIdToDataSetIds(chatQueryContext.getModelIdToDataSetIds());
        queryContext.setChatWorkflowState(chatQueryContext.getChatWorkflowState());
        queryContext.getRequest().setDescriptionMapped(true);
        return queryContext;
    }
}
