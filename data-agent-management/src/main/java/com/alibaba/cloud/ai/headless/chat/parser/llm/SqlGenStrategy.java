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
package com.alibaba.cloud.ai.headless.chat.parser.llm;

import com.alibaba.cloud.ai.headless.chat.query.llm.s2sql.LLMReq;
import com.alibaba.cloud.ai.headless.chat.query.llm.s2sql.LLMResp;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SqlGenStrategy abstracts generation step so that different LLM prompting strategies can be
 * implemented.
 */
@Service
public abstract class SqlGenStrategy implements InitializingBean {

    @Autowired
    protected PromptHelper promptHelper;

 /*   protected ChatLanguageModel getChatLanguageModel(ChatModelConfig modelConfig) {
        return ModelProvider.getChatModel(modelConfig);
    }*/

    public abstract LLMResp generate(LLMReq llmReq);
}
