package com.asl.prd004.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "form_input_request_indicator_s")
public class FormInputRequestindicatorS extends BaseModel implements Serializable {
    @Id
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "form_input_request_id", nullable = false, length = 16)
    private String formInputRequestId;

    @Column(name = "ind_code", nullable = false, length = 10)
    private String indCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormInputRequestId() {
        return formInputRequestId;
    }

    public void setFormInputRequestId(String formInputRequestId) {
        this.formInputRequestId = formInputRequestId;
    }

    public String getIndCode() {
        return indCode;
    }

    public void setIndCode(String indCode) {
        this.indCode = indCode;
    }
}