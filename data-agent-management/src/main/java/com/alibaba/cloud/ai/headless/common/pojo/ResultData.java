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
package com.alibaba.cloud.ai.headless.common.pojo;

import com.alibaba.cloud.ai.headless.common.pojo.enums.ReturnCode;
import com.alibaba.cloud.ai.headless.common.util.TraceIdUtil;
import lombok.Data;
import org.slf4j.MDC;

/** * result data */
@Data
public class ResultData<T> {
    private int code;
    private String msg;
    private T data;
    private long timestamp;
    private String traceId;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.SUCCESS.getCode());
        resultData.setMsg(ReturnCode.SUCCESS.getMessage());
        resultData.setData(data);
        resultData.setTraceId(MDC.get(TraceIdUtil.TRACE_ID));
        return resultData;
    }

    public static <T> ResultData<T> fail(int code, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(code);
        resultData.setMsg(message);
        resultData.setTraceId(MDC.get(TraceIdUtil.TRACE_ID));
        return resultData;
    }
}
