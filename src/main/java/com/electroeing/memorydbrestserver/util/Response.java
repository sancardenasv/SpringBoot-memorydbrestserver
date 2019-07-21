package com.electroeing.memorydbrestserver.util;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private boolean status;
    private String reason;
    private Map<String, Object> body;

    public Response(boolean status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public void addBodyObject(String key, Object value) {
        if (body == null) {
            body = new HashMap<>();
        }
        body.put(key, value);
    }

    @Override
    public String toString() {
        return new StringBuilder("Response{")
        .append("status=").append(status)
        .append(", reason='").append(reason).append("'")
        .append(", body=").append(body)
        .append('}').toString();
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }
}
