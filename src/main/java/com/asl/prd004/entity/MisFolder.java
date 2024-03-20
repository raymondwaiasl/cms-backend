package com.asl.prd004.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_folder", schema = "MIS", catalog = "")
public class MisFolder {
    private String misFolderId;
    private String misFolderName;
    private String misFolderFullPath;
    private String misFolderParentId;
    private String misPermissionId;
    private String delFlag;
    private String createBy;
    private Timestamp createTime;
    private String updateBy;
    private Timestamp updateTime;

    private boolean isWrite;

    private boolean isDelete;

    private boolean isRead;

    @Id
    @Column(name = "mis_folder_id", nullable = false, length = 16)
    public String getMisFolderId() {
        return misFolderId;
    }

    public void setMisFolderId(String misFolderId) {
        this.misFolderId = misFolderId;
    }

    @Basic
    @Column(name = "mis_folder_name", nullable = false, length = 40)
    public String getMisFolderName() {
        return misFolderName;
    }

    public void setMisFolderName(String misFolderName) {
        this.misFolderName = misFolderName;
    }

    @Basic
    @Column(name = "mis_folder_full_path", nullable = true, length = 255)
    public String getMisFolderFullPath() {
        return misFolderFullPath;
    }

    public void setMisFolderFullPath(String misFolderFullPath) {
        this.misFolderFullPath = misFolderFullPath;
    }

    @Basic
    @Column(name = "mis_folder_parent_id", nullable = true, length = 16)
    public String getMisFolderParentId() {
        return misFolderParentId;
    }

    public void setMisFolderParentId(String misFolderParentId) {
        this.misFolderParentId = misFolderParentId;
    }

    @Basic
    @Column(name = "mis_permission_id", nullable = true, length = 16)
    public String getMisPermissionId() {
        return misPermissionId;
    }

    public void setMisPermissionId(String misPermissionId) {
        this.misPermissionId = misPermissionId;
    }

    @Basic
    @Column(name = "del_flag", nullable = true, length = 1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Basic
    @Column(name = "create_by", nullable = true, length = 64)
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Basic
    @Column(name = "create_time", nullable = true)
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "update_by", nullable = true, length = 64)
    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Basic
    @Column(name = "update_time", nullable = true)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Transient
    public boolean isWrite() {
        return isWrite;
    }

    public void setWrite(boolean write) {
        isWrite = write;
    }
    @Transient
    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Transient
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisFolder misFolder = (MisFolder) o;

        if (misFolderId != null ? !misFolderId.equals(misFolder.misFolderId) : misFolder.misFolderId != null)
            return false;
        if (misFolderName != null ? !misFolderName.equals(misFolder.misFolderName) : misFolder.misFolderName != null)
            return false;
        if (misFolderFullPath != null ? !misFolderFullPath.equals(misFolder.misFolderFullPath) : misFolder.misFolderFullPath != null)
            return false;
        if (misFolderParentId != null ? !misFolderParentId.equals(misFolder.misFolderParentId) : misFolder.misFolderParentId != null)
            return false;
        if (misPermissionId != null ? !misPermissionId.equals(misFolder.misPermissionId) : misFolder.misPermissionId != null)
            return false;
        if (delFlag != null ? !delFlag.equals(misFolder.delFlag) : misFolder.delFlag != null) return false;
        if (createBy != null ? !createBy.equals(misFolder.createBy) : misFolder.createBy != null) return false;
        if (createTime != null ? !createTime.equals(misFolder.createTime) : misFolder.createTime != null) return false;
        if (updateBy != null ? !updateBy.equals(misFolder.updateBy) : misFolder.updateBy != null) return false;
        if (updateTime != null ? !updateTime.equals(misFolder.updateTime) : misFolder.updateTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misFolderId != null ? misFolderId.hashCode() : 0;
        result = 31 * result + (misFolderName != null ? misFolderName.hashCode() : 0);
        result = 31 * result + (misFolderFullPath != null ? misFolderFullPath.hashCode() : 0);
        result = 31 * result + (misFolderParentId != null ? misFolderParentId.hashCode() : 0);
        result = 31 * result + (misPermissionId != null ? misPermissionId.hashCode() : 0);
        result = 31 * result + (delFlag != null ? delFlag.hashCode() : 0);
        result = 31 * result + (createBy != null ? createBy.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateBy != null ? updateBy.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }
}
