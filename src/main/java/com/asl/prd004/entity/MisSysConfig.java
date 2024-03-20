package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "mis_sys_config", schema = "MIS", catalog = "")
public class MisSysConfig {
    private String misSysConfigId;
    private String misSysConfigKey;
    private String misSysConfigValue;
    private String misSysConfigVisible;
    private String misSysConfigType;// 0:文本，1：图片

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_sys_config_id", nullable = false, length = 16)
    public String getMisSysConfigId() {
        return misSysConfigId;
    }

    public void setMisSysConfigId(String misSysConfigId) {
        this.misSysConfigId = misSysConfigId;
    }

    @Basic
    @Column(name = "mis_sys_config_key", nullable = false, length = 40)
    public String getMisSysConfigKey() {
        return misSysConfigKey;
    }

    public void setMisSysConfigKey(String misSysConfigKey) {
        this.misSysConfigKey = misSysConfigKey;
    }

    @Basic
    @Column(name = "mis_sys_config_value", nullable = false, length = 255)
    public String getMisSysConfigValue() {
        return misSysConfigValue;
    }

    public void setMisSysConfigValue(String misSysConfigValue) {
        this.misSysConfigValue = misSysConfigValue;
    }

    @Basic
    @Column(name = "mis_sys_config_visible", nullable = false, length = 1)
    public String getMisSysConfigVisible() {
        return misSysConfigVisible;
    }

    @Basic
    @Column(name = "mis_sys_config_type", nullable = false, length = 1)
    public void setMisSysConfigVisible(String misSysConfigVisible) {
        this.misSysConfigVisible = misSysConfigVisible;
    }

    public String getMisSysConfigType() {
        return misSysConfigType;
    }

    public void setMisSysConfigType(String misSysConfigType) {
        this.misSysConfigType = misSysConfigType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MisSysConfig that = (MisSysConfig) o;
        return Objects.equals(misSysConfigId, that.misSysConfigId) && Objects.equals(misSysConfigKey, that.misSysConfigKey) && Objects.equals(misSysConfigValue, that.misSysConfigValue) && Objects.equals(misSysConfigVisible, that.misSysConfigVisible);
    }

    @Override
    public int hashCode() {
        return Objects.hash(misSysConfigId, misSysConfigKey, misSysConfigValue, misSysConfigVisible);
    }
}
