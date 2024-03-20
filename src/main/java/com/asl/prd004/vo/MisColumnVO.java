package com.asl.prd004.vo;

import lombok.Data;

@Data
public class MisColumnVO {
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
    private MisColumnComputeFormulaVO misColumnComputeFormula;

    private String misColumnComputeQuery;
}
