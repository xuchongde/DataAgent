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
package com.alibaba.cloud.ai.headless.chat.knowledge;

import com.google.common.base.Objects;
import com.alibaba.cloud.ai.headless.common.pojo.Constants;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class HanlpMapResult extends MapResult {
    private List<String> natures;

    public HanlpMapResult(String name, List<String> natures, String detectWord, double similarity) {
        this.name = name;
        this.natures = natures;
        this.detectWord = detectWord;
        this.similarity = similarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HanlpMapResult hanlpMapResult = (HanlpMapResult) o;
        return Objects.equal(name, hanlpMapResult.name)
                && Objects.equal(natures, hanlpMapResult.natures);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, natures);
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String getMapKey() {
        return this.getName() + Constants.UNDERLINE
                + String.join(Constants.UNDERLINE, this.getNatures());
    }
}
