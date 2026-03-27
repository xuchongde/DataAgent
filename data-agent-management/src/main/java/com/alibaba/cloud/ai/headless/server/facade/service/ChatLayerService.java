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
package com.alibaba.cloud.ai.headless.server.facade.service;

import com.alibaba.cloud.ai.headless.common.pojo.User;
import com.alibaba.cloud.ai.headless.api.pojo.SqlEvaluation;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryMapReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryNLReq;
import com.alibaba.cloud.ai.headless.api.pojo.request.QuerySqlReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.MapInfoResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.MapResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.ParseResp;
import com.alibaba.cloud.ai.headless.api.pojo.response.SearchResult;

import java.util.List;

/** This interface adds natural language support to the semantic layer. */
public interface ChatLayerService {

    MapResp map(QueryNLReq queryNLReq);

    MapInfoResp map(QueryMapReq queryMapReq);

    List<SearchResult> retrieve(QueryNLReq queryNLReq);

    ParseResp parse(QueryNLReq queryNLReq);

    void correct(QuerySqlReq querySqlReq, User user);

    SqlEvaluation validate(QuerySqlReq querySqlReq, User user);
}
