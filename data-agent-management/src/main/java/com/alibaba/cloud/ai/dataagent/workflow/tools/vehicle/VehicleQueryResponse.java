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
package com.alibaba.cloud.ai.dataagent.workflow.tools.vehicle;

import java.util.Map;

public class VehicleQueryResponse {
    private Map<String, Object> criteria;
    private String[] ambiguity;
    private String error;

    public VehicleQueryResponse() {
    }

    public VehicleQueryResponse(Map<String, Object> criteria, String[] ambiguity, String error) {
        this.criteria = criteria;
        this.ambiguity = ambiguity;
        this.error = error;
    }

    public Map<String, Object> getCriteria() {
        return criteria;
    }

    public void setCriteria(Map<String, Object> criteria) {
        this.criteria = criteria;
    }

    public String[] getAmbiguity() {
        return ambiguity;
    }

    public void setAmbiguity(String[] ambiguity) {
        this.ambiguity = ambiguity;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
