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
