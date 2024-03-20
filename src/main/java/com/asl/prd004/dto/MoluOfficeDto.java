package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoluOfficeDto {

//    private String id;
    private String moluCode;
    private String moCode;
    private String moluType;
    private String moluNameEn;
    private String moluNameTc;
    private Integer active;
}
