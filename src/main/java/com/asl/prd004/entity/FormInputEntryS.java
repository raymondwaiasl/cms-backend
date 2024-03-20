package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

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


    @Column(name = "workflow_type", nullable = false)
    private Integer workflowType;

    @Column(name = "mo_code", nullable = false, length = 10)
    private String moCode;

    @Column(name = "molu_code", nullable = false, length = 10)
    private String moluCode;

    @Column(name = "form_input_status", nullable = false, length = 10)
    private String formInputStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormInputRequestID() {
        return formInputRequestID;
    }

    public void setFormInputRequestID(String formInputRequestID) {
        this.formInputRequestID = formInputRequestID;
    }

    public Integer getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(Integer workflowType) {
        this.workflowType = workflowType;
    }

    public String getMoCode() {
        return moCode;
    }

    public void setMoCode(String moCode) {
        this.moCode = moCode;
    }

    public String getMoluCode() {
        return moluCode;
    }

    public void setMoluCode(String moluCode) {
        this.moluCode = moluCode;
    }

    public String getFormInputStatus() {
        return formInputStatus;
    }

    public void setFormInputStatus(String formInputStatus) {
        this.formInputStatus = formInputStatus;
    }
}