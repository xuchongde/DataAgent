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
import com.alibaba.cloud.ai.headless.api.pojo.request.ClassReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.ClassResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.DomainResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.TagObjectResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.ClassDO;
import com.alibaba.cloud.ai.headless.server.persistence.repository.ClassRepository;
import com.alibaba.cloud.ai.headless.server.service.DomainService;
import com.alibaba.cloud.ai.headless.server.service.TagObjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ClassConverter {

    private final ClassRepository classRepository;
    private final DomainService domainService;
    private final TagObjectService tagObjectService;

    public ClassConverter(ClassRepository classRepository, DomainService domainService,
            TagObjectService tagObjectService) {
        this.classRepository = classRepository;
        this.domainService = domainService;
        this.tagObjectService = tagObjectService;
    }

    public ClassDO convert(ClassReq classReq) {
        ClassDO classDO = new ClassDO();
        BeanUtils.copyProperties(classReq, classDO);
        classDO.setType(classReq.getTypeEnum().name());
        List<Long> itemIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(classReq.getItemIds())) {
            itemIds = classReq.getItemIds();
        }
        classDO.setItemIds(JsonUtil.toString(itemIds));

        return classDO;
    }

    public ClassResp convert2Resp(ClassDO classDO) {
        Map<Long, DomainResp> idAndDomain = getIdAndDomain();
        Map<Long, String> classFullPathMap = getClassFullPathMap();
        return convert2RespInternal(classDO, idAndDomain, classFullPathMap);
    }

    private ClassResp convert2RespInternal(ClassDO classDO, Map<Long, DomainResp> idAndDomain,
            Map<Long, String> classFullPathMap) {
        ClassResp classResp = new ClassResp();
        BeanUtils.copyProperties(classDO, classResp);

        Long domainId = classResp.getDomainId();
        if (Objects.nonNull(idAndDomain) && idAndDomain.containsKey(domainId)
                && Objects.nonNull(idAndDomain.get(domainId))) {
            classResp.setDomainName(idAndDomain.get(domainId).getName());
        }

        if (Objects.nonNull(classFullPathMap) && classFullPathMap.containsKey(classResp.getId())) {
            classResp.setFullPath(classFullPathMap.get(classResp.getId()));
        }

        return classResp;
    }

    public List<ClassResp> convert2RespList(List<ClassDO> classDOList) {
        List<ClassResp> classRespList = new ArrayList<>();
        Map<Long, DomainResp> idAndDomain = getIdAndDomain();
        Map<Long, String> classFullPathMap = getClassFullPathMap();
        for (ClassDO classDO : classDOList) {
            ClassResp classResp = convert2RespInternal(classDO, idAndDomain, classFullPathMap);
            if (Objects.nonNull(classResp)) {
                classRespList.add(classResp);
            }
        }
        return classRespList;
    }

    public Map<Long, String> getClassFullPathMap() {
        Map<Long, String> classFullPathMap = new HashMap<>();
        List<ClassDO> classDOList = classRepository.getAllClassDOList();
        Map<Long, ClassDO> classDOMap = classDOList.stream()
                .collect(Collectors.toMap(ClassDO::getId, a -> a, (k1, k2) -> k1));
        for (ClassDO classDO : classDOList) {
            final Long domainId = classDO.getId();
            StringBuilder fullPath = new StringBuilder(classDO.getBizName() + "/");
            Long parentId = classDO.getParentId();
            while (parentId != null && parentId > 0) {
                classDO = classDOMap.get(parentId);
                if (classDO == null) {
                    String message = String.format("get domain : %s failed", parentId);
                    throw new RuntimeException(message);
                }
                fullPath.insert(0, classDO.getBizName() + "/");
                parentId = classDO.getParentId();
            }
            classFullPathMap.put(domainId, fullPath.toString());
        }
        return classFullPathMap;
    }

    public Map<Long, DomainResp> getIdAndDomain() {
        return domainService.getDomainMap();
    }

    public Map<Long, TagObjectResp> getIdAndTagSet() {
        return tagObjectService.getAllTagObjectMap();
    }
}
