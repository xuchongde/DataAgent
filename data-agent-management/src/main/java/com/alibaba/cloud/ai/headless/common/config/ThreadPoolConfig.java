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
package com.alibaba.cloud.ai.headless.common.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolConfig {

    @Bean("eventExecutor")
    public ThreadPoolExecutor getTaskEventExecutor() {
        return new ThreadPoolExecutor(4, 8, 60 * 3, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("supersonic-event-pool-").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean("commonExecutor")
    public ThreadPoolExecutor getCommonExecutor() {
        return new ThreadPoolExecutor(8, 16, 60 * 3, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("supersonic-common-pool-").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean("mapExecutor")
    public ThreadPoolExecutor getMapExecutor() {
        return new ThreadPoolExecutor(8, 16, 60 * 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("supersonic-map-pool-").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean("chatExecutor")
    public ThreadPoolExecutor getChatExecutor() {
        return new ThreadPoolExecutor(8, 16, 60 * 3, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("supersonic-chat-pool-").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
