package com.asl.prd004.dto;

import com.asl.prd004.entity.MisColumn;
import lombok.Data;

import java.util.List;

@Data
public class TypeDto {
    private String misTypeId;
    private String misTypeLabel;
    private String misTypeName;
    private List<MisColumn> misColumnList;
}
