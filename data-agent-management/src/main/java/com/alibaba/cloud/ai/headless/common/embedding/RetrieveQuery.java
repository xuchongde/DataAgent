package com.alibaba.cloud.ai.headless.common.embedding;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RetrieveQuery {

    private List<String> queryTextsList;

    private Map<String, Object> filterCondition;

    private List<List<Double>> queryEmbeddings;
}
