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
import com.alibaba.cloud.ai.headless.api.pojo.request.DictItemFilter;
import com.alibaba.cloud.ai.headless.api.pojo.request.DictItemReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DictItemResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.DictConfDO;
import com.alibaba.cloud.ai.headless.server.persistence.repository.DictRepository;
import com.alibaba.cloud.ai.headless.server.service.DictConfService;
import com.alibaba.cloud.ai.headless.server.utils.DictUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DictConfServiceImpl implements DictConfService {

    private final DictRepository dictRepository;
    private final DictUtils dictConverter;

    public DictConfServiceImpl(DictRepository dictRepository, DictUtils dictConverter) {
        this.dictRepository = dictRepository;
        this.dictConverter = dictConverter;
    }

    @Override
    public DictItemResp addDictConf(DictItemReq itemValueReq, User user) {
        DictConfDO dictConfDO = dictConverter.generateDictConfDO(itemValueReq, user);
        Boolean exist = checkConfExist(itemValueReq, user);
        if (exist) {
            throw new RuntimeException("dictConf is existed");
        }
        Long id = dictRepository.addDictConf(dictConfDO);
        log.debug("dictConfDO:{}", dictConfDO);

        DictItemFilter filter =
                DictItemFilter.builder().id(id).status(itemValueReq.getStatus()).build();
        Optional<DictItemResp> dictItemResp = queryDictConf(filter, user).stream().findFirst();
        if (dictItemResp.isPresent()) {
            return dictItemResp.get();
        }
        return null;
    }

    private Boolean checkConfExist(DictItemReq itemValueReq, User user) {
        DictItemFilter filter = DictItemFilter.builder().build();
        BeanUtils.copyProperties(itemValueReq, filter);
        filter.setStatus(null);
        Optional<DictItemResp> dictItemResp = queryDictConf(filter, user).stream().findFirst();
        if (dictItemResp.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public DictItemResp editDictConf(DictItemReq itemValueReq, User user) {
        DictConfDO dictConfDO = dictConverter.generateDictConfDO(itemValueReq, user);
        dictRepository.editDictConf(dictConfDO);
        DictItemFilter filter = DictItemFilter.builder().build();
        BeanUtils.copyProperties(itemValueReq, filter);
        Optional<DictItemResp> dictItemResp = queryDictConf(filter, user).stream().findFirst();
        if (dictItemResp.isPresent()) {
            return dictItemResp.get();
        }
        return null;
    }

    @Override
    public List<DictItemResp> queryDictConf(DictItemFilter dictItemFilter, User user) {
        return dictRepository.queryDictConf(dictItemFilter);
    }
}
