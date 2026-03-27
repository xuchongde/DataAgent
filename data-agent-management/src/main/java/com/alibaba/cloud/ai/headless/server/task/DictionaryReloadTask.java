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
package com.alibaba.cloud.ai.headless.server.task;

import com.alibaba.cloud.ai.headless.server.service.impl.DictWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(2)
public class DictionaryReloadTask implements CommandLineRunner {

    @Autowired
    private DictWordService dictWordService;

    @Override
    public void run(String... args) {
        updateKnowledgeDimValue();
    }

    public void updateKnowledgeDimValue() {
        try {
            log.debug("ApplicationStartedInit start");
            dictWordService.loadDictWord();
            log.debug("ApplicationStartedInit end");
        } catch (Exception e) {
            log.error("ApplicationStartedInit error", e);
        }
    }

    /** * reload knowledge task */
    @Scheduled(cron = "${reload.knowledge.corn:0 0/1 * * * ?}")
    public void reloadKnowledge() {
        log.debug("reloadKnowledge start");
        try {
            dictWordService.reloadDictWord();
        } catch (Exception e) {
            log.error("reloadKnowledge error", e);
        }
        log.debug("reloadKnowledge end");
    }
}
