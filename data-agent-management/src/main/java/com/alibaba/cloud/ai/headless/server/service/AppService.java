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
package com.alibaba.cloud.ai.headless.server.service;

import com.github.pagehelper.PageInfo;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.request.AppQueryReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.AppReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.AppDetailResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.AppResp;

public interface AppService {

    AppDetailResp save(AppReq app, User user);

    AppDetailResp update(AppReq app, User user);

    void online(Integer id, User user);

    void offline(Integer id, User user);

    void delete(Integer id, User user);

    PageInfo<AppResp> pageApp(AppQueryReq appQueryReq, User user);

    AppDetailResp getApp(Integer id, User user);

    AppDetailResp getApp(Integer id);
}
