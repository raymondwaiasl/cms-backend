package com.asl.prd004.dao;

import com.asl.prd004.entity.MisSubscriptionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionEventDao extends JpaRepository<MisSubscriptionEvent,String> {
    MisSubscriptionEvent findByMisSubscriptionIdAndMisSubEventMsg(String subscriptionId, String msg);
}
