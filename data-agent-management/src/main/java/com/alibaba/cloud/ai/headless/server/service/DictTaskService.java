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
import com.alibaba.cloud.ai.headless.api.pojo.request.DictSingleTaskReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.DictValueReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.ValueTaskQueryReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DictTaskResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.DictValueDimResp;

/** Manage dictionary tasks */
public interface DictTaskService {
    Long addDictTask(DictSingleTaskReq taskReq, User user);

    Long deleteDictTask(DictSingleTaskReq taskReq, User user);

    Boolean dailyDictTask();

    DictTaskResp queryLatestDictTask(DictSingleTaskReq taskReq, User user);

    PageInfo<DictTaskResp> queryDictTask(ValueTaskQueryReq taskQueryReq, User user);

    PageInfo<DictValueDimResp> queryDictValue(DictValueReq dictValueReq, User user);

    String queryDictFilePath(DictValueReq dictValueReq, User user);
}
