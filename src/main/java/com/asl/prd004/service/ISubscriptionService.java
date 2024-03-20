package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.SubscriptionBuDto;
import org.json.JSONException;

import java.util.List;

public interface ISubscriptionService {
    Boolean saveSubscription(String id,String typeId,Boolean checkedN,Boolean checkedM,Boolean checkedD,Boolean checkedB,String repeat,String buDate) throws Exception;

    Boolean unSubscription(String objId);

    PageDataDto getSubscriptionMsgListByUserId(PageableDto pageableDto) throws JSONException;

    Boolean deleteMsg(String msgId);

    Boolean updateMsgStatus(String msgId);

    List<SubscriptionBuDto> getSubscriptionListByRepeatType(String misSubEventBuRepeat);

    List<SubscriptionBuDto> getSubscriptionListByFolderId(String folderId);

    List<SubscriptionBuDto> getSubscriptionListByTypeIdAndObjId(String tableId, String recordId);

    Boolean insertMsg(String subscriptionId, String subEventId);

    Boolean folderIsSubscribe(String folderId);
}
