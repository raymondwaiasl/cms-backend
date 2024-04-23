package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "form_input_entry_s")
public class FormInputEntryS extends BaseModel  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "form_input_request_id", nullable = false, length = 16)
    private String formInputRequestID;


    @Column(name = "workflow_type")
    private Integer workflowType;

    @Column(name = "mo_code", nullable = false, length = 10)
    private String moCode;

    @Column(name = "molu_code", length = 10)
    private String moluCode;

    @Column(name = "form_input_status", nullable = false, length = 10)
    private String formInputStatus;

    @Column(name = "data_period_type", nullable = false, length = 10)
    private String dataPeriodType;

    @Transient
    private String category;
    @Transient
    private String formInputRequestId;
    @Transient
    private String refNum;
    @Transient
    private String formInputRequestTitle;
    @Transient
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date inputEndDate;
    @Transient
    private String updatedUser;


}