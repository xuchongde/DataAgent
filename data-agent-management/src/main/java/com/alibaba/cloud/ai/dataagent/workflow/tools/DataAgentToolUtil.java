package com.alibaba.cloud.ai.dataagent.workflow.tools;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataAgentToolUtil {

    public static String toolDataMsg(List<ToolDataResponse> list){
        if(null==list || list.isEmpty()){
            return "(无)";
        }
        StringBuilder stringBuilder = new StringBuilder("");
        for(ToolDataResponse tool : list){
            if(Objects.isNull(tool)){
                continue;
            }
            Map<String, Object> values = tool.getToolReturnData();
            if(null != values && !values.isEmpty()){
                values.keySet().forEach(key->{
                    stringBuilder.append(key).append("=").append(values.get(key)).append("\n");
                });
            }
        }
        if(stringBuilder.length()==0){
            stringBuilder.append("(无)");
        }
        return stringBuilder.toString();
    }
}
