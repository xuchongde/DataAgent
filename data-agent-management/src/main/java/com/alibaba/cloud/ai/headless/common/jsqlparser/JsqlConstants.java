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

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class JsqlConstants {

    public static final String DATE_FUNCTION = "datediff";
    public static final double HALF_YEAR = 0.5d;
    public static final int SIX_MONTH = 6;
    public static final String EQUAL = "=";
    public static final String MINOR_THAN = "<";
    public static final String MINOR_THAN_EQUALS = "<=";
    public static final String GREATER_THAN = ">";
    public static final String GREATER_THAN_EQUALS = ">=";
    public static final String MINOR_THAN_CONSTANT = " 1 < 2 ";
    public static final String MINOR_THAN_EQUALS_CONSTANT = " 1 <= 1 ";
    public static final String GREATER_THAN_CONSTANT = " 2 > 1 ";
    public static final String GREATER_THAN_EQUALS_CONSTANT = " 1 >= 1 ";
    public static final String EQUAL_CONSTANT = " 1 = 1 ";
    public static final String IN_CONSTANT = " 1 in (1) ";
    public static final String LIKE_CONSTANT = "1 like 1";
    public static final String BETWEEN_AND_CONSTANT = "1 between 2 and 3";
    public static final String IN = "IN";
    public static final Map<String, String> rightMap = Stream.of(
            new AbstractMap.SimpleEntry<>("<=", "<="), new AbstractMap.SimpleEntry<>("<", "<"),
            new AbstractMap.SimpleEntry<>(">=", "<="), new AbstractMap.SimpleEntry<>(">", "<"),
            new AbstractMap.SimpleEntry<>("=", "<="))
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    public static final Map<String, String> leftMap = Stream.of(
            new AbstractMap.SimpleEntry<>("<=", ">="), new AbstractMap.SimpleEntry<>("<", ">"),
            new AbstractMap.SimpleEntry<>(">=", "<="), new AbstractMap.SimpleEntry<>(">", "<"),
            new AbstractMap.SimpleEntry<>("=", ">="))
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
}
