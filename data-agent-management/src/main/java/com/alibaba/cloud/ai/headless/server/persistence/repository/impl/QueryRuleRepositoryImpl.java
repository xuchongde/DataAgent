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
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryRuleFilter;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.QueryRuleDO;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.QueryRuleMapper;
import com.alibaba.cloud.ai.headless.server.persistence.repository.QueryRuleRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QueryRuleRepositoryImpl implements QueryRuleRepository {

    private final QueryRuleMapper mapper;

    public QueryRuleRepositoryImpl(QueryRuleMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Integer create(QueryRuleDO queryRuleDO) {
        return mapper.insert(queryRuleDO);
    }

    @Override
    public Integer update(QueryRuleDO queryRuleDO) {
        return mapper.updateById(queryRuleDO);
    }

    @Override
    public QueryRuleDO getQueryRuleById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<QueryRuleDO> getQueryRules(QueryRuleFilter filter) {
        QueryWrapper<QueryRuleDO> wrapper = new QueryWrapper<>();
        if (CollectionUtils.isNotEmpty(filter.getRuleIds())) {
            wrapper.lambda().in(QueryRuleDO::getId, filter.getRuleIds());
        }
        if (CollectionUtils.isNotEmpty(filter.getDataSetIds())) {
            wrapper.lambda().in(QueryRuleDO::getDataSetId, filter.getDataSetIds());
        }
        if (CollectionUtils.isNotEmpty(filter.getStatusList())) {
            wrapper.lambda().in(QueryRuleDO::getStatus, filter.getStatusList());
        }
        wrapper.lambda().gt(QueryRuleDO::getPriority, 0);
        List<QueryRuleDO> queryRuleDOList = mapper.selectList(wrapper);

        QueryWrapper<QueryRuleDO> wrapperSys = new QueryWrapper<>();
        // 返回系统设置的规则
        wrapperSys.lambda().or().eq(QueryRuleDO::getPriority, 0L);
        List<QueryRuleDO> queryRuleDOListSys = mapper.selectList(wrapperSys);

        queryRuleDOList.addAll(queryRuleDOListSys);
        return queryRuleDOList;
    }
}
