package com.asl.prd004.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "form_input_request_period_s")
public class FormInputRequestPeriodS extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "form_input_request_id", nullable = false, length = 16)
    private String formInputRequestId;

    @Column(name = "fiscal_year", length = 10)
    private String fiscalYear;

    @Column(name = "year")
    private Integer year;

    @Column(name = "start_month")
    private Integer startMonth;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "data_period_type", length = 10)
    private String dataPeriodType;

}