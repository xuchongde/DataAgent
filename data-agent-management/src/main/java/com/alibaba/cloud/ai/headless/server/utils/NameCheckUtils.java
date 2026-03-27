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
package com.alibaba.cloud.ai.headless.server.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameCheckUtils {

    public static final String forbiddenCharactersRegex = "[（）%#()]";
    public static final String identifierRegex = "^[_a-zA-Z0-9]+$";

    public static String findForbiddenCharacters(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        Pattern pattern = Pattern.compile(forbiddenCharactersRegex);
        Matcher matcher = pattern.matcher(str);

        StringBuilder foundCharacters = new StringBuilder();
        while (matcher.find()) {
            foundCharacters.append(matcher.group()).append(" ");
        }
        return foundCharacters.toString().trim();
    }

    public static Boolean isValidIdentifier(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile(identifierRegex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
