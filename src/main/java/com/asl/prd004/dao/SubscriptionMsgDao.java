package com.asl.prd004.dao;

import com.asl.prd004.dto.SubscriptionBuDto;
import com.asl.prd004.dto.SubscriptionMsgListDto;
import com.asl.prd004.entity.MisSubscriptionMsg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SubscriptionMsgDao extends  JpaRepository<MisSubscriptionMsg, String> {

    @Query(value = "select new com.asl.prd004.dto.SubscriptionMsgListDto( msm.misSubscriptionMsgId, msm.misSubscriptionId, msm.misSubscriptionMsgDate, msm.misSubscriptionEventId, msm.misSubscriptionMsgHasRead, mse.misSubEventMsg, ms.misSubscriptionType, mf.misFolderName ) " +
            " from MisSubscriptionMsg msm " +
            " left join MisSubscription ms on msm.misSubscriptionId = ms.misSubscriptionId  " +
            " left join MisSubscriptionEvent mse on msm.misSubscriptionEventId = mse.misSubEventId " +
            " left join MisFolder mf on mf.misFolderId = ms.misSubscriptionObjId " +
            " where ms.misSubscriptionUserId = :userId")
    Page<SubscriptionMsgListDto> findAllByMisSubscriptionUserId(@Param("userId") String userId, Pageable pageable);

    @Query(value = "select new com.asl.prd004.dto.SubscriptionBuDto( ms.misSubscriptionId, mse.misSubEventId, mse.misSubEventBuDate, mse.misSubEventBuRepeat, mse.misSubEventMsg) " +
            " from MisSubscription ms " +
            " join MisSubscriptionEvent mse on mse.misSubscriptionId = ms.misSubscriptionId  " +
            " where ms.misSubscriptionObjId = :folderId")
    List<SubscriptionBuDto> findAllByMisSubscriptionObjId(@Param("folderId") String folderId);

    @Query(value = "select new com.asl.prd004.dto.SubscriptionBuDto( ms.misSubscriptionId, mse.misSubEventId, mse.misSubEventBuDate, mse.misSubEventBuRepeat, mse.misSubEventMsg) " +
            " from MisSubscription ms " +
            " join MisSubscriptionEvent mse on mse.misSubscriptionId = ms.misSubscriptionId  " +
            " where ms.misSubscriptionType=:tableId and ms.misSubscriptionObjId = :objId")
    List<SubscriptionBuDto> findAllByMisSubscriptionTypeAndMisSubscriptionObjId(@Param("tableId") String tableId ,@Param("objId") String objId);

    @Query(value = "select new com.asl.prd004.dto.SubscriptionBuDto( ms.misSubscriptionId, mse.misSubEventId, mse.misSubEventBuDate, mse.misSubEventBuRepeat, mse.misSubEventMsg) " +
            " from MisSubscription ms " +
            " join MisSubscriptionEvent mse on mse.misSubscriptionId = ms.misSubscriptionId  " +
            " where mse.misSubEventBuRepeat = :misSubEventBuRepeat")
    List<SubscriptionBuDto> findAllByMisSubEventBuRepeat(@Param("misSubEventBuRepeat") String misSubEventBuRepeat);

}
