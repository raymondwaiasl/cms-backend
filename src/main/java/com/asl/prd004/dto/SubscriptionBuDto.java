package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionBuDto {

    private String misSubscriptionId;
    private String misSubscriptionEventId;
    private Date misSubEventBuDate;
    private String misSubEventBuRepeat;
    private String misSubEventMsg;

}
