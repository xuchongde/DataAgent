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
package com.alibaba.cloud.ai.headless.core.translator;

import com.google.common.collect.Lists;
import com.alibaba.cloud.ai.headless.common.config.ParameterConfig;
import com.alibaba.cloud.ai.headless.common.pojo.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("HeadlessTranslatorConfig")
@Slf4j
public class TranslatorConfig extends ParameterConfig {

    public static final Parameter TRANSLATOR_RESULT_LIMIT =
            new Parameter("s2.query-optimizer.resultLimit", "1000", "查询最大返回数据行数",
                    "为了前端展示性能考虑，请不要设置过大", "number", "语义翻译配置");

    @Override
    public List<Parameter> getSysParameters() {
        return Lists.newArrayList(TRANSLATOR_RESULT_LIMIT);
    }

}
