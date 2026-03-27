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
package com.alibaba.cloud.ai.headless.api.pojo;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaFilter {

    private String id;

    private String name;

    private String bizName;

    private String createdBy;

    private List<Long> modelIds;

    private Long domainId;

    private Long dataSetId;

    private Integer sensitiveLevel;

    private Integer status;

    private String key;

    private List<Long> ids;

    private List<String> names;

    private List<String> bizNames;

    private List<String> fieldsDepend;

    private Integer isTag;

    public MetaFilter(List<Long> modelIds) {
        this.modelIds = modelIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetaFilter that = (MetaFilter) o;
        return Objects.equal(id, that.id) && Objects.equal(name, that.name)
                && Objects.equal(bizName, that.bizName) && Objects.equal(createdBy, that.createdBy)
                && Objects.equal(modelIds, that.modelIds) && Objects.equal(domainId, that.domainId)
                && Objects.equal(dataSetId, that.dataSetId)
                && Objects.equal(sensitiveLevel, that.sensitiveLevel)
                && Objects.equal(status, that.status) && Objects.equal(key, that.key)
                && Objects.equal(ids, that.ids) && Objects.equal(fieldsDepend, that.fieldsDepend);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, bizName, createdBy, modelIds, domainId, dataSetId,
                sensitiveLevel, status, key, ids, fieldsDepend);
    }
}
