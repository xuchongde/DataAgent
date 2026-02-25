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
