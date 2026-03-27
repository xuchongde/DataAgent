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

public enum DimensionType {
    categorical, time, partition_time, primary_key, foreign_key;

    public static DimensionType fromIdentify(String identify) {
        if (IdentifyType.foreign.name().equalsIgnoreCase(identify)) {
            return DimensionType.foreign_key;
        } else if (IdentifyType.primary.name().equalsIgnoreCase(identify)) {
            return DimensionType.primary_key;
        }
        return DimensionType.categorical;
    }

    public static boolean isTimeDimension(String type) {
        try {
            return isTimeDimension(DimensionType.valueOf(type.toLowerCase()));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isTimeDimension(DimensionType type) {
        return type == time || type == partition_time;
    }

    public static boolean isPartitionTime(DimensionType type) {
        return type == partition_time;
    }

    public static boolean isPrimaryKey(DimensionType type) {
        return type == primary_key;
    }
}
