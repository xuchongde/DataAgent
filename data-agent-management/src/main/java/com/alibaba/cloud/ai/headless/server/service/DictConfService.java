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

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.request.DictItemFilter;
import com.alibaba.cloud.ai.headless.api.pojo.request.DictItemReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DictItemResp;

import java.util.List;

/** Make relevant settings for the dictionary */
public interface DictConfService {

    DictItemResp addDictConf(DictItemReq itemValueReq, User user);

    DictItemResp editDictConf(DictItemReq itemValueReq, User user);

    List<DictItemResp> queryDictConf(DictItemFilter dictItemFilter, User user);
}
