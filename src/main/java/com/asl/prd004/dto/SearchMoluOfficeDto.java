package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchMoluOfficeDto {

    private String moluCode;
    private String moCode;
    private String moluType;
    private String moluName;
    private String active;
    private String lang;

    private PageStateDto pageState;
    private SortModelDto sortModel;
}
