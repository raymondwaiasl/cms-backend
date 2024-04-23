package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchFormInputRequestDto {

    private String refNum;
    private String formInputRequestTitle;
    private String inputStartDate;
    private String inputEndDate;
    private String categoryCode;
    private String dataPeriodType;
    private String lang;

    private PageStateDto pageState;
    private SortModelDto sortModel;
}
