package com.asl.prd004.dto;

import lombok.Data;

@Data
public class MisColumnDTO {
    private String misColumnId;
    private String misTypeId;
    private String misColumnName;
    private String misColumnLabel;
    private String misColumnType;
    private Float misColumnWidth;
    private String misColumnLength;
    private String misColumnInputType;
    private String misColumnDictionary;
    private String misColumnAllowSearch;
    private String misColumnAllowEmpty;
    private String misColumnComputeFrom;
    private MisColumnComputeFormulaDTO misColumnComputeFormula;
    private String misColumnComputeQuery;
}
