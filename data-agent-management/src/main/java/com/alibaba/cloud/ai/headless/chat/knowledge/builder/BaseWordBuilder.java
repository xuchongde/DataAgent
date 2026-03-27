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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/** base word nature */
@Slf4j
public abstract class BaseWordBuilder {

    public static final Long DEFAULT_FREQUENCY = 100000L;

    public List<DictWord> getDictWords(List<SchemaElement> schemaElements) {
        List<DictWord> dictWords = new ArrayList<>();
        try {
            dictWords = getDictWordsWithException(schemaElements);
        } catch (Exception e) {
            log.error("getWordNatureList error,", e);
        }
        return dictWords;
    }

    protected List<DictWord> getDictWordsWithException(List<SchemaElement> schemaElements) {

        List<DictWord> dictWords = new ArrayList<>();

        for (SchemaElement schemaElement : schemaElements) {
            dictWords.addAll(doGet(schemaElement.getName(), schemaElement));
        }
        return dictWords;
    }

    protected abstract List<DictWord> doGet(String word, SchemaElement schemaElement);
}
