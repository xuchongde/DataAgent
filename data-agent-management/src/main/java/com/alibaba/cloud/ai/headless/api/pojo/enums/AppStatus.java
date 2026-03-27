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

public enum AppStatus {
    INIT(0), ONLINE(1), OFFLINE(2), DELETED(3), UNKNOWN(4);

    private Integer code;

    AppStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static AppStatus fromCode(Integer code) {
        for (AppStatus appStatusEnum : AppStatus.values()) {
            if (appStatusEnum.getCode().equals(code)) {
                return appStatusEnum;
            }
        }
        return AppStatus.UNKNOWN;
    }
}
