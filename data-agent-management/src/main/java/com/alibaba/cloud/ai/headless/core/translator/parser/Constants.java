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
package com.alibaba.cloud.ai.headless.core.translator.parser;

public class Constants {

    public static final String DIMENSION_IDENTIFY = "__";
    public static final String DATASOURCE_TABLE_PREFIX = "src0_";
    public static final String DATASOURCE_TABLE_FILTER_PREFIX = "src2_";
    public static final String DATASOURCE_TABLE_OUT_PREFIX = "src00_";
    public static final String JOIN_TABLE_PREFIX = "src1_";
    public static final String JOIN_TABLE_OUT_PREFIX = "src11_";
    public static final String JOIN_TABLE_LEFT_PREFIX = "src12_";
    public static final String DIMENSION_TYPE_TIME_GRANULARITY_NONE = "none";
    public static final String DIMENSION_TYPE_TIME = "time";
    public static final String DIMENSION_ARRAY_SINGLE_SUFFIX = "_sgl";
    public static final String MATERIALIZATION_ZIPPER_START = "start_";
    public static final String MATERIALIZATION_ZIPPER_END = "end_";
    public static final String SQL_PARSER_TABLE = "parsed_tb";
    public static final String SQL_PARSER_DB = "parsed_db";
    public static final String SQL_PARSER_FIELD = "parsed_field";
    public static final String DIMENSION_DELIMITER = "dimension_delimiter";
}
