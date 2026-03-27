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
package com.alibaba.cloud.ai.headless.server.service.impl;

import com.alibaba.cloud.ai.headless.common.pojo.enums.DictWordType;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElement;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticSchema;
import com.alibaba.cloud.ai.headless.chat.knowledge.DictWord;
import com.alibaba.cloud.ai.headless.chat.knowledge.KnowledgeBaseService;
import com.alibaba.cloud.ai.headless.chat.knowledge.builder.WordBuilderFactory;
import com.alibaba.cloud.ai.headless.server.service.HeadlessSchemaService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class DictWordService {

    @Autowired
    private HeadlessSchemaService schemaService;
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    private List<DictWord> preDictWords = new ArrayList<>();

    public void loadDictWord() {
        List<DictWord> dictWords = getAllDictWords();
        setPreDictWords(dictWords);
        knowledgeBaseService.reloadAllData(dictWords);
    }

    public void reloadDictWord() {
        long startTime = System.currentTimeMillis();
        List<DictWord> dictWords = getAllDictWords();
        List<DictWord> preDictWords = getPreDictWords();
        if (org.apache.commons.collections.CollectionUtils.isEqualCollection(dictWords,
                preDictWords)) {
            log.debug("Dictionary hasn't been reloaded.");
            return;
        }
        setPreDictWords(dictWords);
        knowledgeBaseService.reloadAllData(dictWords);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Dictionary has been regularly reloaded in {} milliseconds", duration);
    }

    public List<DictWord> getAllDictWords() {
        SemanticSchema semanticSchema = schemaService.getSemanticSchema();

        List<DictWord> words = new ArrayList<>();

        addWordsByType(DictWordType.DIMENSION, semanticSchema.getDimensions(), words);
        addWordsByType(DictWordType.METRIC, semanticSchema.getMetrics(), words);
        addWordsByType(DictWordType.VALUE, semanticSchema.getDimensionValues(), words);
        addWordsByType(DictWordType.TERM, semanticSchema.getTerms(), words);
        return words;
    }

    public List<DictWord> getDimDictWords(Set<Long> dimIds) {
        SemanticSchema semanticSchema = schemaService.getSemanticSchema();
        List<SchemaElement> requiredDims = semanticSchema.getDimensionValues().stream()
                .filter(dim -> dimIds.contains(dim.getId())).collect(Collectors.toList());
        List<DictWord> words = new ArrayList<>();
        addWordsByType(DictWordType.VALUE, requiredDims, words);
        return words;
    }

    private void addWordsByType(DictWordType value, List<SchemaElement> metas,
            List<DictWord> natures) {
        metas = distinct(metas);
        List<DictWord> natureList = WordBuilderFactory.get(value).getDictWords(metas);
        log.debug("nature type:{} , nature size:{}", value.name(), natureList.size());
        natures.addAll(natureList);
    }

    private List<SchemaElement> distinct(List<SchemaElement> metas) {
        if (CollectionUtils.isEmpty(metas)) {
            return metas;
        }
        if (SchemaElementType.TERM.equals(metas.get(0).getType())) {
            return metas;
        }
        return metas.stream()
                .collect(
                        Collectors.toMap(SchemaElement::getId, Function.identity(), (e1, e2) -> e1))
                .values().stream().collect(Collectors.toList());
    }
}
