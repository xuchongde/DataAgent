package com.alibaba.cloud.ai.headless.auth.api.authorization.request;

import lombok.Data;

import java.util.List;

@Data
public class RemoveGroupReq {

    private List<Integer> groupIds;
}
