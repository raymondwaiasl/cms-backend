package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2022/12/1 10:06
 */
@Entity
@Table(name = "MIS_CMS_AUTOLINK_CONDITION", schema = "MIS", catalog = "")
public class MisCmsAutolinkCondition extends BaseModel{
    @Id
    @Column(name = "CMS_AUTOLINK_CONDITION_ID",  nullable = false, length = 20)
    private String cmsAutolinkConditionId;
    @Column(name = "CMS_AUTOLINK_ID",  nullable = false, length = 20)
    private String cmsAutolinkId;
    @Column(name = "MIS_COLUMN_ID",  nullable = false, length = 20)
    private String misColumnId;
    @Column(name = "CMS_AUTOLINK_CONDITION",  nullable = false, length = 20)
    private String cmsAutolinkCondition;
    @Column(name = "CMS_AUTOLINK_VALUE",  nullable = false, length = 255)
    private String cmsAutolinkValue;
    @Column(name = "CMS_AUTOLINK_CONDITION_REL",length = 3)
    private String cmsAutolinkConditionRel;
    @ManyToOne
    @JoinColumn(name="CMS_AUTOLINK_ID", insertable = false,updatable = false)
    @JsonIgnore
    private MisCmsAutolink autolink;

    public String getCmsAutolinkConditionId() {
        return cmsAutolinkConditionId;
    }

    public void setCmsAutolinkConditionId(String cmsAutolinkConditionId) {
        this.cmsAutolinkConditionId = cmsAutolinkConditionId;
    }

    public String getCmsAutolinkId() {
        return cmsAutolinkId;
    }

    public void setCmsAutolinkId(String cmsAutolinkId) {
        this.cmsAutolinkId = cmsAutolinkId;
    }

    public String getMisColumnId() {
        return misColumnId;
    }

    public void setMisColumnId(String misColumnId) {
        this.misColumnId = misColumnId;
    }

    public String getCmsAutolinkCondition() {
        return cmsAutolinkCondition;
    }

    public void setCmsAutolinkCondition(String cmsAutolinkCondition) {
        this.cmsAutolinkCondition = cmsAutolinkCondition;
    }

    public String getCmsAutolinkValue() {
        return cmsAutolinkValue;
    }

    public void setCmsAutolinkValue(String cmsAutolinkValue) {
        this.cmsAutolinkValue = cmsAutolinkValue;
    }

    public String getCmsAutolinkConditionRel() {
        return cmsAutolinkConditionRel;
    }

    public void setCmsAutolinkConditionRel(String cmsAutolinkConditionRel) {
        this.cmsAutolinkConditionRel = cmsAutolinkConditionRel;
    }

    public MisCmsAutolink getAutolink() {
        return autolink;
    }

    public void setAutolink(MisCmsAutolink autolink) {
        this.autolink = autolink;
    }

    @Override
    public String toString() {
        return "MisCmsAutolinkCondition{" +
                "cmsAutolinkConditionId='" + cmsAutolinkConditionId + '\'' +
                ", cmsAutolinkId='" + cmsAutolinkId + '\'' +
                ", misColumnId='" + misColumnId + '\'' +
                ", cmsAutolinkCondition='" + cmsAutolinkCondition + '\'' +
                ", cmsAutolinkValue='" + cmsAutolinkValue + '\'' +
                ", cmsAutolinkConditionRel='" + cmsAutolinkConditionRel + '\'' +
                '}';
    }
}


