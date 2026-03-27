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
package com.alibaba.cloud.ai.headless.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.pojo.enums.AuthType;
import com.alibaba.cloud.ai.headless.api.pojo.MetaFilter;
import com.alibaba.cloud.ai.headless.api.pojo.request.CanvasReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.CanvasSchemaResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.DimensionResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.MetricResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.CanvasDO;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.CanvasDOMapper;
import com.alibaba.cloud.ai.headless.server.service.CanvasService;
import com.alibaba.cloud.ai.headless.server.service.DimensionService;
import com.alibaba.cloud.ai.headless.server.service.MetricService;
import com.alibaba.cloud.ai.headless.server.service.ModelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanvasServiceImpl extends ServiceImpl<CanvasDOMapper, CanvasDO>
        implements CanvasService {

    @Autowired
    private ModelService modelService;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private MetricService metricService;

    @Override
    public List<CanvasDO> getCanvasList(Long domainId) {
        QueryWrapper<CanvasDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CanvasDO::getDomainId, domainId);
        return list(queryWrapper);
    }

    @Override
    public List<CanvasSchemaResp> getCanvasSchema(Long domainId, User user) {
        List<CanvasSchemaResp> canvasSchemaResps = Lists.newArrayList();
        List<ModelResp> modelResps =
                modelService.getModelListWithAuth(user, domainId, AuthType.ADMIN);
        for (ModelResp modelResp : modelResps) {
            CanvasSchemaResp canvasSchemaResp = new CanvasSchemaResp();
            MetaFilter metaFilter = new MetaFilter();
            metaFilter.setModelIds(Lists.newArrayList(modelResp.getId()));
            List<MetricResp> metricResps = metricService.getMetrics(metaFilter);
            List<DimensionResp> dimensionResps = dimensionService.getDimensions(metaFilter);
            canvasSchemaResp.setModel(modelResp);
            canvasSchemaResp.setDimensions(dimensionResps);
            canvasSchemaResp.setMetrics(metricResps);
            canvasSchemaResp.setDomainId(domainId);
            canvasSchemaResps.add(canvasSchemaResp);
        }
        return canvasSchemaResps;
    }

    @Override
    public CanvasDO createOrUpdateCanvas(CanvasReq canvasReq, User user) {
        if (canvasReq.getId() == null) {
            canvasReq.createdBy(user.getName());
            CanvasDO viewInfoDO = new CanvasDO();
            BeanUtils.copyProperties(canvasReq, viewInfoDO);
            save(viewInfoDO);
            return viewInfoDO;
        }
        Long id = canvasReq.getId();
        CanvasDO viewInfoDO = getById(id);
        canvasReq.updatedBy(user.getName());
        BeanUtils.copyProperties(canvasReq, viewInfoDO);
        updateById(viewInfoDO);
        return viewInfoDO;
    }

    @Override
    public void deleteCanvas(Long id) {
        removeById(id);
    }
}
