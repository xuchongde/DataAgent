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
import com.alibaba.cloud.ai.headless.common.pojo.enums.AuthType;
import com.alibaba.cloud.ai.headless.api.pojo.request.DomainReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.DomainUpdateReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DomainResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.DomainDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DomainService {

    DomainResp getDomain(Long id);

    Map<Long, String> getDomainFullPath();

    DomainResp createDomain(DomainReq domainReq, User user);

    DomainResp updateDomain(DomainUpdateReq domainUpdateReq, User user);

    void deleteDomain(Long id);

    List<DomainResp> getDomainList();

    List<DomainResp> getDomainList(List<Long> domainIds);

    Map<Long, DomainResp> getDomainMap();

    List<DomainResp> getDomainListWithAdminAuth(User user);

    Set<DomainResp> getDomainAuthSet(User user, AuthType authTypeEnum);

    List<DomainDO> getDomainByBizName(String bizName);

    Set<DomainResp> getDomainChildren(List<Long> domainId);
}
