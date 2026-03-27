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
package com.alibaba.cloud.ai.headless.common.pojo;

import com.alibaba.cloud.ai.headless.common.pojo.enums.TypeEnums;
import org.springframework.context.ApplicationEvent;

public class DataDeleteEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    private String name;
    private Long modelId;
    private Long id;
    private TypeEnums type;

    public DataDeleteEvent(Object source, String name, Long modelId, Long id, TypeEnums type) {
        super(source);
        this.name = name;
        this.modelId = modelId;
        this.id = id;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public void setType(TypeEnums type) {
        this.type = type;
    }

    public TypeEnums getType() {
        return type;
    }
}
