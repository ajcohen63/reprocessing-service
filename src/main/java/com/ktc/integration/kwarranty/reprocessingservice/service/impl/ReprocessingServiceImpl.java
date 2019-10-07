package com.ktc.integration.kwarranty.reprocessingservice.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ktc.integration.kwarranty.reprocessingservice.repository.RequestStatusRepository;
import com.ktc.integration.kwarranty.reprocessingservice.service.ReprocessingService;
import com.ktc.integration.kwarranty.reprocessingservice.dto.RequestStatusHistory;
import com.ktc.integration.kwarranty.reprocessingservice.dto.SearchParams;
import com.ktc.integration.kwarranty.reprocessingservice.dto.SearchResults;
import com.ktc.integration.kwarranty.reprocessingservice.entity.RequestStatus;

import com.ktc.integration.kwarranty.reprocessingservice.utils.GlobalConstants;
import com.ktc.integration.kwarranty.reprocessingservice.utils.KafkaProducerProperties;
import com.ktc.integration.twod.integrationutilities.utils.Util;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ReprocessingServiceImpl implements ReprocessingService {

    @Autowired
    private KafkaProducerProperties producerProps;

    @Autowired
    RequestStatusRepository requestStatusRepository;

    @Value("${sap.mw.requeststatus.endpoint.uri}")
    String sendSapStatusUri;
    @Value("${bw.mw.requeststatus.endpoint.uri}")
    String sendBwStatusUri;

    @Value("${pickup.failed.messages.days}")
    int priorDays;

    @Value("${request.status.update.batch.size}")
    int batchSize;

    @Value("${mw.kwarranty.api.timeout.seconds}")
    int timeoutSeconds;

    @Value("${bw.mw.requeststatus.endpoint.uri}")
    String mwStatusURI;

    private static final Logger log = LogManager.getLogger(ReprocessingServiceImpl.class);



    @Override
    public List<SearchResults> searchByFilters(SearchParams searchParams) throws Exception {
        try {
            log.info("Fetching all the request status records from database");
            return convertToSearchResultsList(requestStatusRepository.searchByFilters(searchParams));
        } catch (Exception e) {
        log.error("Error occurred :: " + e.getMessage());
    }

        return null;
    }


    @Override
    public void  reprocesByRowId(String rowId) throws Exception {
        try {
            List<RequestStatus> rows = this.requestStatusRepository.findByRowId(rowId);
            if (rows.size()!=0) {
                RequestStatus rs = rows.get(0);
                if(GlobalConstants.SYSTEMS_NAME.SAP.toString().equalsIgnoreCase(rs.getOriginatedSystem())) {
                    reprocessIDocToKafka(rs.getOriginatedSystemRequest());
                }
            }
        } catch (Exception e) {
            log.error("Error occurred in reprocesByRowId:: " + e.getMessage());
        }
    }

    @Override
    public void autoReprocess() throws Exception {

        try {
            Date curDt = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(curDt);
            cal.add(Calendar.DATE, (-1) * priorDays);
            Date cutoffDate=cal.getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String reprocessDt = df.format(cutoffDate);
            List<RequestStatus> rows = this.requestStatusRepository.findFailedMessagesByDate(cutoffDate);
            for(RequestStatus rs: rows) {
                if (GlobalConstants.SYSTEMS_NAME.SAP.toString().equalsIgnoreCase(rs.getOriginatedSystem())) {
                    reprocessIDocToKafka(rs.getOriginatedSystemRequest());
                } else if (GlobalConstants.SYSTEMS_NAME.BW.toString().equalsIgnoreCase(rs.getOriginatedSystem())) {
                } else if (GlobalConstants.SYSTEMS_NAME.TAVANT.toString().equalsIgnoreCase(rs.getOriginatedSystem())) {

                }
            }

        } catch (Exception e) {
            log.error("Error occurred in autoReprocess: " + e.getMessage());
        }
    }


    private Iterable<? extends RequestStatus> convertToEntity(Iterable<? extends RequestStatusHistory> entities) {
        List<RequestStatus> listStatus = new ArrayList<>();
        List<RequestStatusHistory> list = new ArrayList<>();
        entities.forEach(list::add);
        Iterator<RequestStatusHistory> itr = list.iterator();
        while (itr.hasNext()) {
            RequestStatusHistory requestStatusHistory = itr.next();
            RequestStatus requestStatus = new RequestStatus();
            requestStatus.setRequestId(requestStatusHistory.getRequestId());
            requestStatus.setTargetSystemResponseStatus(requestStatusHistory.getStatus());
            requestStatus.setRequestKey(requestStatusHistory.getRequestKey());
            requestStatus.setOriginatedSystem(requestStatusHistory.getOriginatedSystem());
            requestStatus.setSrvcNameToSendResponse(requestStatusHistory.getSrvcNameToSendResponse());
            requestStatus.setOriginatedSystemRequest(requestStatusHistory.getOriginatedSystemRequest());
            requestStatus.setTargetSystemRequest(requestStatusHistory.getTargetSystemRequest());
            requestStatus.setTargetSystemResponse(requestStatusHistory.getTargetSystemResponse());
            requestStatus.setFailedCount(requestStatusHistory.getFailedCount());
            requestStatus.setSucessCount(requestStatusHistory.getSucessCount());
            requestStatus.setTargetSystem(requestStatusHistory.getTargetSystem());
            requestStatus.setMessage(requestStatusHistory.getMessage());
            requestStatus.setStatus(requestStatusHistory.getStatus());
            requestStatus.setTotalCount(requestStatusHistory.getTotalCount());
            listStatus.add(requestStatus);
        }
        return listStatus;
    }

    private RequestStatus convertToRequestStatus(RequestStatusHistory requestStatusHistory) {
        RequestStatus rStatus = new RequestStatus();
        rStatus.setRequestId(requestStatusHistory.getRequestId());
        rStatus.setTargetSystemResponseStatus(requestStatusHistory.getStatus());
        rStatus.setRequestKey(requestStatusHistory.getRequestKey());
        rStatus.setOriginatedSystem(requestStatusHistory.getOriginatedSystem());
        rStatus.setSrvcNameToSendResponse(requestStatusHistory.getSrvcNameToSendResponse());
        rStatus.setCreatedOn(new Date());
        rStatus.setTargetSystem(requestStatusHistory.getTargetSystem());
        rStatus.setOriginatedSystemRequest(requestStatusHistory.getOriginatedSystemRequest());
        rStatus.setTargetSystemRequest(requestStatusHistory.getTargetSystemRequest());
        rStatus.setTargetSystemResponse(requestStatusHistory.getTargetSystemResponse());
        rStatus.setStatus(requestStatusHistory.getStatus());
        rStatus.setMessage(requestStatusHistory.getMessage());
        rStatus.setFailedCount(requestStatusHistory.getFailedCount());
        rStatus.setTotalCount(requestStatusHistory.getTotalCount());
        rStatus.setSucessCount(requestStatusHistory.getSucessCount());
        return rStatus;
    }

    private RequestStatusHistory convertToRequestStatusHistory(RequestStatus requestStatus) {
        RequestStatusHistory requestStatusHistory = new RequestStatusHistory();
        requestStatusHistory.setRequestId(requestStatus.getRequestId());
        requestStatus.setTargetSystemResponseStatus(requestStatus.getStatus());
        requestStatusHistory.setRequestKey(requestStatus.getRequestKey());
        requestStatusHistory.setOriginatedSystem(requestStatus.getOriginatedSystem());
        requestStatusHistory.setSrvcNameToSendResponse(requestStatus.getSrvcNameToSendResponse());
        requestStatusHistory.setOriginatedSystemRequest(requestStatus.getOriginatedSystemRequest());
        requestStatusHistory.setTargetSystemRequest(requestStatus.getTargetSystemRequest());
        requestStatusHistory.setTargetSystemResponse(requestStatus.getTargetSystemResponse());
        requestStatusHistory.setCreatedOn(requestStatus.getCreatedOn());
        requestStatusHistory.setFailedCount(requestStatus.getFailedCount());
        requestStatusHistory.setUpdatedOn(requestStatus.getUpdatedOn());
        requestStatusHistory.setSucessCount(requestStatus.getSucessCount());
        requestStatusHistory.setTargetSystem(requestStatus.getTargetSystem());
        requestStatusHistory.setMessage(requestStatus.getMessage());
        requestStatusHistory.setStatus(requestStatus.getStatus());
        requestStatusHistory.setTotalCount(requestStatus.getTotalCount());
        return requestStatusHistory;
    }

    private RequestStatus convertToEntity(RequestStatusHistory requestStatusHistory) {
        RequestStatus requestStatus = new RequestStatus();
        requestStatus.setRequestKey(requestStatusHistory.getRequestKey());
        requestStatus.setOriginatedSystem(requestStatusHistory.getOriginatedSystem());
        requestStatus.setSrvcNameToSendResponse(requestStatusHistory.getSrvcNameToSendResponse());
        requestStatus.setFailedCount(requestStatusHistory.getFailedCount());
        requestStatus.setTargetSystemResponseStatus(requestStatusHistory.getStatus());
        requestStatus.setSucessCount(requestStatusHistory.getSucessCount());
        requestStatus.setOriginatedSystemRequest(requestStatusHistory.getOriginatedSystemRequest());
        requestStatus.setTargetSystemRequest(requestStatusHistory.getTargetSystemRequest());
        requestStatus.setTargetSystemResponse(requestStatusHistory.getTargetSystemResponse());
        requestStatus.setTargetSystem(requestStatusHistory.getTargetSystem());
        requestStatus.setMessage(requestStatusHistory.getMessage());
        requestStatus.setStatus(requestStatusHistory.getStatus());
        requestStatus.setTotalCount(requestStatusHistory.getTotalCount());
        return requestStatus;
    }

    public String executeHttpClient(String  jsonInput, String endpointUri) {
        String apiResponse="";
        ResponseBody rBody = null;
        OkHttpClient client = null;
        log.trace("Sending notifcation request to Notification service.."+endpointUri);
        try {

            if(timeoutSeconds>0) {
                client = new OkHttpClient().newBuilder()
                        .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(false)
                        .build();
            }else{
                client = new OkHttpClient();
            }
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInput);
            okhttp3.Request postRequest = new okhttp3.Request.Builder()
                    .url(endpointUri)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .build();
            //log.info("Invoking Notification service..");
            long t1 = System.currentTimeMillis();
            Response response = client.newCall(postRequest).execute();
            long t2 = System.currentTimeMillis();
            log.info("Time taken to send update was "+ (t2-t1) +" ms");
            rBody = response.body();
            if (!response.isSuccessful()){
                apiResponse = convert(rBody);
            }

        } catch (Exception e) {
            log.error("Exception in executeHttpClient" + e.getMessage());
        }
        finally {
            rBody.close();
        }
        return apiResponse;
    }
    public String convert(ResponseBody value) throws IOException {

        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        final StringBuilder stringBuilder = new StringBuilder();

        try {
            inputStreamReader = (InputStreamReader) value.charStream();
            bufferedReader = new BufferedReader(inputStreamReader);

            String content = null;
            while ((content = bufferedReader.readLine()) != null) {
                stringBuilder.append(content);
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
        }
        final String json = stringBuilder.toString();
        log.info("Not able to update the status to source system.."+json);
        return json;
    }

    public List<SearchResults> convertToSearchResultsList(List<RequestStatus> requestStatuses){
        log.trace("Results found: "+ requestStatuses.size());
        List<SearchResults> searchResultsList = new ArrayList<SearchResults>();
        for(RequestStatus requestStatus : requestStatuses){
            SearchResults searchResults= new SearchResults();
            searchResults = new SearchResults();
            searchResults.setRowId(requestStatus.get_id());
            searchResults.setRequestId(requestStatus.getRequestId());
            searchResults.setRequestKey(requestStatus.getRequestKey());
            searchResults.setOrginatedFrom(requestStatus.getOriginatedSystem());
            searchResults.setDestinationSystem(requestStatus.getTargetSystem());
            searchResults.setInterfaceName(requestStatus.getSrvcNameToSendResponse());
            searchResults.setRequestStatus(requestStatus.getStatus());
            searchResults.setRequestDate(Util.ConvertDateToString(requestStatus.getCreatedOn()));
            searchResults.setOrginatedSystemRequest(requestStatus.getOriginatedSystemRequest());
            searchResults.setDestinationSystemRequest(requestStatus.getTargetSystemRequest());
            searchResults.setResponse(requestStatus.getTargetSystemResponse());
            searchResults.setTargetSystemResponseStatus(requestStatus.getTargetSystemResponseStatus());
            searchResultsList.add(searchResults);
        }
        return searchResultsList;
    }


    @Override
    public void reprocessIDocToKafka(String xmlData) {
     //   for ( String iDocXml:iDocs) {

            String jsonData = "",reqStatus=""; String iDocXmlData = "", iDocNum = "", iDocType = "";
            InputStream stream = new ByteArrayInputStream(xmlData.getBytes());
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = builderFactory.newDocumentBuilder();
                Document xmlDocument = builder.parse(stream);
                iDocNum = getiDocNum(xmlDocument);
                iDocType = getiDocType(xmlDocument);
            } catch (Exception e) {
                log.error("Error occured during iDoc xml string to json conversion: " + e.getMessage());
            }
            log.info("SAP (" + iDocType + ") iDoc number " +iDocNum +" is being received for reprocessing" );
            String producerTopic =  "sap.idoc."+iDocType.trim().toLowerCase()+".topic";
            Producer producer = new KafkaProducer(producerProps.asProperties());
            producer.initTransactions();
            producer.beginTransaction();
            try {
                ProducerRecord<String, String> rec = new ProducerRecord<String, String>(producerTopic, iDocNum, xmlData.toString());
                producer.send(rec);
                producer.commitTransaction();
                reqStatus = GlobalConstants.REQUEST_STATUS.SENT_TO_KAFKA_REPROCESS.name();
                log.info("iDoc #: " + iDocNum + " was pushed to Kafka topic'" + producerTopic + " for Reproccessing");
            } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
                log.error("Failed during BW producing message to Kafka " + e.getMessage());
                producer.close();
                reqStatus = GlobalConstants.REQUEST_STATUS.FAILED_TO_SEND_KAFKA_REPROCESS.name();
            } catch (KafkaException e) {
                log.error("Failed during BW producing message to Kafka " + e.getMessage());
                producer.abortTransaction();
                producer.close();
                reqStatus = GlobalConstants.REQUEST_STATUS.FAILED_TO_SEND_KAFKA_REPROCESS.name();
            }

            try {
                RequestStatusHistory history = new RequestStatusHistory();
                history.setRequestId("IDOC");
                history.setRequestKey(iDocNum);
                history.setOriginatedSystem(GlobalConstants.SYSTEMS_NAME.SAP.name());
                history.setTargetSystem(GlobalConstants.SYSTEMS_NAME.TAVANT.name());
                history.setStatus(reqStatus);
                history.setTargetSystemRequest("");
                history.setTargetSystemResponse("");
                history.setTargetSystemResponseStatus(reqStatus);
                history.setMessage(GlobalConstants.REQUEST_STATUS.SENT_TO_KAFKA_REPROCESS.name());
                history.setOriginatedSystemRequest(xmlData.toString());
                history.setSrvcNameToSendResponse(iDocType);
                history.setCreatedOn(new Date());
                history.setUpdatedOn(new Date());
                ObjectMapper objectMapperjson = new ObjectMapper();
                objectMapperjson.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                String reqHistory = objectMapperjson.writeValueAsString(history);
                updateRequestStatus(reqHistory, mwStatusURI );

            }catch(Exception e){
                log.info("Exception in updating request status "+e.getMessage());
            }

            log.info("iDoc number "+ iDocNum +" was sent to Kafka topic '"+producerTopic + "' successfully for reprocessing." );
      //  }
    }

    public String getiDocNum (Document xmlDocument){
        String iDocNum = "";
        boolean checkIDocNum = true;
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            iDocNum = xPath.compile(GlobalConstants.IDOC_NUM_ORDER_XPATH_EXPR).evaluate(xmlDocument);
            if (StringUtils.isEmpty(iDocNum) || iDocNum == null) {
                iDocNum = xPath.compile(GlobalConstants.IDOC_NUM_CUSTOMER_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocNum) || iDocNum == null) {
                iDocNum = xPath.compile(GlobalConstants.IDOC_NUM_MATERIAL_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocNum) || iDocNum == null) {
                iDocNum = xPath.compile(GlobalConstants.IDOC_NUM_SUPPLIER_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocNum) || iDocNum == null) {
                iDocNum = xPath.compile(GlobalConstants.IDOC_NUM_EXTND_WARRANTY_REGN_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocNum) || iDocNum == null) {
                iDocNum = xPath.compile(GlobalConstants.IDOC_NUM_DNT_PART_PRICING_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocNum) || iDocNum == null) {
                iDocNum = xPath.compile(GlobalConstants.IDOC_NUM_SUP_PART_PRICING_XPATH_EXPR).evaluate(xmlDocument);
            }
            log.info("Received iDoc Type :" + iDocNum);
        } catch (Exception e) {

        }
        return iDocNum;
    }
    public String getiDocType (Document xmlDocument){
        String iDocType = "";
        boolean checkIDocType = true;
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            iDocType = xPath.compile(GlobalConstants.IDOC_ORDER_XPATH_EXPR).evaluate(xmlDocument);
            if (StringUtils.isEmpty(iDocType) || iDocType == null) {
                iDocType = xPath.compile(GlobalConstants.IDOC_CUSTOMER_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocType) || iDocType == null) {
                iDocType = xPath.compile(GlobalConstants.IDOC_MATERIAL_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocType) || iDocType == null) {
                iDocType = xPath.compile(GlobalConstants.IDOC_SUPPLIER_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocType) || iDocType == null) {
                iDocType = xPath.compile(GlobalConstants.IDOC_EXTND_WARRANTY_REGN_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocType) || iDocType == null) {
                iDocType = xPath.compile(GlobalConstants.IDOC_DNT_PART_PRICING_XPATH_EXPR).evaluate(xmlDocument);
            }
            if (StringUtils.isEmpty(iDocType) || iDocType == null) {
                iDocType = xPath.compile(GlobalConstants.IDOC_SUP_PART_PRICING_XPATH_EXPR).evaluate(xmlDocument);
            }
            log.info("Received iDoc Type :" + iDocType);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return iDocType;

    }

    public void updateRequestStatus(String jsonRequest, String endpointUri) {
        ResponseBody rBody = null;
        //  log.info("endpointUri getting invoked::" + endpointUri + " ::  JSON INPUT REQUEST:" + jsonRequest);
        try {
            OkHttpClient client = new OkHttpClient();
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");

            RequestBody body = RequestBody.create(mediaType, jsonRequest);
            okhttp3.Request postRequest = new okhttp3.Request.Builder()
                    .url(endpointUri)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .build();
            long t1 = System.currentTimeMillis();
            Response response = client.newCall(postRequest).execute();
            long t2 = System.currentTimeMillis();
            log.info("Request status Update service time consumtion: "+ (t2-t1) + " ms");
            rBody = response.body();
            if(!response.isSuccessful()){
                convert(rBody);
            }

        } catch (Exception e) {
            log.error(" MongoDB update status Exception:" +e.getCause()+" : "+ e.getMessage());
        }
        finally {
            rBody.close();
        }
    }

}