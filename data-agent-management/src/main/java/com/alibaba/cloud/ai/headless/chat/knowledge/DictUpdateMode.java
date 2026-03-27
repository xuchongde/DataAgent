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

public enum DictUpdateMode {
    OFFLINE_FULL("OFFLINE_FULL"),
    OFFLINE_MODEL("OFFLINE_MODEL"),
    REALTIME_ADD("REALTIME_ADD"),
    REALTIME_DELETE("REALTIME_DELETE"),
    NOT_SUPPORT("NOT_SUPPORT");

    private String value;

    DictUpdateMode(String value) {
        this.value = value;
    }

    public static DictUpdateMode of(String value) {
        for (DictUpdateMode item : DictUpdateMode.values()) {
            if (item.value.equalsIgnoreCase(value)) {
                return item;
            }
        }
        return DictUpdateMode.NOT_SUPPORT;
    }

    public String getValue() {
        return value;
    }
}
