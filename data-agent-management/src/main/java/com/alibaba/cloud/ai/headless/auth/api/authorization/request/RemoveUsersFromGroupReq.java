package com.alibaba.cloud.ai.headless.auth.api.authorization.request;

import lombok.Data;

import java.util.List;

@Data
public class RemoveUsersFromGroupReq {

    private Integer groupId;
    private List<String> users;
}
