package com.ktc.integration.kwarranty.reprocessingservice.repository;

import com.ktc.integration.kwarranty.reprocessingservice.entity.RequestStatus;
import com.ktc.integration.kwarranty.reprocessingservice.repository.custom.RequestStatusCustomQueries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RequestStatusRepository extends MongoRepository<RequestStatus, Integer>, RequestStatusCustomQueries {

    RequestStatus save(RequestStatus requestStatus);

    Optional<RequestStatus> findById(Integer id);

    Optional<RequestStatus> findByRequestId(String requestId);

    RequestStatus findByRequestIdRequestKeyAndOriginatedSystem(String requestId,String requestKey, String originatedSystem);

    void deleteById(Integer id);

    void delete(RequestStatus entity);


}
