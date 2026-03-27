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
package com.alibaba.cloud.ai.headless.server.service.impl;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.common.pojo.enums.StatusEnum;
import com.alibaba.cloud.ai.headless.common.util.BeanMapper;
import com.alibaba.cloud.ai.headless.api.pojo.request.ClassReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.ClassResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.ClassDO;
import com.alibaba.cloud.ai.headless.server.persistence.repository.ClassRepository;
import com.alibaba.cloud.ai.headless.server.pojo.ClassFilter;
import com.alibaba.cloud.ai.headless.server.service.ClassService;
import com.alibaba.cloud.ai.headless.server.utils.ClassConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final ClassConverter converter;

    public ClassServiceImpl(ClassRepository classRepository, ClassConverter converter) {
        this.classRepository = classRepository;
        this.converter = converter;
    }

    @Override
    public ClassResp create(ClassReq classReq, User user) {

        ClassDO classDO = converter.convert(classReq);
        classDO.setId(null);
        Date date = new Date();
        classDO.setCreatedBy(user.getName());
        classDO.setCreatedAt(date);
        classDO.setUpdatedBy(user.getName());
        classDO.setUpdatedAt(date);
        classDO.setStatus(StatusEnum.ONLINE.getCode());

        classRepository.create(classDO);
        ClassDO classDOById = classRepository.getClassById(classDO.getId());

        return converter.convert2Resp(classDOById);
    }

    @Override
    public ClassResp update(ClassReq classReq, User user) {
        ClassDO classDO = classRepository.getClassById(classReq.getId());
        BeanMapper.mapper(classReq, classDO);
        classDO.setUpdatedAt(new Date());
        classDO.setUpdatedBy(user.getName());
        classRepository.update(classDO);
        return converter.convert2Resp(classRepository.getClassById(classReq.getId()));
    }

    @Override
    public Boolean delete(Long id, Boolean force, User user) throws Exception {
        ClassDO classDO = classRepository.getClassById(id);
        checkDeletePermission(classDO, user);
        checkDeleteValid(classDO, force);
        classRepository.delete(new ArrayList<>(Arrays.asList(id)));

        if (force) {
            // 删除子分类
            List<ClassDO> classDOList = classRepository.getAllClassDOList();
            Set<Long> deleteClassList = extractSubClass(id, classDOList);
            classRepository.delete(new ArrayList<>(deleteClassList));
        }
        return true;
    }

    private Set<Long> extractSubClass(Long id, List<ClassDO> classDOList) {
        Set<Long> classIdSet = new HashSet<>();
        for (ClassDO classDO : classDOList) {
            if (id.equals(classDO.getParentId())) {
                classIdSet.add(classDO.getId());
                classIdSet.addAll(extractSubClass(classDO.getId(), classDOList));
            }
        }
        return classIdSet;
    }

    private void checkDeleteValid(ClassDO classDelete, Boolean force) {
        List<ClassDO> classDOList = classRepository.getAllClassDOList();
        for (ClassDO classDO : classDOList) {
            if (classDO.getParentId().equals(classDelete.getId()) && !force) {
                throw new RuntimeException("该分类下还存在子分类, 暂不能删除, 请确认");
            }
        }
    }

    private void checkDeletePermission(ClassDO classDO, User user) throws Exception {
        if (user.getName().equalsIgnoreCase(classDO.getCreatedBy()) || user.isSuperAdmin()) {
            return;
        }
        throw new Exception(
                "delete operation is not supported at the moment. Please contact the admin.");
    }

    @Override
    public List<ClassResp> getClassList(ClassFilter filter, User user) {
        List<ClassDO> classDOList = classRepository.getClassDOList(filter);
        return converter.convert2RespList(classDOList);
    }
}
