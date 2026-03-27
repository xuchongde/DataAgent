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
import com.alibaba.cloud.ai.headless.api.pojo.AppConfig;
import com.alibaba.cloud.ai.headless.api.pojo.Item;
import com.alibaba.cloud.ai.headless.api.pojo.enums.AppStatus;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class AppResp extends RecordInfo {

    private Integer id;

    private String name;

    private String description;

    private AppStatus appStatus;

    private AppConfig config;

    private Date endDate;

    private Integer qps;

    private List<String> owners;

    private boolean hasAdminRes;

    public void setOwner(String owner) {
        if (StringUtils.isBlank(owner)) {
            owners = Lists.newArrayList();
            return;
        }
        owners = Arrays.asList(owner.split(","));
    }

    public Set<Item> allItems() {
        Set<Item> itemSet = new HashSet<>();
        for (Item item : config.getItems()) {
            itemSet.add(item);
            itemSet.addAll(item.getRelateItems());
        }
        return itemSet;
    }
}
