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
