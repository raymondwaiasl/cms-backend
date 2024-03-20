package com.asl.prd004.dto;

import com.asl.prd004.entity.MisQueryFormColumn;
import com.asl.prd004.entity.MisQueryFormCondition;
import lombok.Data;

import java.util.List;

@Data
public class QueryFormDto {
    //private MisQueryForm queryForm;
    private  String misQfId;
    private  String misQfName;
    private  String misQfTableId;
    private  String misQfPublic;
    private  String misQfGroupId;
    private List<MisQueryFormColumn> qfColumns;
    private List<MisQueryFormCondition> qfConditions;

}
