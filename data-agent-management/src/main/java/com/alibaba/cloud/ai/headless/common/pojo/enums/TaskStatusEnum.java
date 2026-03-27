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

public enum TaskStatusEnum {
    INITIAL("initial", -2),

    ERROR("error", -1),

    PENDING("pending", 0),

    RUNNING("running", 1),

    SUCCESS("success", 2),

    UNKNOWN("unknown", 3);

    private String status;
    private Integer code;

    TaskStatusEnum(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public Integer getCode() {
        return code;
    }

    public static TaskStatusEnum of(String status) {
        for (TaskStatusEnum statusEnum : TaskStatusEnum.values()) {
            if (statusEnum.status.equalsIgnoreCase(status)) {
                return statusEnum;
            }
        }
        return TaskStatusEnum.UNKNOWN;
    }

    public static TaskStatusEnum of(Integer code) {
        for (TaskStatusEnum statusEnum : TaskStatusEnum.values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum;
            }
        }
        return TaskStatusEnum.UNKNOWN;
    }
}
