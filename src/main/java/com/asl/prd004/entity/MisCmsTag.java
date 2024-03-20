package com.asl.prd004.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2022/12/1 10:06
 */
@Entity
@Table(name = "MIS_CMS_TAG", schema = "MIS", catalog = "")
public class MisCmsTag extends BaseModel{
    @Id
    @Column(name = "CMS_TAG_ID",  nullable = false, length = 20)
    private String cmsTagId;
    @Column(name = "MIS_TYPE_ID",  nullable = false, length = 20)
    private String misTypeId;
    @Column(name = "MIS_RECORD_ID",  nullable = false, length = 20)
    private String misRecordId;
    @Column(name = "CMS_TAG",  nullable = false, length = 255)
    private String cmsTag;

    public String getCmsTagId() {
        return cmsTagId;
    }

    public void setCmsTagId(String cmsTagId) {
        this.cmsTagId = cmsTagId;
    }

    public String getMisTypeId() {
        return misTypeId;
    }

    public void setMisTypeId(String misTypeId) {
        this.misTypeId = misTypeId;
    }

    public String getMisRecordId() {
        return misRecordId;
    }

    public void setMisRecordId(String misRecordId) {
        this.misRecordId = misRecordId;
    }

    public String getCmsTag() {
        return cmsTag;
    }

    public void setCmsTag(String cmsTag) {
        this.cmsTag = cmsTag;
    }

    @Override
    public String toString() {
        return "MisCmsTag{" +
                "cmsTagId='" + cmsTagId + '\'' +
                ", misTypeId='" + misTypeId + '\'' +
                ", misRecordId='" + misRecordId + '\'' +
                ", cmsTag='" + cmsTag + '\'' +
                '}';
    }
}


