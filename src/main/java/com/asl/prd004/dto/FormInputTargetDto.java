package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormInputTargetDto {

    private String indicatorCode;
    private Double value;

}
