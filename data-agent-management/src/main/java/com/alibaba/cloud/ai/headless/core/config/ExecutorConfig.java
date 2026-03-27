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
package com.alibaba.cloud.ai.headless.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ExecutorConfig {

    @Value("${s2.metricParser.agg.mysql.lowVersion:8.0}")
    private String mysqlLowVersion;

    @Value("${s2.metricParser.agg.ck.lowVersion:20.4}")
    private String ckLowVersion;

    @Value("${s2.internal.metric.cnt.suffix:internal_cnt}")
    private String internalMetricNameSuffix;

    @Value("${s2.accelerator.duckDb.enable:false}")
    private Boolean duckEnable = false;

    @Value("${s2.accelerator.duckDb.temp:/data1/duck/tmp/}")
    private String duckDbTemp;

    @Value("${s2.accelerator.duckDb.maximumPoolSize:10}")
    private Integer duckDbMaximumPoolSize;

    @Value("${s2.accelerator.duckDb.MaxLifetime:3}")
    private Integer duckDbMaxLifetime;

    @Value("${s2.accelerator.duckDb.memoryLimit:31}")
    private Integer memoryLimit;

    @Value("${s2.accelerator.duckDb.threads:32}")
    private Integer threads;
}
