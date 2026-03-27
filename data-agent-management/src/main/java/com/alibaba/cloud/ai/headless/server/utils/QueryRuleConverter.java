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
import com.alibaba.cloud.ai.headless.api.pojo.ActionInfo;
import com.alibaba.cloud.ai.headless.api.pojo.RuleInfo;
import com.alibaba.cloud.ai.headless.api.pojo.enums.QueryRuleType;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryRuleReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.QueryRuleResp;
import com.alibaba.cloud.ai.headless.server.persistence.dataobject.QueryRuleDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QueryRuleConverter {

    public static QueryRuleDO convert2DO(QueryRuleReq queryRuleReq) {
        QueryRuleDO queryRuleDO = new QueryRuleDO();
        BeanUtils.copyProperties(queryRuleReq, queryRuleDO);
        queryRuleDO.setRuleType(queryRuleReq.getRuleType().name());
        queryRuleDO.setRule(JsonUtil.toString(queryRuleReq.getRule()));
        queryRuleDO.setAction(Objects.isNull(queryRuleReq.getAction()) ? ""
                : JsonUtil.toString(queryRuleReq.getAction()));
        queryRuleDO.setExt(JsonUtil.toString(queryRuleReq.getExt()));

        return queryRuleDO;
    }

    public static QueryRuleResp convert2Resp(QueryRuleDO queryRuleDO) {
        QueryRuleResp queryRuleResp = new QueryRuleResp();
        BeanUtils.copyProperties(queryRuleDO, queryRuleResp);
        queryRuleResp.setRuleType(QueryRuleType.valueOf(queryRuleDO.getRuleType()));
        queryRuleResp.setRule(JsonUtil.toObject(queryRuleDO.getRule(), RuleInfo.class));
        queryRuleResp.setAction(StringUtils.isEmpty(queryRuleDO.getAction()) ? new ActionInfo()
                : JsonUtil.toObject(queryRuleDO.getAction(), ActionInfo.class));
        queryRuleResp.setExt(JsonUtil.toMap(queryRuleDO.getExt(), String.class, String.class));

        return queryRuleResp;
    }

    public static List<QueryRuleResp> convert2RespList(List<QueryRuleDO> queryRules) {
        List<QueryRuleResp> queryRuleRespList = new ArrayList<>();
        queryRules.stream()
                .forEach(queryRuleDO -> queryRuleRespList.add(convert2Resp(queryRuleDO)));
        return queryRuleRespList;
    }
}
