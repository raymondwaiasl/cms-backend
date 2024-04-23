package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormInputRequestDto {

    private String id;
    private String refNum;
    private String category;
    private String formInputRequestTitle;
    private String requester;
    private String formInputRequestStatus;

    @Temporal(TemporalType.DATE)
    private Date inputStartDate;

    @Temporal(TemporalType.DATE)
    private Date inputEndDate;
}
