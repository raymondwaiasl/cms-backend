package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidateDataListDto {

    private String categoryCode;
//    private String category;
    private Integer year;
    private String categoryNameEn;
    private String categoryNameTc;
}
