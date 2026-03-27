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
package com.alibaba.cloud.ai.headless.api.pojo.request;

import com.alibaba.cloud.ai.headless.api.pojo.DrillDownDimension;
import com.alibaba.cloud.ai.headless.api.pojo.ModelDetail;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaItem;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ModelReq extends SchemaItem {

    private Long databaseId;

    private Long domainId;

    private String filterSql;

    private Integer isOpen;

    private List<DrillDownDimension> drillDownDimensions;

    private String alias;

    private String sourceType;

    private ModelDetail modelDetail;

    private List<String> viewers;

    private List<String> viewOrgs;

    private List<String> admins;

    private List<String> adminOrgs;

    private Map<String, Object> ext;

    public String getViewer() {
        if (viewers == null) {
            return null;
        }
        return String.join(",", viewers);
    }

    public String getViewOrg() {
        if (viewOrgs == null) {
            return null;
        }
        return String.join(",", viewOrgs);
    }

    public String getAdmin() {
        if (admins == null) {
            return null;
        }
        return String.join(",", admins);
    }

    public String getAdminOrg() {
        if (adminOrgs == null) {
            return null;
        }
        return String.join(",", adminOrgs);
    }
}
