package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorTargetDetailDto {

    private String ind_code;
    private String moluCode;
    private Double target;
}
