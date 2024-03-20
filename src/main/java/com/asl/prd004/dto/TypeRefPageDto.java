package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeRefPageDto {
    private String tableId;
    private PageStateDto pageState;
    private SortModelDto sortModel;
}
