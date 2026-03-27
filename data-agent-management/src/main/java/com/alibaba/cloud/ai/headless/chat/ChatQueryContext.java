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
package com.alibaba.cloud.ai.headless.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.alibaba.cloud.ai.headless.common.pojo.enums.Text2SQLType;
import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SchemaMapInfo;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticSchema;
import com.alibaba.cloud.ai.headless.api.pojo.enums.ChatWorkflowState;
import com.alibaba.cloud.ai.headless.api.pojo.request.QueryNLReq;
import com.alibaba.cloud.ai.headless.api.pojo.response.ParseResp;
import com.alibaba.cloud.ai.headless.chat.query.SemanticQuery;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class ChatQueryContext implements Serializable {

    private QueryNLReq request;
    private ParseResp parseResp;
    private Map<Long, List<Long>> modelIdToDataSetIds;
    private List<SemanticQuery> candidateQueries = new ArrayList<>();
    private SchemaMapInfo mapInfo = new SchemaMapInfo();
    @JsonIgnore
    private SemanticSchema semanticSchema;
    private ChatWorkflowState chatWorkflowState;

    public ChatQueryContext() {
        this(new QueryNLReq());
    }

    public ChatQueryContext(QueryNLReq request) {
        this.request = request;
        SemanticParseInfo parseInfo = request.getSelectedParseInfo();
        if (Objects.nonNull(parseInfo) && Objects.nonNull(parseInfo.getDataSetId())) {
            mapInfo.setMatchedElements(parseInfo.getDataSetId(), parseInfo.getElementMatches());
        }
    }

    public boolean needSQL() {
        return !request.getText2SQLType().equals(Text2SQLType.NONE);
    }

    public DataSetSchema getDataSetSchema(Long dataSetId) {
        return semanticSchema.getDataSetSchema(dataSetId);
    }

    public List<SemanticQuery> getCandidateQueries() {
        candidateQueries = candidateQueries.stream()
                .sorted(Comparator.comparing(
                        semanticQuery -> semanticQuery.getParseInfo().getScore(),
                        Comparator.reverseOrder()))
                .limit(1).collect(Collectors.toList());
        return candidateQueries;
    }

    public boolean containsPartitionDimensions(Long dataSetId) {
        SemanticSchema semanticSchema = this.getSemanticSchema();
        DataSetSchema dataSetSchema = semanticSchema.getDataSetSchemaMap().get(dataSetId);
        return dataSetSchema.containsPartitionDimensions();
    }
}
