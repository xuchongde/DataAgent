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
package com.alibaba.cloud.ai.headless.api.pojo.enums;

import java.util.Objects;

public enum TagType {
    ATOMIC, DERIVED;

    public static TagType of(String src) {
        for (TagType tagType : TagType.values()) {
            if (Objects.nonNull(src) && src.equalsIgnoreCase(tagType.name())) {
                return tagType;
            }
        }
        return null;
    }

    public static Boolean isDerived(String src) {
        TagType tagType = of(src);
        return Objects.nonNull(tagType) && tagType.equals(DERIVED);
    }

    public static TagType getType(TagDefineType tagDefineType) {
        return Objects.nonNull(tagDefineType) && TagDefineType.TAG.equals(tagDefineType)
                ? TagType.DERIVED
                : TagType.ATOMIC;
    }
}
