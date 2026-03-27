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
package com.alibaba.cloud.ai.headless.api.pojo.request;

import com.alibaba.cloud.ai.headless.common.pojo.DataFormat;
import com.alibaba.cloud.ai.headless.api.pojo.RelateDimension;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaItem;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Data
public class MetricBaseReq extends SchemaItem {

    private Long modelId;

    private String alias;

    private String dataFormatType;

    private DataFormat dataFormat;

    private List<String> classifications;

    private RelateDimension relateDimension;

    private int isTag;

    private Map<String, Object> ext;

    public String getClassifications() {
        if (classifications == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(classifications)) {
            return "";
        }
        return StringUtils.join(classifications, ",");
    }
}
