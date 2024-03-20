package com.asl.prd004.dao;

import com.asl.prd004.entity.MisSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionDao extends JpaRepository<MisSubscription,String> {

    MisSubscription findByMisSubscriptionObjIdAndMisSubscriptionUserId(String folderId, String userId);

}
