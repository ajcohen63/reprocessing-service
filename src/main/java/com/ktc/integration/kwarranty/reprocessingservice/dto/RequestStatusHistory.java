package com.ktc.integration.kwarranty.reprocessingservice.dto;

import java.io.Serializable;
import java.util.Date;

public class RequestStatusHistory implements Serializable {

    private Integer sequenceNumber;
    private String requestId;
    private String requestKey;
    private String originatedSystem;
    private String targetSystem;
    private String srvcNameToSendResponse;
    private String originatedSystemRequest;
    private String targetSystemRequest;
    private String targetSystemResponse;
    private String targetSystemResponseStatus;
    private String status;
    private String message;
    private Integer totalCount;
    private Integer sucessCount;
    private Integer failedCount;
    private Date createdOn;
    private Date updatedOn;

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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

    public String getOriginatedSystem() {
        return originatedSystem;
    }

    public void setOriginatedSystem(String originatedSystem) {
        this.originatedSystem = originatedSystem;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    public String getSrvcNameToSendResponse() {
        return srvcNameToSendResponse;
    }

    public void setSrvcNameToSendResponse(String srvcNameToSendResponse) {
        this.srvcNameToSendResponse = srvcNameToSendResponse;
    }

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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSucessCount() {
        return sucessCount;
    }

    public void setSucessCount(Integer sucessCount) {
        this.sucessCount = sucessCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getOriginatedSystemRequest() {
        return originatedSystemRequest;
    }

    public void setOriginatedSystemRequest(String originatedSystemRequest) {
        this.originatedSystemRequest = originatedSystemRequest;
    }

    public String getTargetSystemResponse() {
        return targetSystemResponse;
    }

    public void setTargetSystemResponse(String targetSystemResponse) {
        this.targetSystemResponse = targetSystemResponse;
    }

    public String getTargetSystemRequest() {
        return targetSystemRequest;
    }

    public void setTargetSystemRequest(String targetSystemRequest) {
        this.targetSystemRequest = targetSystemRequest;
    }

    public String getTargetSystemResponseStatus() {
        return targetSystemResponseStatus;
    }

    public void setTargetSystemResponseStatus(String targetSystemResponseStatus) {
        this.targetSystemResponseStatus = targetSystemResponseStatus;
    }

    @Override
    public String toString() {
        return "RequestStatusHistory{" +
                "sequenceNumber=" + sequenceNumber +
                ", requestId='" + requestId + '\'' +
                ", requestKey='" + requestKey + '\'' +
                ", originatedSystem='" + originatedSystem + '\'' +
                ", targetSystem='" + targetSystem + '\'' +
                ", srvcNameToSendResponse='" + srvcNameToSendResponse + '\'' +
                ", originatedSystemRequest='" + originatedSystemRequest + '\'' +
                ", targetSystemRequest='" + targetSystemRequest + '\'' +
                ", targetSystemResponse='" + targetSystemResponse + '\'' +
                ", targetSystemResponseStatus='" + targetSystemResponseStatus + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", totalCount=" + totalCount +
                ", sucessCount=" + sucessCount +
                ", failedCount=" + failedCount +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}
