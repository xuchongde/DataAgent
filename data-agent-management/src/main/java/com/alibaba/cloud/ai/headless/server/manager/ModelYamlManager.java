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

import com.alibaba.cloud.ai.headless.api.pojo.*;
import com.alibaba.cloud.ai.headless.api.pojo.enums.ModelDefineType;
import com.alibaba.cloud.ai.headless.api.pojo.response.DatabaseResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import com.alibaba.cloud.ai.headless.server.pojo.yaml.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

/** manager to handle the model */
@Service
@Slf4j
public class ModelYamlManager {

    public static synchronized DataModelYamlTpl convert2YamlObj(ModelResp modelResp,
            DatabaseResp databaseResp) {
        ModelDetail modelDetail = modelResp.getModelDetail();
        DataModelYamlTpl dataModelYamlTpl = new DataModelYamlTpl();
        dataModelYamlTpl.setType(databaseResp.getType());
        BeanUtils.copyProperties(modelDetail, dataModelYamlTpl);
        dataModelYamlTpl.setIdentifiers(modelDetail.getIdentifiers().stream()
                .map(ModelYamlManager::convert).collect(Collectors.toList()));
        dataModelYamlTpl.setDimensions(modelDetail.getDimensions().stream()
                .map(ModelYamlManager::convert).collect(Collectors.toList()));
        dataModelYamlTpl.setMeasures(modelDetail.getMeasures().stream()
                .map(ModelYamlManager::convert).collect(Collectors.toList()));
        dataModelYamlTpl.setName(modelResp.getBizName());
        dataModelYamlTpl.setSourceId(modelResp.getDatabaseId());
        if (modelDetail.getQueryType().equalsIgnoreCase(ModelDefineType.SQL_QUERY.getName())) {
            dataModelYamlTpl.setSqlQuery(modelDetail.getSqlQuery());
        } else {
            dataModelYamlTpl.setTableQuery(modelDetail.getTableQuery());
        }
        dataModelYamlTpl.setFilterSql(modelDetail.getFilterSql());
        dataModelYamlTpl.setFields(modelResp.getModelDetail().getFields());
        dataModelYamlTpl.setId(modelResp.getId());
        dataModelYamlTpl.setSqlVariables(modelDetail.getSqlVariables());
        return dataModelYamlTpl;
    }

    public static DimensionYamlTpl convert(Dimension dim) {
        DimensionYamlTpl dimensionYamlTpl = new DimensionYamlTpl();
        BeanUtils.copyProperties(dim, dimensionYamlTpl);
        dimensionYamlTpl.setName(dim.getBizName());
        if (Objects.isNull(dimensionYamlTpl.getExpr())) {
            dimensionYamlTpl.setExpr(dim.getBizName());
        }
        if (dim.getTypeParams() != null) {
            DimensionTimeTypeParams dimensionTimeTypeParamsTpl = new DimensionTimeTypeParams();
            dimensionTimeTypeParamsTpl.setIsPrimary(dim.getTypeParams().getIsPrimary());
            dimensionTimeTypeParamsTpl.setTimeGranularity(dim.getTypeParams().getTimeGranularity());
            dimensionYamlTpl.setTypeParams(dimensionTimeTypeParamsTpl);
        }
        return dimensionYamlTpl;
    }

    public static MeasureYamlTpl convert(Measure measure) {
        MeasureYamlTpl measureYamlTpl = new MeasureYamlTpl();
        BeanUtils.copyProperties(measure, measureYamlTpl);
        measureYamlTpl.setName(measure.getBizName());
        return measureYamlTpl;
    }

    public static IdentifyYamlTpl convert(Identify identify) {
        IdentifyYamlTpl identifyYamlTpl = new IdentifyYamlTpl();
        identifyYamlTpl.setName(identify.getBizName());
        identifyYamlTpl.setType(identify.getType());
        return identifyYamlTpl;
    }

}
