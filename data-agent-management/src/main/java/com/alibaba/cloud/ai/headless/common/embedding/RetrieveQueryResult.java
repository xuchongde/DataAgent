package com.alibaba.cloud.ai.headless.common.embedding;

import lombok.Data;

import java.util.List;

@Data
public class RetrieveQueryResult {

    private String query;

    private List<Retrieval> retrieval;
}
