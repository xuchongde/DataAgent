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
package com.alibaba.cloud.ai.headless.common.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageBaseReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Integer MAX_PAGESIZE = 100;
    private Integer current = 1;
    private Integer pageSize = 10;
    private String sort = "desc";
    private String orderCondition;

    public Integer getLimitStart() {
        return this.pageSize * (this.current - 1);
    }

    public Integer getLimitSize() {
        return this.pageSize;
    }
}
