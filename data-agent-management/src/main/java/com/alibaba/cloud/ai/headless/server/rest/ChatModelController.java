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

import com.alibaba.cloud.ai.headless.auth.api.authentication.utils.UserHolder;
import com.alibaba.cloud.ai.headless.common.config.ChatModel;
import com.alibaba.cloud.ai.headless.common.pojo.ChatApp;
import com.alibaba.cloud.ai.headless.common.pojo.ChatModelConfig;
import com.alibaba.cloud.ai.headless.common.pojo.ChatModelParameters;
import com.alibaba.cloud.ai.headless.common.pojo.Parameter;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.pojo.enums.AppModule;
import com.alibaba.cloud.ai.headless.common.service.ChatModelService;
import com.alibaba.cloud.ai.headless.common.util.ChatAppManager;
import com.alibaba.cloud.ai.headless.server.utils.ModelConfigHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/chat/model", "/openapi/chat/model"})
public class ChatModelController {
    @Autowired
    private ChatModelService chatModelService;
    @Autowired
    private ModelConfigHelper modelConfigHelper;

    @PostMapping
    public ChatModel createModel(@RequestBody ChatModel model,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = UserHolder.findUser(httpServletRequest, httpServletResponse);
        return chatModelService.createChatModel(model, user);
    }

    @PutMapping
    public ChatModel updateModel(@RequestBody ChatModel model,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = UserHolder.findUser(httpServletRequest, httpServletResponse);
        return chatModelService.updateChatModel(model, user);
    }

    @DeleteMapping("/{id}")
    public boolean deleteModel(@PathVariable("id") Integer id,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = UserHolder.findUser(httpServletRequest, httpServletResponse);
        chatModelService.deleteChatModel(id, user);
        return true;
    }

    @RequestMapping("/getModelList")
    public List<ChatModel> getModelList(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        User user = UserHolder.findUser(httpServletRequest, httpServletResponse);
        return chatModelService.getChatModels(user);
    }

    @RequestMapping("/getModelAppList")
    public Map<String, ChatApp> getChatAppList() {
        return ChatAppManager.getAllApps(AppModule.CHAT);
    }

    /*@RequestMapping("/getModelParameters")
    public List<Parameter> getModelParameters() {
        return ChatModelParameters.getParameters();
    }*/

    @PostMapping("/testConnection")
    public boolean testConnection(@RequestBody ChatModelConfig modelConfig) {
        return modelConfigHelper.testConnection(modelConfig);
    }
}
