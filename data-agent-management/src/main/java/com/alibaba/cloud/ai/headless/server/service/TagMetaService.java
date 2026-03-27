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

import com.github.pagehelper.PageInfo;
import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.enums.TagDefineType;
import com.alibaba.cloud.ai.headless.api.pojo.request.TagDeleteReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.TagFilterPageReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.TagReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.TagItem;
import com.alibaba.cloud.ai.headless.api.pojo.response.TagResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.TagDO;
import com.alibaba.cloud.ai.headless.server.pojo.TagFilter;

import java.util.List;

public interface TagMetaService {

    TagResp create(TagReq tagReq, User user);

    Integer createBatch(List<TagReq> tagReqList, User user);

    Boolean delete(Long id, User user);

    Boolean deleteBatch(List<TagDeleteReq> tagDeleteReqList, User user);

    TagResp getTag(Long id, User user);

    List<TagResp> getTags(TagFilter tagFilter);

    List<TagDO> getTagDOList(TagFilter tagFilter);

    PageInfo<TagResp> queryTagMarketPage(TagFilterPageReq tagMarketPageReq, User user);

    List<TagItem> getTagItems(List<Long> itemIds, TagDefineType tagDefineType);
}
