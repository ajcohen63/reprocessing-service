package com.ktc.integration.kwarranty.reprocessingservice.repository.custom;

import com.ktc.integration.kwarranty.reprocessingservice.dto.SearchParams;
import com.ktc.integration.kwarranty.reprocessingservice.entity.RequestStatus;
import com.ktc.integration.kwarranty.reprocessingservice.utils.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class RequestStatusCustomQueriesImpl implements RequestStatusCustomQueries {

    private static final Logger log = LogManager.getLogger(RequestStatusCustomQueriesImpl.class);
    @Autowired
    MongoOperations mongoOperations;

    @Override
    public RequestStatus findByRequestIdRequestKeyAndOriginatedSystem(String requestId, String requestKey,String originatedSystem) {
        Query query = new Query(Criteria.where("requestKey").is(requestKey.trim()).and("requestId").is(requestId.trim()).and("originatedSystem").is(originatedSystem.trim()));
       long t1 = System.currentTimeMillis();
        RequestStatus rs = mongoOperations.findOne(query, RequestStatus.class);
        long t2 = System.currentTimeMillis();
        log.info(Thread.currentThread().getId()+" requestId: "+requestId+" :Request Key: "+requestKey +": originatedSystem"+originatedSystem+" :Time taken to fetch a record from MongoDB: "+(t2-t1) +" ms");
        return rs;
    }

    @Override
    public List<RequestStatus> findByStatus(String completed, String failed){
        List<String> list = new ArrayList<>();
        list.add(completed);
        list.add(failed);
        Query query = new Query(Criteria.where("status").in(list));
        return mongoOperations.find(query, RequestStatus.class);
    }

    @Override
    public List<RequestStatus> searchByOriginationSystemAndStatusIn(String orginationSystem, List<String> status) {
        Query query = new Query(Criteria.where("originatedSystem").is(orginationSystem).and("status").in(status));
        return mongoOperations.find(query, RequestStatus.class);
    }

    @Override
    public List<RequestStatus> searchByFilters(SearchParams searchParams) {
        List<Criteria> andCriteria = new ArrayList<>();

        if(StringUtils.isNotEmpty(searchParams.getRequestKey())){
            andCriteria.add(Criteria.where("requestKey").is(searchParams.getRequestKey()));
        }
        if(StringUtils.isNotEmpty(searchParams.getRequestId())){
            andCriteria.add(Criteria.where("requestId").is(searchParams.getRequestId()));
        }
        if(StringUtils.isNotEmpty(searchParams.getRequestOrginatedFrom())){
            andCriteria.add(Criteria.where("originatedSystem").is(searchParams.getRequestOrginatedFrom()));
        }
        if(StringUtils.isNotEmpty(searchParams.getInterfaceName())){
            andCriteria.add(Criteria.where("srvcNameToSendResponse").is(searchParams.getInterfaceName()));
        }
        if(StringUtils.isNotEmpty(searchParams.getStatus())){
            andCriteria.add(Criteria.where("status").is(searchParams.getStatus()));
        }
        if(StringUtils.isNotEmpty(searchParams.getRequestDestinationSystem())){
            andCriteria.add(Criteria.where("targetSystem").is(searchParams.getRequestDestinationSystem()));
        }
        if(searchParams.getRequestReceivedFrom()!=null){
            andCriteria.add(Criteria.where("createdOn").gte(searchParams.getRequestReceivedFrom()));
        }
        if(searchParams.getRequestRecivedTo()!=null){
            andCriteria.add(Criteria.where("createdOn").lte(searchParams.getRequestRecivedTo()));
        }

        Criteria orCriteria = new Criteria().andOperator(andCriteria.toArray(new Criteria[andCriteria.size()]));
        return  mongoOperations.find(new Query().addCriteria(orCriteria), RequestStatus.class);
    }

    @Override
    public  List<RequestStatus>  findByRowId(String rowId) {
        List<RequestStatus> resp = new ArrayList<>();
        log.info("findByrowId");
        List<Criteria> andCriteria = new ArrayList<>();

        andCriteria.add(Criteria.where("_id").is(rowId));
        Criteria orCriteria = new Criteria().andOperator(andCriteria.toArray(new Criteria[andCriteria.size()]));



        return  mongoOperations.find(new Query().addCriteria(orCriteria), RequestStatus.class);
    }

    @Override
    public List<RequestStatus> findFailedMessagesByDate(Date dt) {
        List<Criteria> andCriteria = new ArrayList<>();

        List<String> reprocessingStatues = new ArrayList<>();
        reprocessingStatues.add(GlobalConstants.REQUEST_STATUS.SENT_TO_KAFKA.toString());
        reprocessingStatues.add(GlobalConstants.REQUEST_STATUS.FAILED.toString());
        andCriteria.add(Criteria.where("status").in(reprocessingStatues));
        andCriteria.add(Criteria.where("createdOn").lte(dt));
        Criteria orCriteria = new Criteria().andOperator(andCriteria.toArray(new Criteria[andCriteria.size()]));
        return  mongoOperations.find(new Query().addCriteria(orCriteria), RequestStatus.class);

    }


}
