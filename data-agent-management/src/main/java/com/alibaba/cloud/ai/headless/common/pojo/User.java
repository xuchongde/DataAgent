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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Long id;

    private String name;

    private String displayName;

    private String email;

    private Integer isAdmin;

    private Timestamp lastLogin;

    public static User get(Long id, String name, String displayName, String email,
            Integer isAdmin) {
        return new User(id, name, displayName, email, isAdmin, null);
    }

    public static User get(Long id, String name) {
        return new User(id, name, name, name, 0, null);
    }

    public static User getDefaultUser() {
        return new User(1L, "admin", "admin", "admin@email", 1, null);
    }

    public static User getVisitUser() {
        return new User(1L, "visit", "visit", "visit@email", 0, null);
    }

    public static User getAppUser(int appId) {
        String name = String.format("app_%s", appId);
        return new User(1L, name, name, "", 1, null);
    }

    public String getDisplayName() {
        return StringUtils.isBlank(displayName) ? name : displayName;
    }

    public boolean isSuperAdmin() {
        return isAdmin != null && isAdmin == 1;
    }
}
