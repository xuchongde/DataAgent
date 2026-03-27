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
package com.alibaba.cloud.ai.headless.server.manager;

import com.alibaba.cloud.ai.headless.api.pojo.enums.IdentifyType;
import com.alibaba.cloud.ai.headless.api.pojo.response.DimensionResp;
import com.alibaba.cloud.ai.headless.server.pojo.yaml.DimensionYamlTpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** manager to handle the dimension */
@Slf4j
@Service
public class DimensionYamlManager {

    public static List<DimensionYamlTpl> convert2DimensionYaml(List<DimensionResp> dimensions) {
        if (CollectionUtils.isEmpty(dimensions)) {
            return new ArrayList<>();
        }
        return dimensions.stream()
                .filter(dimension -> !dimension.getType().name()
                        .equalsIgnoreCase(IdentifyType.primary.name()))
                .map(DimensionYamlManager::convert2DimensionYamlTpl).collect(Collectors.toList());
    }

    public static DimensionYamlTpl convert2DimensionYamlTpl(DimensionResp dimension) {
        DimensionYamlTpl dimensionYamlTpl = new DimensionYamlTpl();
        BeanUtils.copyProperties(dimension, dimensionYamlTpl);
        dimensionYamlTpl.setName(dimension.getBizName());
        dimensionYamlTpl.setOwners(dimension.getCreatedBy());
        return dimensionYamlTpl;
    }
}
