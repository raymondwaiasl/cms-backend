package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyColumnPermissionDto {

    private String misPropertyColumnPermissionId;
    private String misPropertyConfigDetailColumnId;
    private String misPdType;
    private String misPdPerformerId;
    private String misPdAction;
}
