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
package com.alibaba.cloud.ai.headless.server.utils;

import com.alibaba.cloud.ai.headless.common.util.JsonUtil;
import com.alibaba.cloud.ai.headless.api.pojo.request.TagObjectReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.TagObjectResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.TagObjectDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class TagObjectConverter {

    public static TagObjectDO convert(TagObjectReq tagObjectReq) {
        TagObjectDO tagObjectDO = new TagObjectDO();
        BeanUtils.copyProperties(tagObjectReq, tagObjectDO);
        tagObjectDO.setId(null);
        tagObjectDO.setExt(tagObjectReq.getExtJson());
        return tagObjectDO;
    }

    public static TagObjectResp convert2Resp(TagObjectDO tagObjectDO) {
        TagObjectResp tagObjectResp = new TagObjectResp();
        BeanUtils.copyProperties(tagObjectDO, tagObjectResp);
        if (StringUtils.isNotEmpty(tagObjectDO.getExt())) {
            tagObjectResp.setExt(JsonUtil.objectToMapString(tagObjectDO.getExt()));
        }
        return tagObjectResp;
    }

    public static List<TagObjectResp> convert2RespList(List<TagObjectDO> tagObjectDOList) {
        List<TagObjectResp> tagObjectRespList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tagObjectDOList)) {
            tagObjectDOList.stream()
                    .forEach(tagObjectDO -> tagObjectRespList.add(convert2Resp(tagObjectDO)));
        }
        return tagObjectRespList;
    }
}
