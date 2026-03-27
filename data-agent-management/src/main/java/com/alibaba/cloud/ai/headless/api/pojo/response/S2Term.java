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

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import lombok.Data;

@Data
public class S2Term {

    public String word;

    public Nature nature;
    public int offset;
    public int frequency = 0;

    public S2Term() {}

    public S2Term(String word, Nature nature) {
        this.word = word;
        this.nature = nature;
    }

    public S2Term(String word, Nature nature, int offset) {
        this.word = word;
        this.nature = nature;
        this.offset = offset;
    }

    public S2Term(String word, Nature nature, int offset, int frequency) {
        this.word = word;
        this.nature = nature;
        this.offset = offset;
        this.frequency = frequency;
    }

    public int length() {
        return this.word.length();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Term) {
            Term term = (Term) obj;
            if (this.nature == term.nature && this.word.equals(term.word)) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
