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
package com.alibaba.cloud.ai.headless.server.modeller;


import com.alibaba.cloud.ai.headless.api.pojo.DbSchema;
import com.alibaba.cloud.ai.headless.api.pojo.ModelSchema;
import com.alibaba.cloud.ai.headless.api.pojo.request.ModelBuildReq;

import java.util.List;

/**
 * A semantic modeler builds semantic-layer schemas from database-layer schemas.
 */
public interface SemanticModeller {

    void build(DbSchema dbSchema, List<DbSchema> otherDbSchema, ModelSchema modelSchema,
            ModelBuildReq modelBuildReq);

}
