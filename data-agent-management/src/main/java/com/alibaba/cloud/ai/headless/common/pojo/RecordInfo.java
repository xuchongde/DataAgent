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
package com.alibaba.cloud.ai.headless.common.pojo;

import com.google.common.base.Objects;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class RecordInfo {

    private String createdBy;

    private String updatedBy;

    private Date createdAt;

    private Date updatedAt;

    public RecordInfo createdBy(String userName) {
        this.createdBy = userName;
        this.createdAt = new Date();
        this.updatedBy = userName;
        this.updatedAt = new Date();
        return this;
    }

    public RecordInfo updatedBy(String userName) {
        this.updatedBy = userName;
        this.updatedAt = new Date();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecordInfo that = (RecordInfo) o;
        return Objects.equal(createdBy, that.createdBy) && Objects.equal(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(createdBy, updatedBy, createdAt, updatedAt);
    }
}
