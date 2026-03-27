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

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.pojo.RecordInfo;
import com.alibaba.cloud.ai.headless.common.util.AESEncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatabaseResp extends RecordInfo {

    private Long id;

    private String name;

    private String description;

    private List<String> admins = Lists.newArrayList();

    private List<String> viewers = Lists.newArrayList();

    private Integer isOpen = 0;

    private String type;

    private String url;

    private String username;

    private String password;

    private String database;

    private String version;

    private String schema;

    private boolean hasPermission = false;

    private boolean hasUsePermission = false;

    private boolean hasEditPermission = false;

    public boolean isPublic() {
        return isOpen != null && isOpen == 1;
    }

    public String getHost() {
        Pattern p = Pattern.compile("jdbc:(?<db>\\w+):.*((//)|@)(?<host>.+):(?<port>\\d+).*");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group("host");
        }
        return "";
    }

    public String getPort() {
        Pattern p = Pattern.compile("jdbc:(?<db>\\w+):.*((//)|@)(?<host>.+):(?<port>\\d+).*");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group("port");
        }
        return "";
    }

    public String passwordDecrypt() {
        return AESEncryptionUtil.aesDecryptECB(password);
    }
}
