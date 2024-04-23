package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorDetailDto {

    private String id;
    private String indCode;
    private String subcategoryCode;
    private String indNameEn;
    private String indNameTc;
    private String dataType;
    private String currency;
    private Integer active;
    private String subIndicatorNameEn;
    private String subIndicatorNameTc;
    private String categoryCode;
}
