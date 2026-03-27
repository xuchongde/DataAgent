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
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.TagObjectDO;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.TagObjectMapper;
import com.alibaba.cloud.ai.headless.server.persistence.repository.TagObjectRepository;
import com.alibaba.cloud.ai.headless.server.pojo.TagObjectFilter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class TagObjectRepositoryImpl implements TagObjectRepository {

    private final TagObjectMapper tagObjectMapper;

    public TagObjectRepositoryImpl(TagObjectMapper tagObjectMapper) {
        this.tagObjectMapper = tagObjectMapper;
    }

    @Override
    public Integer create(TagObjectDO tagObjectDO) {
        return tagObjectMapper.insert(tagObjectDO);
    }

    @Override
    public Integer update(TagObjectDO tagObjectDO) {
        return tagObjectMapper.updateById(tagObjectDO);
    }

    @Override
    public TagObjectDO getTagObjectById(Long id) {
        return tagObjectMapper.selectById(id);
    }

    @Override
    public List<TagObjectDO> query(TagObjectFilter filter) {
        QueryWrapper<TagObjectDO> wrapper = new QueryWrapper<>();
        if (Objects.nonNull(filter.getDomainIds())) {
            wrapper.lambda().in(TagObjectDO::getDomainId, filter.getDomainIds());
        }
        if (Objects.nonNull(filter.getDomainId())) {
            wrapper.lambda().eq(TagObjectDO::getDomainId, filter.getDomainId());
        }
        if (Objects.nonNull(filter.getIds())) {
            wrapper.lambda().in(TagObjectDO::getId, filter.getIds());
        }
        if (Objects.nonNull(filter.getId())) {
            wrapper.lambda().eq(TagObjectDO::getId, filter.getId());
        }
        if (Objects.nonNull(filter.getBizName())) {
            wrapper.lambda().eq(TagObjectDO::getBizName, filter.getBizName());
        }
        if (Objects.nonNull(filter.getCreatedBy())) {
            wrapper.lambda().eq(TagObjectDO::getCreatedBy, filter.getCreatedBy());
        }
        if (Objects.nonNull(filter.getStatus())) {
            wrapper.lambda().eq(TagObjectDO::getStatus, filter.getStatus());
        }
        return tagObjectMapper.selectList(wrapper);
    }
}
