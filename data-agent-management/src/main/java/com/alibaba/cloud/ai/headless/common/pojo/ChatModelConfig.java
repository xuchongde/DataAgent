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

import com.alibaba.cloud.ai.headless.common.util.AESEncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatModelConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private String provider;
    private String baseUrl;
    private String apiKey;
    private String modelName;
    private String apiVersion;
    private Double temperature = 0.0d;
    private Long timeOut = 60L;
    private String endpoint;
    private String secretKey;
    private Double topP;
    private Integer maxRetries = 3;
    private Boolean logRequests = false;
    private Boolean logResponses = false;
    private Boolean enableSearch = false;
    private Boolean jsonFormat = false;
    private String jsonFormatType = "json_schema";

    public String keyDecrypt() {
        return AESEncryptionUtil.aesDecryptECB(getApiKey());
    }
}
