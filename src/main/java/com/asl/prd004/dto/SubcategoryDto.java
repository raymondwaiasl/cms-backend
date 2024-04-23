package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryDto {

    private String id;
    private String categoryCode;
    private String subcategoryCode;
    private String subcategoryNameEn;
    private String subcategoryNameTc;

}
