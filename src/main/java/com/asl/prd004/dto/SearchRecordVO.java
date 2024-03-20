package com.asl.prd004.dto;

import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisQueryFormColumn;
import com.asl.prd004.entity.MisQueryFormCondition;
import lombok.Data;

import java.util.List;

@Data
public class SearchRecordVO {
//    private String misQfTableId;
//    private String folderId;
//    private String typeId;
//    private List<MisQueryFormColumn> qfColumns;
//    private List<MisQueryFormCondition> qfConditions;
//    private List<MisQueryFormDTO> crossRef;
    private List<MisColumn> columnList;
    private List recordList;
    private Long total;
}
