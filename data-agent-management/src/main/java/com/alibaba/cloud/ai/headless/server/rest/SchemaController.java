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
import com.alibaba.cloud.ai.headless.common.pojo.enums.AuthType;
import com.alibaba.cloud.ai.headless.api.pojo.response.DomainResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import com.alibaba.cloud.ai.headless.server.service.HeadlessSchemaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semantic/schema")
public class SchemaController {

    @Autowired
    private HeadlessSchemaService schemaService;

    @GetMapping("/domain/list")
    public List<DomainResp> getDomainList(HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return schemaService.getDomainList(user);
    }

    @GetMapping("/model/list")
    public List<ModelResp> getModelList(@RequestParam("domainId") Long domainId,
            @RequestParam("authType") String authType, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return schemaService.getModelList(user, AuthType.valueOf(authType), domainId);
    }
}
