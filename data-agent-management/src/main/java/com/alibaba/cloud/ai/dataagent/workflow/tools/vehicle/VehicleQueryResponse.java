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
