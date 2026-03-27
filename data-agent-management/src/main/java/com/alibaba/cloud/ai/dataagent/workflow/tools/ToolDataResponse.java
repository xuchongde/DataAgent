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

import java.util.Map;
public class ToolDataResponse {
    //返回的数据
    private Map<String,Object> toolReturnData;
    private String toolError;
    private String toolName;

    public ToolDataResponse() {
    }

    public ToolDataResponse(Map<String, Object> toolReturnData, String toolError, String toolName) {
        this.toolReturnData = toolReturnData;
        this.toolError = toolError;
        this.toolName = toolName;
    }

    public Map<String, Object> getToolReturnData() {
        return toolReturnData;
    }

    public void setToolReturnData(Map<String, Object> toolReturnData) {
        this.toolReturnData = toolReturnData;
    }

    public String getToolError() {
        return toolError;
    }

    public void setToolError(String toolError) {
        this.toolError = toolError;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String toolMsgShow(){
        StringBuilder stringBuilder = new StringBuilder(this.toolName);
        stringBuilder.append(",");
        if(this.toolReturnData!=null && !this.toolReturnData.isEmpty()){
            stringBuilder.append("返回值:");
            this.toolReturnData.keySet().forEach(key->{
                stringBuilder.append(key).append("=").append(this.toolReturnData.get(key)).append(",");
            });
        }
        return stringBuilder.toString();
    }
}
