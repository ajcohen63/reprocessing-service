package com.ktc.integration.kwarranty.reprocessingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;


@CompoundIndexes({
        @CompoundIndex(name = "request_index_name",
                unique = true,
                def = "{'requestId' : 1, 'requestKey' : 1, 'originatedSystem' : 1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "REQUEST_STATUS")
public class RequestStatus implements Serializable {

    @Id
    private String _id;
    private String requestId;
    private String requestKey;
    private String originatedSystem;
    private String targetSystem;
    private String originatedSystemRequest;
    private String targetSystemRequest;
    private String targetSystemResponse;
    private String srvcNameToSendResponse;
    private String status;
    private String message;
    private Integer totalCount;
    private Integer sucessCount;
    private Integer failedCount;
    private String targetSystemResponseStatus;
    @Field
    private Date createdOn = new Date();
    @Field
    private Date updatedOn = new Date();

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getTargetSystemRequest() {
        return targetSystemRequest;
    }

    public void setTargetSystemRequest(String targetSystemRequest) {
        this.targetSystemRequest = targetSystemRequest;
    }

    public String getTargetSystemResponse() {
        return targetSystemResponse;
    }

    public void setTargetSystemResponse(String targetSystemResponse) {
        this.targetSystemResponse = targetSystemResponse;
    }

    public String getTargetSystemResponseStatus() {
        return targetSystemResponseStatus;
    }

    public void setTargetSystemResponseStatus(String targetSystemResponseStatus) {
        this.targetSystemResponseStatus = targetSystemResponseStatus;
    }

    @Override
    public String toString() {
        return "RequestStatus{" +
                "_id=" + _id +
                ", requestId='" + requestId + '\'' +
                ", requestKey='" + requestKey + '\'' +
                ", originatedSystem='" + originatedSystem + '\'' +
                ", targetSystem='" + targetSystem + '\'' +
                ", originatedSystemRequest='" + originatedSystemRequest + '\'' +
                ", targetSystemRequest='" + targetSystemRequest + '\'' +
                ", targetSystemResponse='" + targetSystemResponse + '\'' +
                ", srvcNameToSendResponse='" + srvcNameToSendResponse + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", totalCount=" + totalCount +
                ", sucessCount=" + sucessCount +
                ", failedCount=" + failedCount +
                ", targetSystemResponseStatus='" + targetSystemResponseStatus + '\'' +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}

