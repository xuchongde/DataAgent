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
package com.alibaba.cloud.ai.headless.server.persistence.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("s2_query_rule")
public class QueryRuleDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** dataSetID */
    private Long dataSetId;

    /** 规则的优先级, 0-系统默认规则 */
    private Integer priority;

    /** 规则类型 */
    private String ruleType;

    /** 规则名称 */
    private String name;

    /** 规则业务名称 */
    private String bizName;

    /** 描述 */
    private String description;

    /** 具体规则信息 */
    private String rule;

    /** 规则输出信息 */
    private String action;

    /** 状态,0-正常,1-下线,2-删除 */
    private Integer status;

    /** 创建时间 */
    private Date createdAt;

    /** 创建人 */
    private String createdBy;

    /** 更新时间 */
    private Date updatedAt;

    /** 更新人 */
    private String updatedBy;

    /** 扩展信息 */
    private String ext;
}
