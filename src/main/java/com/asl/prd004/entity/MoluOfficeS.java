package com.asl.prd004.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "molu_office_s")
public class MoluOfficeS extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "molu_code", nullable = false, length = 10)
    private String moluCode;

    @Column(name = "mo_code", nullable = false, length = 10)
    private String moCode;

    @Column(name = "molu_type", nullable = false, length = 10)
    private String moluType;

    @Column(name = "molu_name_en", length = 100)
    private String moluNameEn;

    @Column(name = "molu_name_tc", length = 50)
    private String moluNameTc;

    @Column(name = "active")
    private Integer active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoluCode() {
        return moluCode;
    }

    public void setMoluCode(String moluCode) {
        this.moluCode = moluCode;
    }

    public String getMoCode() {
        return moCode;
    }

    public void setMoCode(String moCode) {
        this.moCode = moCode;
    }

    public String getMoluType() {
        return moluType;
    }

    public void setMoluType(String moluType) {
        this.moluType = moluType;
    }

    public String getMoluNameEn() {
        return moluNameEn;
    }

    public void setMoluNameEn(String moluNameEn) {
        this.moluNameEn = moluNameEn;
    }

    public String getMoluNameTc() {
        return moluNameTc;
    }

    public void setMoluNameTc(String moluNameTc) {
        this.moluNameTc = moluNameTc;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}