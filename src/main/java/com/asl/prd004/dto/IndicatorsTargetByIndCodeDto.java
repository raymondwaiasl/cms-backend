package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorsTargetByIndCodeDto {

    private String indicatorCode;
    private String type;
    private Double value;

}
