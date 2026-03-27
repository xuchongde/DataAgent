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

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.pojo.enums.TypeEnums;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.CollectDO;
import com.alibaba.cloud.ai.headless.server.persistence.mapper.CollectMapper;
import com.alibaba.cloud.ai.headless.server.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CollectServiceImpl implements CollectService {

    public static final String type = "metric";
    @Resource
    private CollectMapper collectMapper;

    @Override
    public Boolean collect(User user, CollectDO collectReq) {
        CollectDO collect = new CollectDO();
        collect.setType(StringUtils.isEmpty(collectReq.getType()) ? type : collectReq.getType());
        collect.setUsername(user.getName());
        collect.setCollectId(collectReq.getCollectId());
        collectMapper.insert(collect);
        return true;
    }

    @Override
    public Boolean unCollect(User user, Long id) {
        QueryWrapper<CollectDO> collectDOQueryWrapper = new QueryWrapper<>();
        collectDOQueryWrapper.lambda().eq(CollectDO::getUsername, user.getName());
        collectDOQueryWrapper.lambda().eq(CollectDO::getId, id);
        collectDOQueryWrapper.lambda().eq(CollectDO::getType, type);
        collectMapper.delete(collectDOQueryWrapper);
        return true;
    }

    @Override
    public Boolean unCollect(User user, CollectDO collectReq) {
        QueryWrapper<CollectDO> collectDOQueryWrapper = new QueryWrapper<>();
        collectDOQueryWrapper.lambda().eq(CollectDO::getUsername, user.getName());
        collectDOQueryWrapper.lambda().eq(CollectDO::getCollectId, collectReq.getCollectId());
        collectDOQueryWrapper.lambda().eq(CollectDO::getType, collectReq.getType());
        collectMapper.delete(collectDOQueryWrapper);
        return true;
    }

    @Override
    public List<CollectDO> getCollectionList(String username) {
        QueryWrapper<CollectDO> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(username)) {
            queryWrapper.lambda().eq(CollectDO::getUsername, username);
        }
        return collectMapper.selectList(queryWrapper);
    }

    @Override
    public List<CollectDO> getCollectionList(String username, TypeEnums typeEnums) {
        QueryWrapper<CollectDO> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(username)) {
            queryWrapper.lambda().eq(CollectDO::getUsername, username);
        }
        queryWrapper.lambda().eq(CollectDO::getType, typeEnums.name().toLowerCase());
        return collectMapper.selectList(queryWrapper);
    }
}
