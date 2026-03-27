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
package com.alibaba.cloud.ai.headless.common.jsqlparser;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

@Slf4j
public class FunctionNameReplaceVisitor extends ExpressionVisitorAdapter {

    private Map<String, String> functionMap;
    private Map<String, UnaryOperator> functionCallMap;

    public FunctionNameReplaceVisitor(Map<String, String> functionMap,
            Map<String, UnaryOperator> functionCallMap) {
        this.functionMap = functionMap;
        this.functionCallMap = functionCallMap;
    }

    public void visit(Function function) {
        String functionName = function.getName().toLowerCase();
        String replaceFunctionName = functionMap.get(functionName);
        if (StringUtils.isNotBlank(replaceFunctionName)) {
            function.setName(replaceFunctionName);
            if (Objects.nonNull(functionCallMap) && functionCallMap.containsKey(functionName)) {
                Object ret = functionCallMap.get(functionName).apply(function.getParameters());
                if (Objects.nonNull(ret) && ret instanceof ExpressionList) {
                    ExpressionList expressionList = (ExpressionList) ret;
                    function.setParameters(expressionList);
                }
            }
        }
    }
}
