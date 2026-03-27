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

import com.alibaba.cloud.ai.headless.api.pojo.SchemaItem;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@Data
@ToString
public class DomainResp extends SchemaItem {

    private Long parentId;

    private String fullPath;

    private List<String> viewers;

    private List<String> viewOrgs;

    private List<String> admins;

    private List<String> adminOrgs;

    private Integer isOpen = 0;

    private boolean hasEditPermission = false;

    private boolean hasModel;

    public boolean openToAll() {
        return isOpen != null && isOpen == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomainResp that = (DomainResp) o;
        if (getId() == null || that.getId() == null) {
            return false;
        }
        return Objects.equals(getId().intValue(), that.getId().intValue());
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            return 0;
        }
        return getId().hashCode();
    }
}
