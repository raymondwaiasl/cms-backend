package com.asl.prd004.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_property_config", schema = "MIS", catalog = "")
public class MisPropertyConfig {
    private String misPropertyId;
    private String misPropertyName;
    private String misPropertyTableId;
    private Timestamp creationDate;
    private String creatorUserId;
    private Timestamp updatedDate;
    private String updatedUserId;
    private String misIsLock;
    private String misLockedBy;

    private String misLockedLevel;

    @Id
    @Column(name = "mis_property_id", nullable = false, length = 16)
    public String getMisPropertyId() {
        return misPropertyId;
    }

    public void setMisPropertyId(String misPropertyId) {
        this.misPropertyId = misPropertyId;
    }

    @Basic
    @Column(name = "mis_property_name", nullable = false, length = 40)
    public String getMisPropertyName() {
        return misPropertyName;
    }

    public void setMisPropertyName(String misPropertyName) {
        this.misPropertyName = misPropertyName;
    }

    @Basic
    @Column(name = "mis_property_table_id", nullable = false, length = 40)
    public String getMisPropertyTableId() {
        return misPropertyTableId;
    }

    public void setMisPropertyTableId(String misPropertyTableId) {
        this.misPropertyTableId = misPropertyTableId;
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
    @Basic
    @Column(name = "mis_is_lock", nullable = false, length = 1)
    public String getMisIsLock() {
        return misIsLock;
    }

    public void setMisIsLock(String misIsLock) {
        this.misIsLock = misIsLock;
    }
    @Basic
    @Column(name = "mis_lock_by",  length =20)
    public String getMisLockedBy() {
        return misLockedBy;
    }

    public void setMisLockedBy(String misLockedBy) {
        this.misLockedBy = misLockedBy;
    }
    @Basic
    @Column(name = "mis_lock_level",  length = 2)
    public String getMisLockedLevel() {
        return misLockedLevel;
    }

    public void setMisLockedLevel(String misLockedLevel) {
        this.misLockedLevel = misLockedLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisPropertyConfig that = (MisPropertyConfig) o;

        if (misPropertyId != null ? !misPropertyId.equals(that.misPropertyId) : that.misPropertyId != null)
            return false;
        if (misPropertyName != null ? !misPropertyName.equals(that.misPropertyName) : that.misPropertyName != null)
            return false;
        if (misPropertyTableId != null ? !misPropertyTableId.equals(that.misPropertyTableId) : that.misPropertyTableId != null)
            return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        if (creatorUserId != null ? !creatorUserId.equals(that.creatorUserId) : that.creatorUserId != null)
            return false;
        if (updatedDate != null ? !updatedDate.equals(that.updatedDate) : that.updatedDate != null) return false;
        if (updatedUserId != null ? !updatedUserId.equals(that.updatedUserId) : that.updatedUserId != null)
            return false;
        if (misIsLock != null ? !misIsLock.equals(that.misIsLock) : that.misIsLock != null)
            return false;
        if (misLockedBy != null ? !misLockedBy.equals(that.misLockedBy) : that.misLockedBy != null)
            return false;

        if (misLockedLevel != null ? !misLockedLevel.equals(that.misLockedLevel) : that.misLockedLevel != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misPropertyId != null ? misPropertyId.hashCode() : 0;
        result = 31 * result + (misPropertyName != null ? misPropertyName.hashCode() : 0);
        result = 31 * result + (misPropertyTableId != null ? misPropertyTableId.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (creatorUserId != null ? creatorUserId.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + (updatedUserId != null ? updatedUserId.hashCode() : 0);
        result = 31 * result + (misIsLock != null ? misIsLock.hashCode() : 0);
        result = 31 * result + (misLockedBy != null ? misLockedBy.hashCode() : 0);
        result = 31 * result + (misLockedLevel != null ? misLockedLevel.hashCode() : 0);
        return result;
    }
}
