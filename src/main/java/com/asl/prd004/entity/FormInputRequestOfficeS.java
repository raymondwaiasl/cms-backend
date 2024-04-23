package com.asl.prd004.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "form_input_request_office_s")
@Data
public class FormInputRequestOfficeS extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "form_input_request_id", nullable = false, length = 16)
    private String formInputRequestId;

    @Column(name = "molu_code", nullable = false, length = 10)
    private String moluCode;

}