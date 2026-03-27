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

import com.alibaba.cloud.ai.headless.api.pojo.response.S2Term;
import com.alibaba.cloud.ai.headless.chat.knowledge.helper.NatureHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Service
@Slf4j
public class MapperHelper {

    public Integer getStepIndex(Map<Integer, Integer> regOffsetToLength, Integer index) {
        Integer subRegLength = regOffsetToLength.get(index);
        if (Objects.nonNull(subRegLength)) {
            index = index + subRegLength;
        } else {
            index++;
        }
        return index;
    }

    public Integer getStepOffset(List<S2Term> termList, Integer index) {
        List<Integer> offsetList = termList.stream().sorted(Comparator.comparing(S2Term::getOffset))
                .map(term -> term.getOffset()).collect(Collectors.toList());

        for (int j = 0; j < termList.size() - 1; j++) {
            if (offsetList.get(j) <= index && offsetList.get(j + 1) > index) {
                return offsetList.get(j);
            }
        }
        return index;
    }

    public Map<Integer, Integer> getRegOffsetToLength(List<S2Term> terms) {
        return terms.stream().sorted(Comparator.comparing(S2Term::length)).collect(Collectors
                .toMap(S2Term::getOffset, term -> term.word.length(), (value1, value2) -> value2));
    }

    /**
     * * exist dimension values
     *
     * @param natures
     * @return
     */
    public boolean existDimensionValues(List<String> natures) {
        for (String nature : natures) {
            if (NatureHelper.isDimensionValueDataSetId(nature)) {
                return true;
            }
        }
        return false;
    }
}
