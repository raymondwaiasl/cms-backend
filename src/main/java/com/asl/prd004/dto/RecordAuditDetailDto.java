package com.asl.prd004.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordAuditDetailDto {

    private String misAuditDtlId;
    private String misAuditDtlAction;
    private String misAuditRecId;
    private String misAuditRechistBfid;
    private String misAuditRechistAfid;
    private String misOperator;
    private Date misOperationTime;


}
