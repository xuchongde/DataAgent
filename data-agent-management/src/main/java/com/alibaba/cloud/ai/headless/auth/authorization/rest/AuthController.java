package com.alibaba.cloud.ai.headless.auth.authorization.rest;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.auth.api.authentication.utils.UserHolder;
import com.alibaba.cloud.ai.headless.auth.api.authorization.pojo.AuthGroup;
import com.alibaba.cloud.ai.headless.auth.api.authorization.request.QueryAuthResReq;
import com.alibaba.cloud.ai.headless.auth.api.authorization.response.AuthorizedResourceResp;
import com.alibaba.cloud.ai.headless.auth.api.authorization.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/queryGroup")
    public List<AuthGroup> queryAuthGroup(@RequestParam("modelId") String modelId,
                                          @RequestParam(value = "groupId", required = false) Integer groupId) {
        return authService.queryAuthGroups(modelId, groupId);
    }

    /** 新建权限组 */
    @PostMapping("/createGroup")
    public void newAuthGroup(@RequestBody AuthGroup group) {
        group.setGroupId(null);
        authService.addOrUpdateAuthGroup(group);
    }

    @PostMapping("/removeGroup")
    public void removeAuthGroup(@RequestBody AuthGroup group) {
        authService.removeAuthGroup(group);
    }

    /**
     * 更新权限组
     *
     * @param group
     */
    @PostMapping("/updateGroup")
    public void updateAuthGroup(@RequestBody AuthGroup group) {
        if (group.getGroupId() == null || group.getGroupId() == 0) {
            throw new RuntimeException("groupId is empty");
        }
        authService.addOrUpdateAuthGroup(group);
    }

    /**
     * 查询有权限访问的受限资源id
     *
     * @param req
     * @return
     */
    @PostMapping("/queryAuthorizedRes")
    public AuthorizedResourceResp queryAuthorizedResources(@RequestBody QueryAuthResReq req,
                                                           HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return authService.queryAuthorizedResources(req, user);
    }
}
