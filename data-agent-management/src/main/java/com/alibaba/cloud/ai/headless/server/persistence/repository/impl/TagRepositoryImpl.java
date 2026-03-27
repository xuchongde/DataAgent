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

import com.alibaba.cloud.ai.headless.api.pojo.request.TagDeleteReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.TagResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.TagDO;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.TagCustomMapper;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.TagMapper;
import com.alibaba.cloud.ai.headless.server.persistence.repository.TagRepository;
import com.alibaba.cloud.ai.headless.server.pojo.TagFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class TagRepositoryImpl implements TagRepository {
    private final TagMapper mapper;
    private final TagCustomMapper tagCustomMapper;

    public TagRepositoryImpl(TagMapper mapper, TagCustomMapper tagCustomMapper) {
        this.mapper = mapper;
        this.tagCustomMapper = tagCustomMapper;
    }

    @Override
    public Long create(TagDO tagDO) {
        mapper.insert(tagDO);
        return tagDO.getId();
    }

    @Override
    public void update(TagDO tagDO) {
        mapper.updateById(tagDO);
    }

    @Override
    public TagDO getTagById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<TagDO> getTagDOList(TagFilter tagFilter) {
        return tagCustomMapper.getTagDOList(tagFilter);
    }

    @Override
    public List<TagResp> queryTagRespList(TagFilter tagFilter) {
        return tagCustomMapper.queryTagRespList(tagFilter);
    }

    @Override
    public Boolean delete(Long id) {
        return tagCustomMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(TagDeleteReq tagDeleteReq) {
        if (CollectionUtils.isNotEmpty(tagDeleteReq.getIds())) {
            tagCustomMapper.deleteBatchByIds(tagDeleteReq.getIds());
        }
        if (Objects.nonNull(tagDeleteReq.getTagDefineType())
                && CollectionUtils.isNotEmpty(tagDeleteReq.getItemIds())) {
            tagCustomMapper.deleteBatchByType(tagDeleteReq.getItemIds(),
                    tagDeleteReq.getTagDefineType().name());
        }
    }
}
