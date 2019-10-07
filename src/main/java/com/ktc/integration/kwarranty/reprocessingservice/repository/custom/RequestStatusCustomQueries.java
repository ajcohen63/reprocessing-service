package com.ktc.integration.kwarranty.reprocessingservice.repository.custom;

import com.ktc.integration.kwarranty.reprocessingservice.dto.SearchParams;
import com.ktc.integration.kwarranty.reprocessingservice.entity.RequestStatus;

import java.util.Date;
import java.util.List;

public interface RequestStatusCustomQueries {
    RequestStatus findByRequestIdRequestKeyAndOriginatedSystem(String requestId,String requestKey,String OriginatedSystem);
    List<RequestStatus> findByStatus(String completed, String failed);
    List<RequestStatus> searchByOriginationSystemAndStatusIn(String orginationSystem, List<String> status);
    List<RequestStatus> searchByFilters(SearchParams searchParams);
    List<RequestStatus> findByRowId(String Id);
    List<RequestStatus> findFailedMessagesByDate(Date dt);
}
