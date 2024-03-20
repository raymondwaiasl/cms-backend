package com.asl.prd004.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/6/5 16:09
 */
@Entity
@Table(name = "mis_bi_config", schema = "MIS", catalog = "")
public class BiToolConfig {
    @Id
    @Column(name = "mis_bi_config_id",  nullable = false, length = 20)
    private String misBiConfigId;
    @Column(name = "mis_bi_config_name",  nullable = false, length = 20)
    private String misBiConfigName;
    @Column(name = "mis_bi_config_type_id",  nullable = false, length = 20)
    private String misBiConfigTypeId;
    @Column(name = "mis_bi_config_type",  nullable = false, length = 20)
    private String misBiConfigType;
    @Column(name = "mis_bi_config_graphic_type",  nullable = false, length = 20)
    private String misBiConfigGraphicType;
    @Column(name = "mis_bi_config_column_hor",  nullable = false, length = 20)
    private String misBiConfigColumnHor;
    @Column(name = "mis_bi_config_column_vet",  nullable = false, length = 20)
    private String misBiConfigColumnVet;
    @Column(name = "mis_bi_config_date",  nullable = false, length = 20)
    private String misBiConfigDate;
    @Column(name = "mis_bi_config_def_view",  nullable = false, length = 20)
    private String misBiConfigDefView;

    public String getMisBiConfigId() {
        return misBiConfigId;
    }

    public void setMisBiConfigId(String misBiConfigId) {
        this.misBiConfigId = misBiConfigId;
    }

    public String getMisBiConfigName() {
        return misBiConfigName;
    }

    public void setMisBiConfigName(String misBiConfigName) {
        this.misBiConfigName = misBiConfigName;
    }

    public String getMisBiConfigTypeId() {
        return misBiConfigTypeId;
    }

    public String getMisBiConfigType() {
        return misBiConfigType;
    }

    public void setMisBiConfigType(String misBiConfigType) {
        this.misBiConfigType = misBiConfigType;
    }

    public void setMisBiConfigTypeId(String misBiConfigTypeId) {
        this.misBiConfigTypeId = misBiConfigTypeId;
    }

    public String getMisBiConfigGraphicType() {
        return misBiConfigGraphicType;
    }

    public void setMisBiConfigGraphicType(String misBiConfigGraphicType) {
        this.misBiConfigGraphicType = misBiConfigGraphicType;
    }

    public String getMisBiConfigColumnHor() {
        return misBiConfigColumnHor;
    }

    public void setMisBiConfigColumnHor(String misBiConfigColumnHor) {
        this.misBiConfigColumnHor = misBiConfigColumnHor;
    }

    public String getMisBiConfigColumnVet() {
        return misBiConfigColumnVet;
    }

    public void setMisBiConfigColumnVet(String misBiConfigColumnVet) {
        this.misBiConfigColumnVet = misBiConfigColumnVet;
    }

    public String getMisBiConfigDate() {
        return misBiConfigDate;
    }

    public void setMisBiConfigDate(String misBiConfigDate) {
        this.misBiConfigDate = misBiConfigDate;
    }

    public String getMisBiConfigDefView() {
        return misBiConfigDefView;
    }

    public void setMisBiConfigDefView(String misBiConfigDefView) {
        this.misBiConfigDefView = misBiConfigDefView;
    }
}


