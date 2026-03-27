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
package com.alibaba.cloud.ai.headless.common.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.cloud.ai.headless.common.config.ChatModel;
import com.alibaba.cloud.ai.headless.common.persistence.dataobject.ChatModelDO;
import com.alibaba.cloud.ai.headless.common.persistence.mapper.ChatModelMapper;
import com.alibaba.cloud.ai.headless.common.pojo.ChatModelConfig;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.service.ChatModelService;
import com.alibaba.cloud.ai.headless.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatModelServiceImpl extends ServiceImpl<ChatModelMapper, ChatModelDO>
        implements ChatModelService {
    @Override
    public List<ChatModel> getChatModels(User user) {
        return list().stream().map(this::convert).filter(chatModel -> {
            if (chatModel.isPublic() || user.isSuperAdmin()
                    || chatModel.getCreatedBy().equals(user.getName())
                    || chatModel.getViewers().contains(user.getName())) {
                return true;
            }
            return false;
        }).sorted(Comparator.comparingLong(ChatModel::getId)).collect(Collectors.toList());
    }

    @Override
    public ChatModel getChatModel(Integer id) {
        if (id == null) {
            return null;
        }
        return convert(getById(id));
    }

    @Override
    public ChatModel createChatModel(ChatModel chatModel, User user) {
        ChatModelDO chatModelDO = convert(chatModel);
        chatModelDO.setCreatedBy(user.getName());
        chatModelDO.setCreatedAt(new Date());
        chatModelDO.setUpdatedBy(user.getName());
        chatModelDO.setUpdatedAt(chatModelDO.getCreatedAt());
        chatModelDO.setIsOpen(chatModel.getIsOpen());
        if (StringUtils.isBlank(chatModel.getAdmin())) {
            chatModelDO.setAdmin(user.getName());
        }
        if (!chatModel.getViewers().isEmpty()) {
            chatModelDO.setViewer(JsonUtil.toString(chatModel.getViewers()));
        }
        save(chatModelDO);
        chatModel.setId(chatModelDO.getId());
        return chatModel;
    }

    @Override
    public ChatModel updateChatModel(ChatModel chatModel, User user) {
        ChatModelDO chatModelDO = convert(chatModel);
        chatModelDO.setUpdatedBy(user.getName());
        chatModelDO.setUpdatedAt(new Date());
        chatModelDO.setIsOpen(chatModel.getIsOpen());
        if (StringUtils.isBlank(chatModel.getAdmin())) {
            chatModel.setAdmin(user.getName());
        }
        if (!chatModel.getViewers().isEmpty()) {
            chatModelDO.setViewer(JsonUtil.toString(chatModel.getViewers()));
        }
        updateById(chatModelDO);
        return chatModel;
    }

    @Override
    public void deleteChatModel(Integer id, User user) {
        ChatModel chatModel = getChatModel(id);
        if (!checkAdminPermission(user, chatModel)) {
            throw new RuntimeException("没有权限删除该大模型");
        }

        removeById(id);
    }

    private ChatModel convert(ChatModelDO chatModelDO) {
        if (chatModelDO == null) {
            return null;
        }
        ChatModel chatModel = new ChatModel();
        BeanUtils.copyProperties(chatModelDO, chatModel);
        chatModel.setConfig(JsonUtil.toObject(chatModelDO.getConfig(), ChatModelConfig.class));
        chatModel.setViewers(JsonUtil.toList(chatModelDO.getViewer(), String.class));
        return chatModel;
    }

    private ChatModelDO convert(ChatModel chatModel) {
        if (chatModel == null) {
            return null;
        }
        ChatModelDO chatModelDO = new ChatModelDO();
        BeanUtils.copyProperties(chatModel, chatModelDO);
        chatModelDO.setConfig(JsonUtil.toString(chatModel.getConfig()));
        return chatModelDO;
    }

    private boolean checkAdminPermission(User user, ChatModel chatModel) {
        String admin = chatModel.getAdmin();
        if (user.isSuperAdmin()) {
            return true;
        }
        return admin != null && admin.equals(user.getName())
                || chatModel.getCreatedBy().equals(user.getName());
    }
}
