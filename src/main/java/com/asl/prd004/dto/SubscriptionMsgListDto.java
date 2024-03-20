package com.asl.prd004.dto;



import java.util.Date;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionMsgListDto {

    private String misSubscriptionMsgId;
    private String misSubscriptionId;
    private Date misSubscriptionMsgDate;
    private String misSubscriptionEventId;
    private String misSubscriptionMsgHasRead;
    private String misSubEventMsg;
    private String misSubscriptionType;
    private String misFolderName;


}
