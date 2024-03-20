package com.asl.prd004.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "form_input_remark_s")
public class FormInputRemarkS extends BaseModel  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "form_input_entry_id", nullable = false, length = 16)
    private String formInputEntryId;

    @Column(name = "ind_code", nullable = false, length = 10)
    private String indCode;

    @Column(name = "remark", length = 4000)
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormInputEntryId() {
        return formInputEntryId;
    }

    public void setFormInputEntryId(String formInputEntryId) {
        this.formInputEntryId = formInputEntryId;
    }

    public String getIndCode() {
        return indCode;
    }

    public void setIndCode(String indCode) {
        this.indCode = indCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}