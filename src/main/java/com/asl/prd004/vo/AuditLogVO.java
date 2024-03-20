package com.asl.prd004.vo;

import lombok.Data;

@Data
public class AuditLogVO {
    private String typeId;
    private String tableName;
    private String recordId;
}
