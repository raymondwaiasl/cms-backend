package com.asl.prd004.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
@NoArgsConstructor
@DynamicInsert

public class AutolinkDetail implements Serializable {
    @Id
    private String cmsAutolinkConditionId;

    private String misColumnId;

    private String misFolderId;

    private String misTypeId;

    private String cmsAutolinkId;

    private String misFolderName;

    private String cmsFolderLevel;

    private String misColumnLabel;

    private String cmsAutolinkCondition;

    private String cmsAutolinkValue;

    private String cmsAutolinkConditionRel;
    @Column(name = "cms_autolink_condition_id", length = 20)
    public String getCmsAutolinkConditionId() {
        return cmsAutolinkConditionId;
    }

    public void setCmsAutolinkConditionId(String cmsAutolinkConditionId) {
        this.cmsAutolinkConditionId = cmsAutolinkConditionId;
    }
    @Column(name = "mis_column_id", length = 20)
    public String getMisColumnId() {
        return misColumnId;
    }

    public void setMisColumnId(String misColumnId) {
        this.misColumnId = misColumnId;
    }
    @Column(name = "mis_folder_id", length = 20)
    public String getMisFolderId() {
        return misFolderId;
    }

    public void setMisFolderId(String misFolderId) {
        this.misFolderId = misFolderId;
    }
    @Column(name = "mis_type_id", length = 20)
    public String getMisTypeId() {
        return misTypeId;
    }

    public void setMisTypeId(String misTypeId) {
        this.misTypeId = misTypeId;
    }

    @Column(name = "cms_autolink_id", length = 20)
    public String getCmsAutolinkId() {
        return cmsAutolinkId;
    }

    public void setCmsAutolinkId(String cmsAutolinkId) {
        this.cmsAutolinkId = cmsAutolinkId;
    }

    @Column(name = "mis_folder_name", length = 20)
    public String getMisFolderName() {
        return misFolderName;
    }

    public void setMisFolderName(String misFolderName) {
        this.misFolderName = misFolderName;
    }
    @Column(name = "cms_folder_level", length = 20)
    public String getCmsFolderLevel() {
        return cmsFolderLevel;
    }

    public void setCmsFolderLevel(String cmsFolderLevel) {
        this.cmsFolderLevel = cmsFolderLevel;
    }
    @Column(name = "mis_column_label", length = 20)
    public String getMisColumnLabel() {
        return misColumnLabel;
    }

    public void setMisColumnLabel(String misColumnLabel) {
        this.misColumnLabel = misColumnLabel;
    }
    @Column(name = "cms_autolink_condition", length = 20)
    public String getCmsAutolinkCondition() {
        return cmsAutolinkCondition;
    }

    public void setCmsAutolinkCondition(String cmsAutolinkCondition) {
        this.cmsAutolinkCondition = cmsAutolinkCondition;
    }
    @Column(name = "cms_autolink_value", length = 20)
    public String getCmsAutolinkValue() {
        return cmsAutolinkValue;
    }

    public void setCmsAutolinkValue(String cmsAutolinkValue) {
        this.cmsAutolinkValue = cmsAutolinkValue;
    }
    @Column(name = "cms_Autolink_condition_rel", length = 20)
    public String getCmsAutolinkConditionRel() {
        return cmsAutolinkConditionRel;
    }

    public void setCmsAutolinkConditionRel(String cmsAutolinkConditionRel) {
        this.cmsAutolinkConditionRel = cmsAutolinkConditionRel;
    }

}
