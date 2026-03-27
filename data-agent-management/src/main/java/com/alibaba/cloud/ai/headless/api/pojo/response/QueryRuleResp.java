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
package com.alibaba.cloud.ai.headless.api.pojo.response;

import com.alibaba.cloud.ai.headless.api.pojo.ActionInfo;
import com.alibaba.cloud.ai.headless.api.pojo.RuleInfo;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaItem;
import com.alibaba.cloud.ai.headless.api.pojo.enums.QueryRuleType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class QueryRuleResp extends SchemaItem {

    /** dataSetID */
    private Long dataSetId;

    /** 规则的优先级, 1-低,2-中,3-高 */
    private Integer priority = 1;

    /** 规则类型 */
    @NotNull
    private QueryRuleType ruleType;

    /** 具体规则信息 */
    @NotNull
    private RuleInfo rule;

    /** 规则输出信息 */
    private ActionInfo action;

    /** 扩展信息 */
    private Map<String, String> ext;
}
