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
package com.alibaba.cloud.ai.headless.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class SchemaMapInfo implements Serializable {

    private final Map<Long, List<SchemaElementMatch>> dataSetElementMatches = new HashMap<>();

    public boolean isEmpty() {
        return dataSetElementMatches.keySet().isEmpty();
    }

    public Set<Long> getMatchedDataSetInfos() {
        return dataSetElementMatches.keySet();
    }

    public List<SchemaElementMatch> getMatchedElements(Long dataSet) {
        return dataSetElementMatches.getOrDefault(dataSet, Lists.newArrayList());
    }

    public void setMatchedElements(Long dataSet, List<SchemaElementMatch> elementMatches) {
        dataSetElementMatches.put(dataSet, elementMatches);
    }

    public void addMatchedElements(SchemaMapInfo schemaMapInfo) {
        for (Map.Entry<Long, List<SchemaElementMatch>> entry : schemaMapInfo.dataSetElementMatches
                .entrySet()) {
            Long dataSet = entry.getKey();
            List<SchemaElementMatch> newMatches = entry.getValue();

            if (dataSetElementMatches.containsKey(dataSet)) {
                List<SchemaElementMatch> existingMatches = dataSetElementMatches.get(dataSet);
                Set<SchemaElementMatch> mergedMatches = new HashSet<>(existingMatches);
                mergedMatches.addAll(newMatches);
                dataSetElementMatches.put(dataSet, new ArrayList<>(mergedMatches));
            } else {
                dataSetElementMatches.put(dataSet, new ArrayList<>(new HashSet<>(newMatches)));
            }
        }
    }

    @JsonIgnore
    public List<SchemaElement> getTermDescriptionToMap() {
        List<SchemaElement> termElements = Lists.newArrayList();
        for (Long dataSetId : getDataSetElementMatches().keySet()) {
            List<SchemaElementMatch> matchedElements = getMatchedElements(dataSetId);
            for (SchemaElementMatch schemaElementMatch : matchedElements) {
                if (SchemaElementType.TERM.equals(schemaElementMatch.getElement().getType())
                        && schemaElementMatch.isFullMatched()) {
                    termElements.add(schemaElementMatch.getElement());
                }
            }
        }
        return termElements;
    }
}
