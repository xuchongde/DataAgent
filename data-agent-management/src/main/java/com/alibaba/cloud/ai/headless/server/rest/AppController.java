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

import com.github.pagehelper.PageInfo;
import com.alibaba.cloud.ai.headless.auth.api.authentication.utils.UserHolder;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.request.AppQueryReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.AppReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.AppDetailResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.AppResp;
import com.alibaba.cloud.ai.headless.server.service.AppService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/semantic/app")
public class AppController {

    @Autowired
    private AppService appService;

    @PostMapping
    public boolean save(@RequestBody AppReq app, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        appService.save(app, user);
        return true;
    }

    @PutMapping
    public boolean update(@RequestBody AppReq app, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        appService.update(app, user);
        return true;
    }

    @PutMapping("/online/{id}")
    public boolean online(@PathVariable("id") Integer id, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        appService.online(id, user);
        return true;
    }

    @PutMapping("/offline/{id}")
    public boolean offline(@PathVariable("id") Integer id, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        appService.offline(id, user);
        return true;
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Integer id, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        appService.delete(id, user);
        return true;
    }

    @GetMapping("/{id}")
    public AppDetailResp getApp(@PathVariable("id") Integer id, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return appService.getApp(id, user);
    }

    @PostMapping("/page")
    public PageInfo<AppResp> pageApp(@RequestBody AppQueryReq appQueryReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return appService.pageApp(appQueryReq, user);
    }
}
