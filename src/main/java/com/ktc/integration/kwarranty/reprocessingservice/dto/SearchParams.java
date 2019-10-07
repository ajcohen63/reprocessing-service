package com.ktc.integration.kwarranty.reprocessingservice.dto;

import java.io.Serializable;
import java.util.Date;

public class SearchParams implements Serializable {
    private Date requestReceivedFrom;
    private Date requestRecivedTo;
    private String requestOrginatedFrom;
    private String requestDestinationSystem;
    private String status;
    private String requestId;
    private String requestKey;
    private String interfaceName;

    public Date getRequestReceivedFrom() {
        return requestReceivedFrom;
    }

    public void setRequestReceivedFrom(Date requestReceivedFrom) {
        this.requestReceivedFrom = requestReceivedFrom;
    }

    public Date getRequestRecivedTo() {
        return requestRecivedTo;
    }

    public void setRequestRecivedTo(Date requestRecivedTo) {
        this.requestRecivedTo = requestRecivedTo;
    }

    public String getRequestOrginatedFrom() {
        return requestOrginatedFrom;
    }

    public void setRequestOrginatedFrom(String requestOrginatedFrom) {
        this.requestOrginatedFrom = requestOrginatedFrom;
    }

    public String getRequestDestinationSystem() {
        return requestDestinationSystem;
    }

    public void setRequestDestinationSystem(String requestDestinationSystem) {
        this.requestDestinationSystem = requestDestinationSystem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
