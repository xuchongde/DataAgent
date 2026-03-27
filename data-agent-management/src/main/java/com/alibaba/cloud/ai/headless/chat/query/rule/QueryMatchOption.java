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
package com.alibaba.cloud.ai.headless.chat.query.rule;

import lombok.Data;

@Data
public class QueryMatchOption {

    private OptionType schemaElementOption;
    private RequireNumberType requireNumberType;
    private Integer requireNumber;

    public static QueryMatchOption build(OptionType schemaElementOption,
            RequireNumberType requireNumberType, Integer requireNumber) {
        QueryMatchOption queryMatchOption = new QueryMatchOption();
        queryMatchOption.requireNumber = requireNumber;
        queryMatchOption.requireNumberType = requireNumberType;
        queryMatchOption.schemaElementOption = schemaElementOption;
        return queryMatchOption;
    }

    public static QueryMatchOption optional() {
        QueryMatchOption queryMatchOption = new QueryMatchOption();
        queryMatchOption.setSchemaElementOption(OptionType.OPTIONAL);
        queryMatchOption.setRequireNumber(0);
        queryMatchOption.setRequireNumberType(RequireNumberType.AT_LEAST);
        return queryMatchOption;
    }

    public static QueryMatchOption unused() {
        QueryMatchOption queryMatchOption = new QueryMatchOption();
        queryMatchOption.setSchemaElementOption(OptionType.UNUSED);
        queryMatchOption.setRequireNumber(0);
        queryMatchOption.setRequireNumberType(RequireNumberType.EQUAL);
        return queryMatchOption;
    }

    public enum RequireNumberType {
        AT_MOST, AT_LEAST, EQUAL
    }

    public enum OptionType {
        REQUIRED, OPTIONAL, UNUSED
    }
}
