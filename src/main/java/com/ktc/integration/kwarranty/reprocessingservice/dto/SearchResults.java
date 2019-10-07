package com.ktc.integration.kwarranty.reprocessingservice.dto;

import java.io.Serializable;

public class SearchResults implements Serializable {
    private String rowId;
    private String requestDate;
    private String requestId;
    private String requestKey;
    private String orginatedFrom;
    private String destinationSystem;
    private String requestStatus;
    private String orginatedSystemRequest;
    private String destinationSystemRequest;
    private String response;
    private String interfaceName;
    private String targetSystemResponseStatus;


    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getOrginatedFrom() {
        return orginatedFrom;
    }

    public void setOrginatedFrom(String orginatedFrom) {
        this.orginatedFrom = orginatedFrom;
    }

    public String getDestinationSystem() {
        return destinationSystem;
    }

    public void setDestinationSystem(String destinationSystem) {
        this.destinationSystem = destinationSystem;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getOrginatedSystemRequest() {
        return orginatedSystemRequest;
    }

    public void setOrginatedSystemRequest(String orginatedSystemRequest) {
        this.orginatedSystemRequest = orginatedSystemRequest;
    }

    public String getDestinationSystemRequest() {
        return destinationSystemRequest;
    }

    public void setDestinationSystemRequest(String destinationSystemRequest) {
        this.destinationSystemRequest = destinationSystemRequest;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getTargetSystemResponseStatus() {
        return targetSystemResponseStatus;
    }

    public void setTargetSystemResponseStatus(String targetSystemResponseStatus) {
        this.targetSystemResponseStatus = targetSystemResponseStatus;
    }
}
