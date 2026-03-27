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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.DomainDO;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.DomainDOMapper;
import com.alibaba.cloud.ai.headless.server.persistence.repository.DomainRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DomainRepositoryImpl implements DomainRepository {

    private DomainDOMapper domainDOMapper;

    public DomainRepositoryImpl(DomainDOMapper domainDOMapper) {
        this.domainDOMapper = domainDOMapper;
    }

    @Override
    public void createDomain(DomainDO metaDomainDO) {
        domainDOMapper.insert(metaDomainDO);
    }

    @Override
    public void updateDomain(DomainDO metaDomainDO) {
        domainDOMapper.updateById(metaDomainDO);
    }

    @Override
    public void deleteDomain(Long id) {
        domainDOMapper.deleteById(id);
    }

    @Override
    public List<DomainDO> getDomainList() {
        return domainDOMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public DomainDO getDomainById(Long id) {
        return domainDOMapper.selectById(id);
    }

    @Override
    public List<DomainDO> getDomainByBizName(String bizName) {
        QueryWrapper<DomainDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DomainDO::getBizName, bizName);
        return domainDOMapper.selectList(queryWrapper);
    }

}
