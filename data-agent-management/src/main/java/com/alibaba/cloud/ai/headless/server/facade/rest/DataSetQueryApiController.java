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
package com.alibaba.cloud.ai.headless.server.facade.rest;

import com.alibaba.cloud.ai.headless.auth.api.authentication.utils.UserHolder;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryDataSetReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.SemanticQueryReq;
import com.alibaba.cloud.ai.headless.server.facade.service.SemanticLayerService;
import com.alibaba.cloud.ai.headless.server.service.DataSetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/semantic/query")
@Slf4j
public class DataSetQueryApiController {

    @Autowired
    private DataSetService dataSetService;
    @Autowired
    private SemanticLayerService semanticLayerService;

    @PostMapping("/dataSet")
    public Object queryByDataSet(@RequestBody QueryDataSetReq queryDataSetReq,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = UserHolder.findUser(request, response);
        SemanticQueryReq queryReq = dataSetService.convert(queryDataSetReq);
        return semanticLayerService.queryByReq(queryReq, user);
    }
}
