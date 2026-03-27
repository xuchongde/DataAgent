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

import com.alibaba.cloud.ai.headless.common.pojo.Parameter;
import com.alibaba.cloud.ai.headless.common.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public abstract class ParameterConfig {
    public static final String DEMO = "demo";
    @Autowired
    private SystemConfigService sysConfigService;

    @Autowired
    private Environment environment;

    /** @return system parameters to be set with user interface */
    protected List<Parameter> getSysParameters() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Parameter value will be derived in the following order: 1. `system config` set with user
     * interface 2. `system property` set with application.yaml file 3. `default value` set with
     * parameter declaration
     *
     * @param parameter instance
     * @return parameter value
     */
    public String getParameterValue(Parameter parameter) {
        String paramName = parameter.getName();
        String value = sysConfigService.getSystemConfig().getParameterByName(paramName);
        if (StringUtils.isBlank(value)) {
            if (environment.containsProperty(paramName)) {
                value = environment.getProperty(paramName);
            } else {
                value = parameter.getDefaultValue();
            }
        }

        return value;
    }

    protected static List<Parameter.Dependency> getDependency(String dependencyParameterName,
            List<String> includesValue, Map<String, String> setDefaultValue) {

        Parameter.Dependency.Show show = new Parameter.Dependency.Show();
        show.setIncludesValue(includesValue);

        Parameter.Dependency dependency = new Parameter.Dependency();
        dependency.setName(dependencyParameterName);
        dependency.setShow(show);
        dependency.setSetDefaultValue(setDefaultValue);
        List<Parameter.Dependency> dependencies = new ArrayList<>();
        dependencies.add(dependency);
        return dependencies;
    }
}
