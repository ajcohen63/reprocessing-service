package com.ktc.integration.kwarranty.reprocessingservice.controller;

import com.ktc.integration.kwarranty.reprocessingservice.dto.ApiResponse;
import com.ktc.integration.kwarranty.reprocessingservice.service.ReprocessingService;
import com.ktc.integration.kwarranty.reprocessingservice.dto.RequestStatusHistory;
import com.ktc.integration.kwarranty.reprocessingservice.dto.SearchParams;
import com.ktc.integration.kwarranty.reprocessingservice.dto.SearchResults;
import com.ktc.integration.kwarranty.reprocessingservice.entity.RequestStatus;
import com.ktc.integration.kwarranty.reprocessingservice.service.ReprocessingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ReprocessingController {

    private static final Logger log = LogManager.getLogger(ReprocessingController.class);
    @Autowired
    ReprocessingService reprocessingService;

    @RequestMapping(value = "/reprocess",method = RequestMethod.POST, produces = "text/plain")
    public ApiResponse iDocReprocess(@RequestBody String[] messageList){
        ApiResponse resp =null;
        try {
            log.info("Inside reprocessing messages ::" + messageList);

            for(String msg: messageList) {
                try {
                    reprocessingService.reprocesByRowId(msg);
                }catch (Exception e){
                    log.trace(" Failed to reprocess mongoDB ID :" +  msg);
                }

            }
            resp = new ApiResponse();
            resp.setStatus("Success");
            resp.setMessage("Idoc has been sent to KAFKA for reprocessing.");

        }catch(Exception e){
            log.error("Error occured while sending iDoc for reprocessing" +e.getMessage());
        }
        return resp;
    }
/*
    @RequestMapping(value = "/request/{requestid}", method = RequestMethod.GET, produces = "application/json")
    public RequestStatusHistory getByID(@PathVariable("requestid") String id) {
        log.info("Received Request to get  : " + id);
        try {
            return  activityService.findByRequestId(id);
        } catch (Exception e) {
            log.info("Error occured in Get pushDealerAccountData controller '{}'" + e.getMessage());
        }
        return null;
    }
*/

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
    public List<SearchResults> searchByFilters(@RequestBody SearchParams searchParams) {
        log.info("Request to search records from the database");
        try {
            return reprocessingService.searchByFilters(searchParams);
        } catch (Exception e) {
            log.info("Error occured while fetching the records from database '{}'" + e.getMessage());
        }
        return null;
    }
/*
    @RequestMapping(value = "checkstatus", method = RequestMethod.POST, produces = "application/json")
    public String RequestStatus(@RequestBody RequestStatusHistory requestStatusHistory) {

        try {
            RequestStatus rStatus = activityService.findByRequestIdRequestKeyAndOriginatedSystem( requestStatusHistory.getRequestId(),requestStatusHistory.getRequestKey(), requestStatusHistory.getOriginatedSystem());
            if(rStatus!=null) {
                return rStatus.getStatus();
            }
        } catch (Exception e) {
            log.info("Error occurred while checking the status :" + e.getMessage());
        }
        return null;
    }
*/
   // @Scheduled(cron = "0/15 * * * * ?")
@Scheduled(cron = "0 0/15 * ? * *")
    public void autoReprocess() {

        try {
            reprocessingService.autoReprocess();
        } catch (Exception e) {
            log.info("Error occured in autoReprocess controller '{}'" + e.getMessage());
        }
    }
}