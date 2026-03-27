package com.alibaba.cloud.ai.headless.auth.api.authentication.service;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserStrategy {

    String getStrategyName();

    boolean accept(boolean isEnableAuthentication);

    User findUser(HttpServletRequest request, HttpServletResponse response);

    User findUser(String token, String appKey);
}
