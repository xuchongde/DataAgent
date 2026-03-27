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
package com.alibaba.cloud.ai.headless.server.aspect;

import com.alibaba.cloud.ai.headless.common.pojo.Pair;
import com.alibaba.cloud.ai.headless.common.pojo.exception.InvalidArgumentException;
import com.alibaba.cloud.ai.headless.common.util.SignatureUtils;
import com.alibaba.cloud.ai.headless.api.pojo.enums.AppStatus;
import com.alibaba.cloud.ai.headless.api.pojo.response.AppDetailResp;
import com.alibaba.cloud.ai.headless.server.service.AppService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(1)
@Slf4j
public class ApiHeaderCheckAspect {

    public static final String APPID = "appId";

    private static final String TIMESTAMP = "timestamp";

    private static final String SIGNATURE = "signature";

    @Autowired
    private AppService appService;

    @Pointcut("@annotation(com.alibaba.cloud.ai.headless.server.annotation.ApiHeaderCheck)")
    private void apiPermissionCheck() {}

    @Around("apiPermissionCheck()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        HttpServletRequest request = (HttpServletRequest) objects[1];
        checkHeader(request);
        return joinPoint.proceed();
    }

    private void checkHeader(HttpServletRequest request) {
        String timestampStr = request.getHeader(TIMESTAMP);
        String signature = request.getHeader(SIGNATURE);
        String appId = request.getHeader(APPID);
        if (StringUtils.isBlank(timestampStr)) {
            throw new InvalidArgumentException("header中timestamp不可为空");
        }
        if (StringUtils.isBlank(signature)) {
            throw new InvalidArgumentException("header中signature不可为空");
        }
        if (StringUtils.isBlank(appId)) {
            throw new InvalidArgumentException("header中appId不可为空");
        }
        AppDetailResp appDetailResp = appService.getApp(Integer.parseInt(appId));
        if (appDetailResp == null) {
            throw new InvalidArgumentException("该appId对应的应用不存在");
        }
        if (!AppStatus.ONLINE.equals(appDetailResp.getAppStatus())) {
            throw new InvalidArgumentException("该应用暂时为非在线状态");
        }
        Pair<Boolean, String> checkResult = SignatureUtils.isValidSignature(appId,
                appDetailResp.getAppSecret(), Long.parseLong(timestampStr), signature);
        if (!checkResult.first) {
            throw new InvalidArgumentException(checkResult.second);
        }
    }
}
