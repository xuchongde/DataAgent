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
package com.alibaba.cloud.ai.headless.chat.query;

import com.alibaba.cloud.ai.headless.api.pojo.DataSetSchema;
import com.alibaba.cloud.ai.headless.api.pojo.SemanticParseInfo;
import com.alibaba.cloud.ai.headless.api.pojo.request.SemanticQueryReq;
import org.apache.calcite.sql.parser.SqlParseException;

/** A semantic query executes specific type of query based on the results of semantic parsing. */
public interface SemanticQuery {

    String getQueryMode();

    SemanticQueryReq buildSemanticQueryReq() throws SqlParseException;

    void buildS2Sql(DataSetSchema dataSetSchema);

    SemanticParseInfo getParseInfo();

    void setParseInfo(SemanticParseInfo parseInfo);
}
