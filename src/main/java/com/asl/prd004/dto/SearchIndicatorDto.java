package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchIndicatorDto {

    private String categoryCode;
    private String subCategoryCode;
    private String indCode;
    private String indicatorName;
    private String lang;
    private String active;

    private PageStateDto pageState;
    private SortModelDto sortModel;
}
