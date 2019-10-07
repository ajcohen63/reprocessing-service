package com.ktc.integration.kwarranty.reprocessingservice.dto;

import java.io.Serializable;

public class ApiResponse implements Serializable {
    String status;
    String message;
    String requestId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
