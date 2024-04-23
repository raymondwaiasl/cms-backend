package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormInputRequestPeriodDto {

//    private String id;
    private Integer year;
    private Integer startMonth;
    private Integer endMonth;
}
