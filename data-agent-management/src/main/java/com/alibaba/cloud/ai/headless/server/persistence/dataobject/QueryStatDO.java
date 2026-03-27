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
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("s2_query_stat_info")
public class QueryStatDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String traceId;
    private Long modelId;
    private Long dataSetId;
    private String queryUser;
    private String createdAt;
    /** corresponding type, such as sql, struct, etc */
    private String queryType;
    /** NORMAL, PRE_FLUSH */
    private Integer queryTypeBack;
    private String querySqlCmd;
    @TableField("sql_cmd_md5")
    private String querySqlCmdMd5;
    private String queryStructCmd;
    @TableField("struct_cmd_md5")
    private String queryStructCmdMd5;
    private String querySql;
    private String sqlMd5;
    private String queryEngine;
    // private Long startTime;
    private Long elapsedMs;
    private String queryState;
    private Boolean nativeQuery;
    private String startDate;
    private String endDate;
    private String dimensions;
    private String metrics;
    private String selectCols;
    private String aggCols;
    private String filterCols;
    private String groupByCols;
    private String orderByCols;
    private Boolean useResultCache;
    private Boolean useSqlCache;
    private String sqlCacheKey;
    private String resultCacheKey;
    private String queryOptMode;
}
