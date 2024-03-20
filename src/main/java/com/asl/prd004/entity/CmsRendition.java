package com.asl.prd004.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "cms_rendition", schema = "MIS", catalog = "")
public class CmsRendition {
    private String cmsRenditionId;
    private String misTypeId;
    private String misRecordId;
    private String cmsIsPrimary;
    private String cmsFormatId;
    private String cmsRenditionFile;
    private Timestamp cmsRenditionDate;
    private String cmsCreatorUserId;
    private String cmsFileLocation;

    @Id
    @Column(name = "cms_rendition_id", nullable = false, length = 16)
    public String getCmsRenditionId() {
        return cmsRenditionId;
    }

    public void setCmsRenditionId(String cmsRenditionId) {
        this.cmsRenditionId = cmsRenditionId;
    }

    @Basic
    @Column(name = "mis_type_id", nullable = false, length = 16)
    public String getMisTypeId() {
        return misTypeId;
    }

    public void setMisTypeId(String misTypeId) {
        this.misTypeId = misTypeId;
    }

    @Basic
    @Column(name = "mis_record_id", nullable = false, length = 16)
    public String getMisRecordId() {
        return misRecordId;
    }

    public void setMisRecordId(String misRecordId) {
        this.misRecordId = misRecordId;
    }

    @Basic
    @Column(name = "cms_is_primary", nullable = false, length = 1)
    public String getCmsIsPrimary() {
        return cmsIsPrimary;
    }

    public void setCmsIsPrimary(String cmsIsPrimary) {
        this.cmsIsPrimary = cmsIsPrimary;
    }

    @Basic
    @Column(name = "cms_format_id", nullable = false, length = 16)
    public String getCmsFormatId() {
        return cmsFormatId;
    }

    public void setCmsFormatId(String cmsFormatId) {
        this.cmsFormatId = cmsFormatId;
    }

    @Basic
    @Column(name = "cms_rendition_file", nullable = false, length = 255)
    public String getCmsRenditionFile() {
        return cmsRenditionFile;
    }

    public void setCmsRenditionFile(String cmsRenditionFile) {
        this.cmsRenditionFile = cmsRenditionFile;
    }

    @Basic
    @Column(name = "cms_rendition_date", nullable = false)
    public Timestamp getCmsRenditionDate() {
        return cmsRenditionDate;
    }

    public void setCmsRenditionDate(Timestamp cmsRenditionDate) {
        this.cmsRenditionDate = cmsRenditionDate;
    }

    @Basic
    @Column(name = "cms_creator_user_id", nullable = false, length = 16)
    public String getCmsCreatorUserId() {
        return cmsCreatorUserId;
    }

    public void setCmsCreatorUserId(String cmsCreatorUserId) {
        this.cmsCreatorUserId = cmsCreatorUserId;
    }

    @Basic
    @Column(name = "cms_file_location", nullable = false, length = 255)
    public String getCmsFileLocation() {
        return cmsFileLocation;
    }

    public void setCmsFileLocation(String cmsFileLocation) {
        this.cmsFileLocation = cmsFileLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CmsRendition that = (CmsRendition) o;

        if (cmsRenditionId != null ? !cmsRenditionId.equals(that.cmsRenditionId) : that.cmsRenditionId != null)
            return false;
        if (misTypeId != null ? !misTypeId.equals(that.misTypeId) : that.misTypeId != null) return false;
        if (misRecordId != null ? !misRecordId.equals(that.misRecordId) : that.misRecordId != null) return false;
        if (cmsIsPrimary != null ? !cmsIsPrimary.equals(that.cmsIsPrimary) : that.cmsIsPrimary != null) return false;
        if (cmsFormatId != null ? !cmsFormatId.equals(that.cmsFormatId) : that.cmsFormatId != null) return false;
        if (cmsRenditionFile != null ? !cmsRenditionFile.equals(that.cmsRenditionFile) : that.cmsRenditionFile != null)
            return false;
        if (cmsRenditionDate != null ? !cmsRenditionDate.equals(that.cmsRenditionDate) : that.cmsRenditionDate != null)
            return false;
        if (cmsCreatorUserId != null ? !cmsCreatorUserId.equals(that.cmsCreatorUserId) : that.cmsCreatorUserId != null)
            return false;
        if (cmsFileLocation != null ? !cmsFileLocation.equals(that.cmsFileLocation) : that.cmsFileLocation != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cmsRenditionId != null ? cmsRenditionId.hashCode() : 0;
        result = 31 * result + (misTypeId != null ? misTypeId.hashCode() : 0);
        result = 31 * result + (misRecordId != null ? misRecordId.hashCode() : 0);
        result = 31 * result + (cmsIsPrimary != null ? cmsIsPrimary.hashCode() : 0);
        result = 31 * result + (cmsFormatId != null ? cmsFormatId.hashCode() : 0);
        result = 31 * result + (cmsRenditionFile != null ? cmsRenditionFile.hashCode() : 0);
        result = 31 * result + (cmsRenditionDate != null ? cmsRenditionDate.hashCode() : 0);
        result = 31 * result + (cmsCreatorUserId != null ? cmsCreatorUserId.hashCode() : 0);
        result = 31 * result + (cmsFileLocation != null ? cmsFileLocation.hashCode() : 0);
        return result;
    }
}
