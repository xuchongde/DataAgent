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

public class EditDistanceUtils {

    public static double getSimilarity(String word1, String word2) {
        return 1 - (double) editDistance(word1, word2) / Math.max(word2.length(), word1.length());
    }

    public static int editDistance(String word1, String word2) {
        final int m = word1.length();
        final int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int j = 0; j <= n; ++j) {
            dp[0][j] = j;
        }
        for (int i = 0; i <= m; ++i) {
            dp[i][0] = i;
        }

        for (int i = 1; i <= m; ++i) {
            char ci = word1.charAt(i - 1);
            for (int j = 1; j <= n; ++j) {
                char cj = word2.charAt(j - 1);
                if (ci == cj) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (i > 1 && j > 1 && ci == word2.charAt(j - 2)
                        && cj == word1.charAt(i - 2)) {
                    dp[i][j] = 1 + Math.min(dp[i - 2][j - 2], Math.min(dp[i][j - 1], dp[i - 1][j]));
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + 1,
                            Math.min(dp[i][j - 1] + 1, dp[i - 1][j] + 1));
                }
            }
        }
        return dp[m][n];
    }
}
