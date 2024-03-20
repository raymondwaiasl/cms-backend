package com.asl.prd004.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private String id;
    private String categoryCode;
    private String categoryNameEn;
    private String categoryNameTc;
    private String yearType;

    public String getId() {
        return id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryNameEn() {
        return categoryNameEn;
    }

    public String getCategoryNameTc() {
        return categoryNameTc;
    }

    public String getYearType() {
        return yearType;
    }
}
