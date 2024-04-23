package com.asl.prd004.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormInputEntryDto {

//    private String id;
    private String formInputRequestId;
    private String id;
    private String refNum;
    private String categoryNameEn;
    private String categoryNameTc;
    private String formInputRequestTitle;
    private String moCode;
    private String formInputStatus;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date inputEndDate;

    private String updatedUser;
}
