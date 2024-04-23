package com.asl.prd004.entity;


import com.asl.prd004.dto.FormInputRequestPeriodDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "form_input_request_s")
@Data
public class FormInputRequestS extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "ref_num", length = 20)
    private String refNum;

    @Column(name = "form_input_request_title", length = 50)
    private String formInputRequestTitle;

    @Column(name = "form_input_request_description", length = 4000)
    private String formInputRequestDescription;

    @Column(name = "category_code", length = 20)
    private String categoryCode;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "input_start_date")
    private Date inputStartDate;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "input_end_date")
    private Date inputEndDate;

    @Column(name = "deadline_alert_day")
    private Integer deadlineAlertDay;

    @Column(name = "form_input_request_status", length = 10)
    private String formInputRequestStatus;

    @Transient
    private List<String> indicators;

    @Transient
    private List<String> molus;

    @Transient
    private List<FormInputRequestPeriodDto> period;

    @Transient
    private String category;

    @Transient
    private String dataPeriodType;
}