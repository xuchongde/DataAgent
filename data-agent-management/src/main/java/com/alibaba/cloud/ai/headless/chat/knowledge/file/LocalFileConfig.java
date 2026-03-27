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
package com.alibaba.cloud.ai.headless.chat.knowledge.file;

import com.alibaba.cloud.ai.headless.chat.knowledge.helper.HanlpHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;

@Data
@Configuration
@Slf4j
public class LocalFileConfig {

    @Value("${s2.dict.directory.latest:/data/dictionary/custom}")
    private String dictDirectoryLatest;

    @Value("${s2.dict.directory.backup:./data/dictionary/backup}")
    private String dictDirectoryBackup;

    public String getDictDirectoryLatest() {
        return getDictDirectoryPrefixDir() + dictDirectoryLatest;
    }

    public String getDictDirectoryBackup() {
        return getDictDirectoryPrefixDir() + dictDirectoryBackup;
    }

    private String getDictDirectoryPrefixDir() {
        try {
            return HanlpHelper.getHanlpPropertiesPath();
        } catch (FileNotFoundException e) {
            log.error("getDictDirectoryPrefixDir error: ", e);
        }
        return "";
    }
}
