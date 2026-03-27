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
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryRuleFilter;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryRuleReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.QueryRuleResp;
import com.alibaba.cloud.ai.headless.server.service.QueryRuleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semantic/query/rule")
public class QueryRuleController {

    private final QueryRuleService queryRuleService;

    public QueryRuleController(QueryRuleService queryRuleService) {
        this.queryRuleService = queryRuleService;
    }

    /**
     * 新建查询规则
     *
     * @param queryRuleReq
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/create")
    public QueryRuleResp create(@RequestBody @Validated QueryRuleReq queryRuleReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return queryRuleService.addQueryRule(queryRuleReq, user);
    }

    /**
     * 编辑查询规则
     *
     * @param queryRuleReq
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/update")
    public QueryRuleResp update(@RequestBody @Validated QueryRuleReq queryRuleReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return queryRuleService.updateQueryRule(queryRuleReq, user);
    }

    /**
     * 删除查询规则
     *
     * @param id
     * @param request
     * @param response
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Boolean delete(@PathVariable("id") Long id, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return queryRuleService.dropQueryRule(id, user);
    }

    /**
     * 查询规则列表
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("query")
    public List<QueryRuleResp> query(@RequestBody @Validated QueryRuleFilter queryRuleFilter,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return queryRuleService.getQueryRuleList(queryRuleFilter, user);
    }
}
