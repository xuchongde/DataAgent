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

import com.alibaba.cloud.ai.headless.api.pojo.SchemaElementType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Data
@Setter
@Getter
@Builder
public class SearchResult {

    private String recommend;

    private String subRecommend;

    private Long dataSetId;

    private String dataSetName;

    private SchemaElementType schemaElementType;

    private boolean isComplete = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchResult searchResult1 = (SearchResult) o;
        return Objects.equals(recommend, searchResult1.recommend)
                && Objects.equals(dataSetName, searchResult1.dataSetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recommend, dataSetName);
    }
}
