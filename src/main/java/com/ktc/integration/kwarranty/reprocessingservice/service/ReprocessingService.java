package com.ktc.integration.kwarranty.reprocessingservice.service;

import com.ktc.integration.kwarranty.reprocessingservice.dto.RequestStatusHistory;
import com.ktc.integration.kwarranty.reprocessingservice.dto.SearchParams;
import com.ktc.integration.kwarranty.reprocessingservice.dto.SearchResults;
import com.ktc.integration.kwarranty.reprocessingservice.entity.RequestStatus;

import java.util.List;

public interface ReprocessingService {

    public void reprocessIDocToKafka(String iDocs);

    public void reprocesByRowId(String rowId) throws Exception;

    public void autoReprocess() throws Exception;

    List<SearchResults> searchByFilters(SearchParams searchParams) throws Exception;


}