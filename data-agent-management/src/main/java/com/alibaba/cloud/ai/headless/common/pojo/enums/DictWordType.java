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
package com.alibaba.cloud.ai.headless.common.pojo.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/** * nature type such as : metric、dimension etc. */
public enum DictWordType {
    METRIC("metric"),

    DIMENSION("dimension"),

    VALUE("value"),

    DATASET("dataSet"),

    NUMBER("m"),

    TAG("tag"),

    TERM("term"),

    SUFFIX("suffix");

    public static final String NATURE_SPILT = "_";
    public static final String SPACE = " ";
    private String type;

    DictWordType(String type) {
        this.type = type;
    }

    public String getType() {
        return NATURE_SPILT + type;
    }

    public static DictWordType getNatureType(String nature) {
        if (StringUtils.isEmpty(nature) || !nature.startsWith(NATURE_SPILT)) {
            return null;
        }
        for (DictWordType dictWordType : values()) {
            if (nature.endsWith(dictWordType.getType())) {
                return dictWordType;
            }
        }
        // dataSet
        String[] natures = nature.split(DictWordType.NATURE_SPILT);
        if (natures.length == 2 && StringUtils.isNumeric(natures[1])) {
            return DATASET;
        }
        // dimension value
        if (natures.length == 3 && StringUtils.isNumeric(natures[1])
                && StringUtils.isNumeric(natures[2])) {
            return VALUE;
        }
        return null;
    }

    public static DictWordType of(TypeEnums type) {
        for (DictWordType wordType : DictWordType.values()) {
            if (wordType.name().equalsIgnoreCase(type.name())) {
                return wordType;
            }
        }
        return null;
    }

    public static String getSuffixNature(TypeEnums type) {
        DictWordType wordType = of(type);
        if (Objects.nonNull(wordType)) {
            return wordType.type;
        }
        return "";
    }
}
