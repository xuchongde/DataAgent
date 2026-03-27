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

import com.github.pagehelper.PageInfo;
import com.alibaba.cloud.ai.headless.api.pojo.request.DictValueReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DictValueResp;

import java.util.List;

public interface FileHandler {

    /**
     * backup files to a specific directory config: dict.directory.backup
     *
     * @param fileName
     */
    void backupFile(String fileName);

    /**
     * create a directory
     *
     * @param path
     */
    void createDir(String path);

    Boolean existPath(String path);

    /**
     * write data to a specific file, config dir: dict.directory.latest
     *
     * @param data
     * @param fileName
     * @param append
     */
    void writeFile(List<String> data, String fileName, Boolean append);

    /**
     * get the knowledge file root directory
     *
     * @return
     */
    String getDictRootPath();

    /**
     * delete dictionary file automatic backup
     *
     * @param fileName
     * @return
     */
    Boolean deleteDictFile(String fileName);

    /**
     * delete files directly without backup
     *
     * @param fileName
     */
    void deleteFile(String fileName);

    PageInfo<DictValueResp> queryDictValue(String fileName, DictValueReq dictValueReq);

    String queryDictFilePath(String fileName);
}
