package com.asl.prd004.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_property_config_detail", schema = "MIS", catalog = "")
public class MisPropertyConfigDetail {
    private String misPropertyConfigDetailId;
    private String misPropertyId;
    private String misPropertyConfigDetailColumnId;
    private Timestamp creationDate;
    private String creatorUserId;
    private Timestamp updatedDate;
    private String updatedUserId;
    private Integer rowSize;
    private Integer colSize;
    private String misPropertySectionId;
    private String misIsLock;
    private String misLockedBy;
    private String misColumnId;


    public Integer getRowSize() {
        return rowSize;
    }

    public void setRowSize(Integer rowSize) {
        this.rowSize = rowSize;
    }

    public Integer getColSize() {
        return colSize;
    }

    public void setColSize(Integer colSize) {
        this.colSize = colSize;
    }



    @Id
    @Column(name = "mis_property_config_detail_id", nullable = false, length = 16)
    public String getMisPropertyConfigDetailId() {
        return misPropertyConfigDetailId;
    }

    public void setMisPropertyConfigDetailId(String misPropertyConfigDetailId) {
        this.misPropertyConfigDetailId = misPropertyConfigDetailId;
    }

    @Basic
    @Column(name = "mis_property_id", nullable = false, length = 16)
    public String getMisPropertyId() {
        return misPropertyId;
    }

    public void setMisPropertyId(String misPropertyId) {
        this.misPropertyId = misPropertyId;
    }

    @Basic
    @Column(name = "mis_property_config_detail_column_id", nullable = false, length = 16)
    public String getMisPropertyConfigDetailColumnId() {
        return misPropertyConfigDetailColumnId;
    }

    public void setMisPropertyConfigDetailColumnId(String misPropertyConfigDetailColumnId) {
        this.misPropertyConfigDetailColumnId = misPropertyConfigDetailColumnId;
    }

    @Basic
    @Column(name = "creation_date", nullable = false)
    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    @Basic
    @Column(name = "creator_user_id", nullable = false, length = 16)
    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    @Basic
    @Column(name = "updated_date", nullable = false)
    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Basic
    @Column(name = "updated_user_id", nullable = false, length = 16)
    public String getUpdatedUserId() {
        return updatedUserId;
    }

    public void setUpdatedUserId(String updatedUserId) {
        this.updatedUserId = updatedUserId;
    }

    public String getMisPropertySectionId() {
        return misPropertySectionId;
    }

    public void setMisPropertySectionId(String misPropertySectionId) {
        this.misPropertySectionId = misPropertySectionId;
    }

    public String getMisIsLock() {
        return misIsLock;
    }

    public void setMisIsLock(String misIsLock) {
        this.misIsLock = misIsLock;
    }

    public String getMisLockedBy() {
        return misLockedBy;
    }

    public void setMisLockedBy(String misLockedBy) {
        this.misLockedBy = misLockedBy;
    }

    public String getMisColumnId() {
        return misColumnId;
    }

    public void setMisColumnId(String misColumnId) {
        this.misColumnId = misColumnId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisPropertyConfigDetail that = (MisPropertyConfigDetail) o;

        if (misPropertyConfigDetailId != null ? !misPropertyConfigDetailId.equals(that.misPropertyConfigDetailId) : that.misPropertyConfigDetailId != null)
            return false;
        if (misPropertyId != null ? !misPropertyId.equals(that.misPropertyId) : that.misPropertyId != null)
            return false;
        if (misPropertyConfigDetailColumnId != null ? !misPropertyConfigDetailColumnId.equals(that.misPropertyConfigDetailColumnId) : that.misPropertyConfigDetailColumnId != null)
            return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        if (creatorUserId != null ? !creatorUserId.equals(that.creatorUserId) : that.creatorUserId != null)
            return false;
        if (updatedDate != null ? !updatedDate.equals(that.updatedDate) : that.updatedDate != null) return false;
        if (updatedUserId != null ? !updatedUserId.equals(that.updatedUserId) : that.updatedUserId != null)
            return false;
        if (misPropertySectionId != null ? !misPropertySectionId.equals(that.misPropertySectionId) : that.misPropertySectionId != null)
            return false;
        if (misIsLock != null ? !misIsLock.equals(that.misIsLock) : that.misIsLock != null)
            return false;
        if (misLockedBy != null ? !misLockedBy.equals(that.misLockedBy) : that.misLockedBy != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misPropertyConfigDetailId != null ? misPropertyConfigDetailId.hashCode() : 0;
        result = 31 * result + (misPropertyId != null ? misPropertyId.hashCode() : 0);
        result = 31 * result + (misPropertyConfigDetailColumnId != null ? misPropertyConfigDetailColumnId.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (creatorUserId != null ? creatorUserId.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + (updatedUserId != null ? updatedUserId.hashCode() : 0);
        result = 31 * result + (misPropertySectionId != null ? misPropertySectionId.hashCode() : 0);
        result = 31 * result + (misIsLock != null ? misIsLock.hashCode() : 0);
        result = 31 * result + (misLockedBy != null ? misLockedBy.hashCode() : 0);
        return result;
    }
}
