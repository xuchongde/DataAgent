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
package com.alibaba.cloud.ai.headless.server.service;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.request.CanvasReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.CanvasSchemaResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.CanvasDO;

import java.util.List;

public interface CanvasService {

    List<CanvasDO> getCanvasList(Long domainId);

    List<CanvasSchemaResp> getCanvasSchema(Long domainId, User user);

    CanvasDO createOrUpdateCanvas(CanvasReq canvasReq, User user);

    void deleteCanvas(Long id);
}
