package com.alibaba.cloud.ai.headless.auth.api.authorization.request;

import com.alibaba.cloud.ai.headless.common.pojo.PageBaseReq;
import lombok.Data;

import java.util.List;

@Data
public class QueryGroupReq extends PageBaseReq {

    private List<Integer> groupIds;
    private List<String> users;
}
