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
package com.alibaba.cloud.ai.dataagent.workflow.tools;

import com.alibaba.cloud.ai.graph.OverAllState;

public interface DataAgentExtTool {
    /**
     * 调用额外方法，返回值，写入OverAllState中，key TOOL_FIELD_VALUE_MAPPING，为后续节点使用
     * @param state
     * @param userInput
     * @return
     */
    ToolDataResponse toolData(OverAllState state,String userInput);

    /**
     * 判断是否需要执行toolData方法，true情况下才调用
     * @param state
     * @param userInput
     * @return true，需要
     */
    boolean needExecute(OverAllState state,String userInput);
}
