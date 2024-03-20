package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAndDrafDto {
    private String misTypeId;
    private String misTypeLabel;
    private String misTypeName;
    private String draftTable;
    private String groupPerData;
}