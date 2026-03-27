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
package com.alibaba.cloud.ai.headless.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.alibaba.cloud.ai.headless.common.config.SystemConfig;
import com.alibaba.cloud.ai.headless.common.persistence.dataobject.SystemConfigDO;
import com.alibaba.cloud.ai.headless.common.persistence.mapper.SystemConfigMapper;
import com.alibaba.cloud.ai.headless.common.pojo.Parameter;
import com.alibaba.cloud.ai.headless.common.service.SystemConfigService;
import com.alibaba.cloud.ai.headless.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfigDO>
        implements SystemConfigService {

    @Autowired
    private Environment environment;

    // Cache field to store the system configuration
    private AtomicReference<SystemConfig> cachedSystemConfig = new AtomicReference<>();

    @Override
    public SystemConfig getSystemConfig() {
        SystemConfig cachedConfig = cachedSystemConfig.get();
        if (cachedConfig != null) {
            return cachedConfig;
        }
        SystemConfig systemConfigDb = getSystemConfigFromDB();
        cachedSystemConfig.set(systemConfigDb);
        return systemConfigDb;
    }

    private SystemConfig getSystemConfigFromDB() { // 加上id ，如果有多条记录，会出错
        List<SystemConfigDO> list = this.lambdaQuery().eq(SystemConfigDO::getId, 1).list();
        if (CollectionUtils.isEmpty(list)) {
            SystemConfig systemConfig = new SystemConfig();
            systemConfig.setId(1);
            systemConfig.init();
            // use system property to initialize system parameter
            systemConfig.getParameters().stream().forEach(p -> {
                if (environment.containsProperty(p.getName())) {
                    p.setValue(environment.getProperty(p.getName()));
                }
            });
            save(systemConfig);
            return systemConfig;
        }

        return convert(list.iterator().next());
    }

    @Override
    public void save(SystemConfig sysConfig) {
        SystemConfigDO systemConfigDO = convert(sysConfig);
        saveOrUpdate(systemConfigDO);
        cachedSystemConfig.set(sysConfig);
    }

    private SystemConfig convert(SystemConfigDO systemConfigDO) {
        SystemConfig sysParameter = new SystemConfig();
        sysParameter.setId(systemConfigDO.getId());
        List<Parameter> parameters = JsonUtil.toObject(systemConfigDO.getParameters(),
                new TypeReference<List<Parameter>>() {});
        sysParameter.setParameters(parameters);
        sysParameter.setAdminList(systemConfigDO.getAdmin());
        return sysParameter;
    }

    private SystemConfigDO convert(SystemConfig sysParameter) {
        SystemConfigDO sysParameterDO = new SystemConfigDO();
        sysParameterDO.setId(sysParameter.getId());
        sysParameterDO.setParameters(JSONObject.toJSONString(sysParameter.getParameters()));
        sysParameterDO.setAdmin(sysParameter.getAdmin());
        return sysParameterDO;
    }
}
