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
package com.alibaba.cloud.ai.headless.server.rest;

import com.alibaba.cloud.ai.headless.auth.api.authentication.utils.UserHolder;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.request.ClassReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.ClassResp;
import com.alibaba.cloud.ai.headless.server.pojo.ClassFilter;
import com.alibaba.cloud.ai.headless.server.service.ClassService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semantic/class")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    /**
     * 新建目录
     *
     * @param classReq
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/create")
    public ClassResp create(@RequestBody @Valid ClassReq classReq, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return classService.create(classReq, user);
    }

    /**
     * 修改目录
     *
     * @param classReq
     * @param request
     * @param response
     * @return
     */
    @PutMapping("/update")
    public ClassResp update(@RequestBody @Valid ClassReq classReq, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return classService.update(classReq, user);
    }

    /**
     * 删除目录
     *
     * @param id
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @DeleteMapping("delete/{id}/{force}")
    public Boolean delete(@PathVariable("id") Long id, @PathVariable("force") Boolean force,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = UserHolder.findUser(request, response);
        return classService.delete(id, force, user);
    }

    /**
     * 删除目录
     *
     * @param filter
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("delete/{id}/{force}")
    public List<ClassResp> get(@RequestBody @Valid ClassFilter filter, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return classService.getClassList(filter, user);
    }
}
