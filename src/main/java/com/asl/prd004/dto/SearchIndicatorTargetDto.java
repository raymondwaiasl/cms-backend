package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchIndicatorTargetDto {

    private String categoryCode;
    private String year;

    private PageStateDto pageState;
    private SortModelDto sortModel;
}
