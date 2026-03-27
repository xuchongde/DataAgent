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
import com.alibaba.cloud.ai.headless.api.pojo.request.TagObjectReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.TagObjectResp;
import com.alibaba.cloud.ai.headless.server.pojo.TagObjectFilter;
import com.alibaba.cloud.ai.headless.server.service.TagObjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semantic/tagObject")
public class TagObjectController {

    private final TagObjectService tagObjectService;

    public TagObjectController(TagObjectService tagObjectService) {
        this.tagObjectService = tagObjectService;
    }

    /**
     * 新建标签对象
     *
     * @param tagObjectReq
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/create")
    public TagObjectResp create(@RequestBody TagObjectReq tagObjectReq, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        User user = UserHolder.findUser(request, response);
        return tagObjectService.create(tagObjectReq, user);
    }

    /**
     * 编辑标签对象
     *
     * @param tagObjectReq
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/update")
    public TagObjectResp update(@RequestBody TagObjectReq tagObjectReq, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return tagObjectService.update(tagObjectReq, user);
    }

    /**
     * 删除标签对象
     *
     * @param id
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @DeleteMapping("delete/{id}")
    public Boolean delete(@PathVariable("id") Long id, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        User user = UserHolder.findUser(request, response);
        tagObjectService.delete(id, user, true);
        return true;
    }

    /**
     * 标签对象-查询
     *
     * @param filter
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/query")
    public List<TagObjectResp> queryTagObject(@RequestBody TagObjectFilter filter,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = UserHolder.findUser(request, response);
        return tagObjectService.getTagObjects(filter, user);
    }
}
