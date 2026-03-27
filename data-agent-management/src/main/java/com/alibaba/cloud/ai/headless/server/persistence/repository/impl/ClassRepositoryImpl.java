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
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.ClassDO;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.ClassMapper;
import com.alibaba.cloud.ai.headless.server.persistence.repository.ClassRepository;
import com.alibaba.cloud.ai.headless.server.pojo.ClassFilter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class ClassRepositoryImpl implements ClassRepository {

    private final ClassMapper mapper;

    public ClassRepositoryImpl(ClassMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Long create(ClassDO classDO) {
        mapper.insert(classDO);
        return classDO.getId();
    }

    @Override
    public Long update(ClassDO classDO) {
        mapper.updateById(classDO);
        return classDO.getId();
    }

    @Override
    public Integer delete(List<Long> ids) {
        return mapper.deleteBatchIds(ids);
    }

    @Override
    public ClassDO getClassById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<ClassDO> getClassDOList(ClassFilter filter) {
        QueryWrapper<ClassDO> wrapper = new QueryWrapper();
        if (Objects.nonNull(filter.getDomainId())) {
            wrapper.lambda().eq(ClassDO::getDomainId, filter.getDomainId());
        }
        if (Objects.nonNull(filter.getDataSetId())) {
            wrapper.lambda().eq(ClassDO::getDataSetId, filter.getDataSetId());
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            wrapper.lambda().eq(ClassDO::getType, filter.getType());
        }
        if (CollectionUtils.isNotEmpty(filter.getIds())) {
            wrapper.lambda().in(ClassDO::getId, filter.getIds());
        }
        if (Objects.nonNull(filter.getCreatedBy())) {
            wrapper.lambda().eq(ClassDO::getCreatedBy, filter.getCreatedBy());
        }
        if (Objects.nonNull(filter.getStatus())) {
            wrapper.lambda().eq(ClassDO::getStatus, filter.getStatus());
        }
        if (Objects.nonNull(filter.getBizName())) {
            wrapper.lambda().eq(ClassDO::getBizName, filter.getBizName());
        }
        return mapper.selectList(wrapper);
    }

    @Override
    public List<ClassDO> getAllClassDOList() {
        QueryWrapper<ClassDO> wrapper = new QueryWrapper();
        return mapper.selectList(wrapper);
    }
}
