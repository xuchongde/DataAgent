package com.alibaba.cloud.ai.headless.auth.api.authentication.utils;

import com.alibaba.cloud.ai.headless.common.config.SystemConfig;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.service.SystemConfigService;
import com.alibaba.cloud.ai.headless.common.util.ContextUtils;
import com.alibaba.cloud.ai.headless.auth.api.authentication.service.UserStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.CollectionUtils;

public final class UserHolder {

    private static UserStrategy REPO;

    public static synchronized void setStrategy(UserStrategy strategy) {
        REPO = strategy;
    }

    public static User findUser(HttpServletRequest request, HttpServletResponse response) {
        User user = REPO.findUser(request, response);
        return getUser(user);
    }

    public static User findUser(String token, String appKey) {
        User user = REPO.findUser(token, appKey);
        return getUser(user);
    }

    private static User getUser(User user) {
        SystemConfigService sysParameterService = ContextUtils.getBean(SystemConfigService.class);
        SystemConfig systemConfig = sysParameterService.getSystemConfig();
        if (!CollectionUtils.isEmpty(systemConfig.getAdmins())
                && systemConfig.getAdmins().contains(user.getName())) {
            user.setIsAdmin(1);
        }
        return user;
    }
}
