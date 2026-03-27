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
package com.alibaba.cloud.ai.headless.server.pojo;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MysqlParametersBuilder implements DbParametersBuilder {

    @Override
    public List<DatabaseParameter> build() {
        List<DatabaseParameter> databaseParameters = new ArrayList<>();
        DatabaseParameter host = new DatabaseParameter();
        host.setComment("JDBC连接");
        host.setName("url");
        host.setPlaceholder("请输入JDBC连接串");
        host.setValue("jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8");
        databaseParameters.add(host);

        DatabaseParameter version = new DatabaseParameter();
        version.setComment("数据库版本");
        version.setName("version");
        version.setPlaceholder("请输入数据库版本");
        version.setDataType("list");
        version.setValue("5.7");
        version.setCandidateValues(Lists.newArrayList("5.7", "8.0"));
        databaseParameters.add(version);

        DatabaseParameter userName = new DatabaseParameter();
        userName.setComment("用户名");
        userName.setName("username");
        userName.setPlaceholder("请输入用户名");
        databaseParameters.add(userName);

        DatabaseParameter password = new DatabaseParameter();
        password.setComment("密码");
        password.setName("password");
        password.setPlaceholder("请输入密码");
        databaseParameters.add(password);
        return databaseParameters;
    }
}
