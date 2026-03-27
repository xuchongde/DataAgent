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
package com.alibaba.cloud.ai.headless.server.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.cloud.ai.headless.api.pojo.request.DatabaseReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DatabaseResp;
import com.alibaba.cloud.ai.headless.core.pojo.ConnectInfo;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.DatabaseDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;

public class DatabaseConverter {

    public static DatabaseResp convert(DatabaseReq databaseReq) {
        DatabaseResp database = new DatabaseResp();
        BeanUtils.copyProperties(databaseReq, database);
        return database;
    }

    public static DatabaseDO convert(DatabaseReq databaseReq, DatabaseDO databaseDO) {
        BeanUtils.copyProperties(databaseReq, databaseDO);
        ConnectInfo connectInfo = getConnectInfo(databaseReq);
        databaseDO.setConfig(JSONObject.toJSONString(connectInfo));
        databaseDO.setAdmin(String.join(",", databaseReq.getAdmins()));
        databaseDO.setViewer(String.join(",", databaseReq.getViewers()));
        return databaseDO;
    }

    public static DatabaseResp convert(DatabaseDO databaseDO) {
        DatabaseResp databaseResp = new DatabaseResp();
        BeanUtils.copyProperties(databaseDO, databaseResp);
        ConnectInfo connectInfo = JSONObject.parseObject(databaseDO.getConfig(), ConnectInfo.class);
        databaseResp.setUrl(connectInfo.getUrl());
        databaseResp.setUsername(connectInfo.getUserName());
        databaseResp.setDatabase(connectInfo.getDatabase());
        if (StringUtils.isNotBlank(databaseDO.getAdmin())) {
            databaseResp.setAdmins(Arrays.asList(databaseDO.getAdmin().split(",")));
        }
        if (StringUtils.isNotBlank(databaseDO.getViewer())) {
            databaseResp.setViewers(Arrays.asList(databaseDO.getViewer().split(",")));
        }
        return databaseResp;
    }

    public static DatabaseDO convertDO(DatabaseReq databaseReq) {
        DatabaseDO databaseDO = new DatabaseDO();
        BeanUtils.copyProperties(databaseReq, databaseDO);
        ConnectInfo connectInfo = getConnectInfo(databaseReq);
        databaseDO.setConfig(JSONObject.toJSONString(connectInfo));
        databaseDO.setAdmin(String.join(",", databaseReq.getAdmins()));
        databaseDO.setViewer(String.join(",", databaseReq.getViewers()));
        return databaseDO;
    }

    public static DatabaseResp convertWithPassword(DatabaseDO databaseDO) {
        DatabaseResp databaseResp = convert(databaseDO);
        ConnectInfo connectInfo = JSONObject.parseObject(databaseDO.getConfig(), ConnectInfo.class);
        databaseResp.setPassword(connectInfo.getPassword());
        return databaseResp;
    }

    public static ConnectInfo getConnectInfo(DatabaseResp database) {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setUserName(database.getUsername());
        connectInfo.setPassword(database.passwordDecrypt());
        connectInfo.setUrl(database.getUrl());
        connectInfo.setDatabase(database.getDatabase());
        return connectInfo;
    }

    public static ConnectInfo getConnectInfo(DatabaseReq databaseReq) {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setUserName(databaseReq.getUsername());
        connectInfo.setPassword(databaseReq.getPassword());
        connectInfo.setUrl(databaseReq.getUrl());
        connectInfo.setDatabase(databaseReq.getDatabase());
        return connectInfo;
    }
}
