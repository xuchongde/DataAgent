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
import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.auth.api.authentication.utils.UserHolder;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.pojo.enums.SensitiveLevelEnum;
import com.alibaba.cloud.ai.headless.api.pojo.DrillDownDimension;
import com.alibaba.cloud.ai.headless.api.pojo.MetaFilter;
import com.alibaba.cloud.ai.headless.api.pojo.MetricQueryDefaultConfig;
import com.alibaba.cloud.ai.headless.api.pojo.request.MetaBatchReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.MetricBaseReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.MetricReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.PageMetricReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.MetricResp;
import com.alibaba.cloud.ai.headless.server.pojo.MetricFilter;
import com.alibaba.cloud.ai.headless.server.service.MetricService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/semantic/metric")
public class MetricController {

    private MetricService metricService;

    public MetricController(MetricService metricService) {
        this.metricService = metricService;
    }

    @PostMapping("/createMetric")
    public MetricResp createMetric(@RequestBody MetricReq metricReq, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        User user = UserHolder.findUser(request, response);
        return metricService.createMetric(metricReq, user);
    }

    @PostMapping("/updateMetric")
    public MetricResp updateMetric(@RequestBody MetricReq metricReq, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        User user = UserHolder.findUser(request, response);
        return metricService.updateMetric(metricReq, user);
    }

    @PostMapping("/batchUpdateStatus")
    public Boolean batchUpdateStatus(@RequestBody MetaBatchReq metaBatchReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        metricService.batchUpdateStatus(metaBatchReq, user);
        return true;
    }

    @PostMapping("/batchPublish")
    public Boolean batchPublish(@RequestBody MetaBatchReq metaBatchReq, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        metricService.batchPublish(metaBatchReq.getIds(), user);
        return true;
    }

    @PostMapping("/batchUnPublish")
    public Boolean batchUnPublish(@RequestBody MetaBatchReq metaBatchReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        metricService.batchUnPublish(metaBatchReq.getIds(), user);
        return true;
    }

    @PostMapping("/batchUpdateClassifications")
    public Boolean batchUpdateClassifications(@RequestBody MetaBatchReq metaBatchReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        metricService.batchUpdateClassifications(metaBatchReq, user);
        return true;
    }

    @PostMapping("/batchUpdateSensitiveLevel")
    public Boolean batchUpdateSensitiveLevel(@RequestBody MetaBatchReq metaBatchReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        metricService.batchUpdateSensitiveLevel(metaBatchReq, user);
        return true;
    }

    @PostMapping("/mockMetricAlias")
    public List<String> mockMetricAlias(@RequestBody MetricBaseReq metricReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return metricService.mockAlias(metricReq, "indicator", user);
    }

    @GetMapping("/getMetricList/{modelId}")
    public List<MetricResp> getMetricList(@PathVariable("modelId") Long modelId) {
        MetaFilter metaFilter = new MetaFilter(Lists.newArrayList(modelId));
        return metricService.getMetrics(metaFilter);
    }

    @GetMapping("/getMetricsToCreateNewMetric/{modelId}")
    public List<MetricResp> getMetricsToCreateNewMetric(@PathVariable("modelId") Long modelId) {
        return metricService.getMetricsToCreateNewMetric(modelId);
    }

    @PostMapping("/queryMetric")
    public PageInfo<MetricResp> queryMetric(@RequestBody PageMetricReq pageMetricReq,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return metricService.queryMetricMarket(pageMetricReq, user);
    }

    @Deprecated
    @GetMapping("getMetric/{modelId}/{bizName}")
    public MetricResp getMetric(@PathVariable("modelId") Long modelId,
            @PathVariable("bizName") String bizName) {
        return metricService.getMetric(modelId, bizName);
    }

    @GetMapping("getMetric/{id}")
    public MetricResp getMetric(@PathVariable("id") Long id, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return metricService.getMetric(id, user);
    }

    @DeleteMapping("deleteMetric/{id}")
    public Boolean deleteMetric(@PathVariable("id") Long id, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        User user = UserHolder.findUser(request, response);
        metricService.deleteMetric(id, user);
        return true;
    }

    @GetMapping("/getAllHighSensitiveMetric")
    public List<MetricResp> getAllHighSensitiveMetric() {
        MetricFilter metricFilter = new MetricFilter();
        metricFilter.setSensitiveLevel(SensitiveLevelEnum.HIGH.getCode());
        return metricService.getMetrics(metricFilter);
    }

    @Deprecated
    @GetMapping("/getMetricTags")
    public Set<String> getMetricTags() {
        return metricService.getMetricTags();
    }

    @GetMapping("/getMetricClassifications")
    public Set<String> getMetricClassifications() {
        return metricService.getMetricTags();
    }

    @GetMapping("/getDrillDownDimension")
    public List<DrillDownDimension> getDrillDownDimension(Long metricId) {
        return metricService.getDrillDownDimension(metricId);
    }

    @PostMapping("/saveMetricQueryDefaultConfig")
    public boolean saveMetricQueryDefaultConfig(
            @RequestBody MetricQueryDefaultConfig queryDefaultConfig, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        metricService.saveMetricQueryDefaultConfig(queryDefaultConfig, user);
        return true;
    }

    @RequestMapping("getMetricQueryDefaultConfig/{metricId}")
    public MetricQueryDefaultConfig getMetricQueryDefaultConfig(
            @PathVariable("metricId") Long metricId, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return metricService.getMetricQueryDefaultConfig(metricId, user);
    }
}
