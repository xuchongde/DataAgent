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
package com.alibaba.cloud.ai.headless.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class RetryUtils {

    private static final int RETRY_NUM = 3;

    public static <T> T exec(Supplier<T> supplier) {
        return exec(supplier, RETRY_NUM);
    }

    public static <T> T exec(Supplier<T> supplier, int retryNum) {
        T result = null;
        for (int index = 1; index <= retryNum; index++) {
            try {
                result = supplier.get();
            } catch (Exception ex) {
                if (index < retryNum) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        log.error("e", e);
                    }
                    log.warn("Retry exec {}, {}", index, ex.getMessage());
                    continue;
                }
                log.warn("Retry {} times all fail, err: {}", retryNum, ex.getMessage());
                throw ex;
            }
            break;
        }

        return result;
    }
}
