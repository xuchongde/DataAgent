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
package com.alibaba.cloud.ai.headless.chat.knowledge.builder;

import com.alibaba.cloud.ai.headless.api.pojo.SchemaElement;
import com.alibaba.cloud.ai.headless.chat.knowledge.DictWord;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseWordWithAliasBuilder extends BaseWordBuilder {

    public abstract DictWord getOneWordNature(String word, SchemaElement schemaElement,
            boolean isSuffix);

    public List<DictWord> getOneWordNatureAlias(SchemaElement schemaElement, boolean isSuffix) {
        List<DictWord> dictWords = new ArrayList<>();
        if (CollectionUtils.isEmpty(schemaElement.getAlias())) {
            return dictWords;
        }

        for (String alias : schemaElement.getAlias()) {
            dictWords.add(getOneWordNature(alias, schemaElement, isSuffix));
        }
        return dictWords;
    }
}
