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
package com.alibaba.cloud.ai.headless.api.pojo.response;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ParseResp implements Serializable {
    private final String queryText;
    private ParseState state = ParseState.PENDING;
    private String errorMsg;
    private List<SemanticParseInfo> selectedParses = Lists.newArrayList();
    private ParseTimeCostResp parseTimeCost = new ParseTimeCostResp();

    public enum ParseState {
        COMPLETED, PENDING, FAILED
    }

    public ParseResp(String queryText) {
        this.queryText = queryText;
        parseTimeCost.setParseStartTime(System.currentTimeMillis());
    }

    public List<SemanticParseInfo> getSelectedParses() {
        selectedParses = selectedParses.stream()
                .sorted(Comparator.comparingDouble(SemanticParseInfo::getScore).reversed())
                .collect(Collectors.toList());
        generateParseInfoId(selectedParses);
        return selectedParses;
    }

    private void generateParseInfoId(List<SemanticParseInfo> selectedParses) {
        for (int i = 0; i < selectedParses.size(); i++) {
            SemanticParseInfo parseInfo = selectedParses.get(i);
            parseInfo.setId(i + 1);
        }
    }
}
