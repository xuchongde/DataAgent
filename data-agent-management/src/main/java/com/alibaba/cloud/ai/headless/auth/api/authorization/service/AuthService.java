package com.alibaba.cloud.ai.headless.auth.api.authorization.service;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.auth.api.authorization.pojo.AuthGroup;
import com.alibaba.cloud.ai.headless.auth.api.authorization.request.QueryAuthResReq;
import com.alibaba.cloud.ai.headless.auth.api.authorization.response.AuthorizedResourceResp;

import java.util.List;

public interface AuthService {

    List<AuthGroup> queryAuthGroups(String domainId, Integer groupId);

    void addOrUpdateAuthGroup(AuthGroup group);

    void removeAuthGroup(AuthGroup group);

    AuthorizedResourceResp queryAuthorizedResources(QueryAuthResReq req, User user);
}
