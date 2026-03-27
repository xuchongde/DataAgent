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
package com.alibaba.cloud.ai.headless.server.persistence.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.DimensionDO;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.DimensionDOCustomMapper;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.DimensionDOMapper;
import com.alibaba.cloud.ai.headless.server.persistence.repository.DimensionRepository;
import com.alibaba.cloud.ai.headless.server.pojo.DimensionFilter;
import com.alibaba.cloud.ai.headless.server.pojo.DimensionsFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DimensionRepositoryImpl implements DimensionRepository {

    private DimensionDOMapper dimensionDOMapper;

    private DimensionDOCustomMapper dimensionDOCustomMapper;

    public DimensionRepositoryImpl(DimensionDOMapper dimensionDOMapper,
            DimensionDOCustomMapper dimensionDOCustomMapper) {
        this.dimensionDOMapper = dimensionDOMapper;
        this.dimensionDOCustomMapper = dimensionDOCustomMapper;
    }

    @Override
    public void createDimension(DimensionDO dimensionDO) {
        dimensionDOMapper.insert(dimensionDO);
    }

    @Override
    public void createDimensionBatch(List<DimensionDO> dimensionDOS) {
        dimensionDOCustomMapper.batchInsert(dimensionDOS);
    }

    @Override
    public void updateDimension(DimensionDO dimensionDO) {
        dimensionDOMapper.updateById(dimensionDO);
    }

    @Override
    public void batchUpdateStatus(List<DimensionDO> dimensionDOS) {
        dimensionDOCustomMapper.batchUpdateStatus(dimensionDOS);
    }

    @Override
    public void batchUpdate(List<DimensionDO> dimensionDOS) {
        dimensionDOCustomMapper.batchUpdate(dimensionDOS);
    }

    @Override
    public DimensionDO getDimensionById(Long id) {
        return dimensionDOMapper.selectById(id);
    }

    @Override
    public List<DimensionDO> getDimension(DimensionFilter dimensionFilter) {
        QueryWrapper<DimensionDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(DimensionDO::getStatus, 3);
        if (Objects.nonNull(dimensionFilter.getIds()) && !dimensionFilter.getIds().isEmpty()) {
            queryWrapper.lambda().in(DimensionDO::getId, dimensionFilter.getIds());
        }
        if (StringUtils.isNotBlank(dimensionFilter.getId())) {
            queryWrapper.lambda().eq(DimensionDO::getId, dimensionFilter.getId());
        }
        if (Objects.nonNull(dimensionFilter.getModelIds())
                && !dimensionFilter.getModelIds().isEmpty()) {
            queryWrapper.lambda().in(DimensionDO::getModelId, dimensionFilter.getModelIds());
        }
        if (StringUtils.isNotBlank(dimensionFilter.getName())) {
            queryWrapper.lambda().like(DimensionDO::getName, dimensionFilter.getName());
        }
        if (StringUtils.isNotBlank(dimensionFilter.getId())) {
            queryWrapper.lambda().like(DimensionDO::getBizName, dimensionFilter.getBizName());
        }
        if (Objects.nonNull(dimensionFilter.getStatus())) {
            queryWrapper.lambda().eq(DimensionDO::getStatus, dimensionFilter.getStatus());
        }
        if (Objects.nonNull(dimensionFilter.getSensitiveLevel())) {
            queryWrapper.lambda().eq(DimensionDO::getSensitiveLevel,
                    dimensionFilter.getSensitiveLevel());
        }
        if (StringUtils.isNotBlank(dimensionFilter.getCreatedBy())) {
            queryWrapper.lambda().eq(DimensionDO::getCreatedBy, dimensionFilter.getCreatedBy());
        }
        if (StringUtils.isNotBlank(dimensionFilter.getKey())) {
            String key = dimensionFilter.getKey();
            queryWrapper.and(qw -> qw.lambda().like(DimensionDO::getName, key).or()
                    .like(DimensionDO::getBizName, key).or().like(DimensionDO::getDescription, key)
                    .or().like(DimensionDO::getAlias, key).or()
                    .like(DimensionDO::getCreatedBy, key));
        }

        return dimensionDOMapper.selectList(queryWrapper);
    }

    @Override
    public List<DimensionDO> getDimensions(DimensionsFilter dimensionsFilter) {
        return dimensionDOCustomMapper.queryDimensions(dimensionsFilter);
    }
}
