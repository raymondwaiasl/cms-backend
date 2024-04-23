package com.asl.prd004.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormInputValueDto {

//    private String id;
    private String formInputValueId;
    private String categoryNameEn;
    private String categoryNameTc;
    private String Indicator;
    private Integer year;
    private Integer startMonth;
    private Integer endMonth;
    private String dataType;

    @Transient
    private String value;

}
