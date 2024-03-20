package com.asl.prd004.entity;

import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2022/12/1 10:06
 */
@Entity
@Table(name = "MIS_CMS_VERSION", schema = "MIS", catalog = "")
public class MisCmsVersion extends BaseModel{
    @Id
    @Column(name = "CMS_VERSION_ID",  nullable = false, length = 20)
    private String cmsVersionId;
    @Column(name = "MIS_TYPE_ID",  nullable = false, length = 20)
    private String misTypeId;
    @Column(name = "MIS_RECORD_ID",  nullable = false, length = 20)
    private String misRecordId;
    @Column(name = "CMS_VERSION_NO",  nullable = false, length = 20)
    private String cmsVersionNo;
    @Column(name = "CMS_FILE_LOCATION",  nullable = false, length = 255)
    private String cmsFileLocation;
    @Column(name = "CMS_CREATION_DATE",  nullable = false, length = 30)
    private String cmsCreationDate;
    @Column(name = "VERSION_STATUS",  nullable = false, length = 1)
    private String versionStatus;
    @Column(name = "CMS_CREATOR_USER_ID",  nullable = false, length = 20)
    private String cmsCreatorUserId;
    @Column(name = "FILE_SIZE",  nullable = false, length = 20)
    private String fileSize;
    @Transient
    private String cmsUserName;

    public String getCmsVersionId() {
        return cmsVersionId;
    }

    public void setCmsVersionId(String cmsVersionId) {
        this.cmsVersionId = cmsVersionId;
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

    public String getCmsVersionNo() {
        return cmsVersionNo;
    }

    public void setCmsVersionNo(String cmsVersionNo) {
        this.cmsVersionNo = cmsVersionNo;
    }

    public String getCmsFileLocation() {
        return cmsFileLocation;
    }

    public void setCmsFileLocation(String cmsFileLocation) {
        this.cmsFileLocation = cmsFileLocation;
    }

    public String getCmsCreationDate() {
        return cmsCreationDate;
    }

    public void setCmsCreationDate(String cmsCreationDate) {
        this.cmsCreationDate = cmsCreationDate;
    }

    public String getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(String versionStatus) {
        this.versionStatus = versionStatus;
    }

    public String getCmsCreatorUserId() {
        return cmsCreatorUserId;
    }

    public void setCmsCreatorUserId(String cmsCreatorUserId) {
        this.cmsCreatorUserId = cmsCreatorUserId;
    }
    @Transient
    public String getCmsUserName() {
        return cmsUserName;
    }

    public void setCmsUserName(String cmsUserName) {
        this.cmsUserName = cmsUserName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "MisCmsVersion{" +
                "cmsVersionId='" + cmsVersionId + '\'' +
                ", misTypeId='" + misTypeId + '\'' +
                ", misRecordId='" + misRecordId + '\'' +
                ", cmsVersionNo='" + cmsVersionNo + '\'' +
                ", cmsFileLocation='" + cmsFileLocation + '\'' +
                ", cmsCreationDate='" + cmsCreationDate + '\'' +
                ", versionStatus='" + versionStatus + '\'' +
                ", cmsCreatorUserId='" + cmsCreatorUserId + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", cmsUserName='" + cmsUserName + '\'' +
                '}';
    }
}


