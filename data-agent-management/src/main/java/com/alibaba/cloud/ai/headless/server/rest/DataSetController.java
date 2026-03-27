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
import com.alibaba.cloud.ai.headless.common.pojo.enums.StatusEnum;
import com.alibaba.cloud.ai.headless.api.pojo.request.DataSetReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DataSetResp;
import com.alibaba.cloud.ai.headless.server.service.DataSetService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/semantic/dataSet")
public class DataSetController {

    @Autowired
    private DataSetService dataSetService;

    @PostMapping
    public DataSetResp save(@RequestBody DataSetReq dataSetReq, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return dataSetService.save(dataSetReq, user);
    }

    @PutMapping
    public DataSetResp update(@RequestBody DataSetReq dataSetReq, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return dataSetService.update(dataSetReq, user);
    }

    @GetMapping("/{id}")
    public DataSetResp getDataSet(@PathVariable("id") Long id) {
        return dataSetService.getDataSet(id);
    }

    @GetMapping("/getDataSetList")
    public List<DataSetResp> getDataSetList(@RequestParam("domainId") Long domainId) {
        List<Integer> statuCodeList =
                Arrays.asList(StatusEnum.ONLINE.getCode(), StatusEnum.OFFLINE.getCode());
        return dataSetService.getDataSetList(domainId, statuCodeList);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable("id") Long id, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        dataSetService.delete(id, user);
        return true;
    }
}
