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

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.pojo.DataFormat;
import com.alibaba.cloud.ai.headless.common.pojo.enums.PublishEnum;
import com.alibaba.cloud.ai.headless.common.pojo.enums.StatusEnum;
import com.alibaba.cloud.ai.headless.common.pojo.enums.TypeEnums;
import com.alibaba.cloud.ai.headless.common.util.BeanMapper;
import com.alibaba.cloud.ai.headless.api.pojo.Dimension;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByFieldParams;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByMeasureParams;
import com.alibaba.cloud.ai.headless.api.pojo.MetricDefineByMetricParams;
import com.alibaba.cloud.ai.headless.api.pojo.RelateDimension;
import com.alibaba.cloud.ai.headless.api.pojo.enums.MetricDefineType;
import com.alibaba.cloud.ai.headless.api.pojo.request.MetricReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.DataSetResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.MetricResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ModelResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.MetricDO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricConverter {

    public static MetricDO convert2MetricDO(MetricReq metricReq) {
        MetricDO metricDO = new MetricDO();
        BeanMapper.mapper(metricReq, metricDO);
        metricDO.setType(metricReq.getMetricType().name());
        metricDO.setTypeParams(metricReq.getTypeParamsJson());
        metricDO.setDataFormat(JSONObject.toJSONString(metricReq.getDataFormat()));
        metricDO.setClassifications(metricReq.getClassifications());
        metricDO.setRelateDimensions(JSONObject.toJSONString(metricReq.getRelateDimension()));
        metricDO.setStatus(StatusEnum.ONLINE.getCode());
        metricDO.setIsPublish(PublishEnum.PUBLISHED.getCode());
        if (metricReq.getExt() != null) {
            metricDO.setExt(JSONObject.toJSONString(metricReq.getExt()));
        }
        metricDO.setDefineType(metricReq.getMetricDefineType().name());
        return metricDO;
    }

    public static MetricDO convert(MetricDO metricDO, MetricReq metricReq) {
        BeanMapper.mapper(metricReq, metricDO);
        metricDO.setDefineType(metricReq.getMetricDefineType().name());
        if (metricReq.getDataFormat() != null) {
            metricDO.setDataFormat(JSONObject.toJSONString(metricReq.getDataFormat()));
        }
        if (metricReq.getRelateDimension() != null) {
            metricDO.setRelateDimensions(JSONObject.toJSONString(metricReq.getRelateDimension()));
        }
        if (metricReq.getClassifications() != null) {
            metricDO.setClassifications(metricReq.getClassifications());
        }
        if (metricReq.getExt() != null) {
            metricDO.setExt(JSONObject.toJSONString(metricReq.getExt()));
        }
        if (metricReq.getTypeParamsJson() != null) {
            metricDO.setTypeParams(metricReq.getTypeParamsJson());
        }
        return metricDO;
    }

    public static MetricResp convert2MetricResp(MetricDO metricDO) {
        return convert2MetricResp(metricDO, new HashMap<>(), Lists.newArrayList());
    }

    public static MetricResp convert2MetricResp(MetricDO metricDO, Map<Long, ModelResp> modelMap,
            List<Long> collect) {
        MetricResp metricResp = new MetricResp();
        BeanUtils.copyProperties(metricDO, metricResp);

        metricResp
                .setDataFormat(JSONObject.parseObject(metricDO.getDataFormat(), DataFormat.class));
        ModelResp modelResp = modelMap.get(metricDO.getModelId());
        if (modelResp != null) {
            metricResp.setModelName(modelResp.getName());
            metricResp.setModelBizName(modelResp.getBizName());
            metricResp.setDomainId(modelResp.getDomainId());
            List<Dimension> timeDims = modelResp.getTimeDimension();
            if (CollectionUtils.isNotEmpty(timeDims)) {
                metricResp.setContainsPartitionDimensions(true);
            }
        }

        metricResp.setIsCollect(collect != null && collect.contains(metricDO.getId()));
        metricResp.setClassifications(metricDO.getClassifications());
        metricResp.setRelateDimension(
                JSONObject.parseObject(metricDO.getRelateDimensions(), RelateDimension.class));
        if (metricDO.getExt() != null) {
            metricResp.setExt(JSONObject.parseObject(metricDO.getExt(), HashMap.class));
        }
        metricResp.setTypeEnum(TypeEnums.METRIC);
        if (MetricDefineType.MEASURE.name().equalsIgnoreCase(metricDO.getDefineType())) {
            metricResp.setMetricDefineByMeasureParams(JSONObject
                    .parseObject(metricDO.getTypeParams(), MetricDefineByMeasureParams.class));
        } else if (MetricDefineType.METRIC.name().equalsIgnoreCase(metricDO.getDefineType())) {
            metricResp.setMetricDefineByMetricParams(JSONObject
                    .parseObject(metricDO.getTypeParams(), MetricDefineByMetricParams.class));
        } else if (MetricDefineType.FIELD.name().equalsIgnoreCase(metricDO.getDefineType())) {
            metricResp.setMetricDefineByFieldParams(JSONObject.parseObject(metricDO.getTypeParams(),
                    MetricDefineByFieldParams.class));
        }
        if (metricDO.getDefineType() != null) {
            metricResp.setMetricDefineType(MetricDefineType.valueOf(metricDO.getDefineType()));
        }
        metricResp.setIsTag(metricDO.getIsTag());
        return metricResp;
    }

    public static List<MetricResp> filterByDataSet(List<MetricResp> metricResps,
            DataSetResp dataSetResp) {
        return metricResps.stream()
                .filter(metricResp -> dataSetResp.metricIds().contains(metricResp.getId())
                        || dataSetResp.getAllIncludeAllModels().contains(metricResp.getModelId()))
                .collect(Collectors.toList());
    }
}
