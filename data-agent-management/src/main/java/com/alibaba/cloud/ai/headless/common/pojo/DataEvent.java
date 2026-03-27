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

import com.alibaba.cloud.ai.headless.common.pojo.enums.EventType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class DataEvent extends ApplicationEvent {

    private final List<DataItem> dataItems;

    private final EventType eventType;

    private final String userName;

    public DataEvent(Object source, List<DataItem> dataItems, EventType eventType,
            String userName) {
        super(source);
        this.dataItems = dataItems;
        this.eventType = eventType;
        this.userName = userName;
    }

}
