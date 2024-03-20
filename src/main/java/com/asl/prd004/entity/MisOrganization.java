package com.asl.prd004.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "mis_organization", schema = "MIS", catalog = "")
public class MisOrganization {
    private String misOrganizationId;
    private String misOrganizationName;
    private String createBy;
    private Timestamp createTime;
    private String updateBy;
    private Timestamp updateTime;

    @Id
    @Column(name = "mis_organization_id", nullable = false, length = 16)
    public String getMisOrganizationId() {
        return misOrganizationId;
    }

    public void setMisOrganizationId(String misOrganizationId) {
        this.misOrganizationId = misOrganizationId;
    }

    @Basic
    @Column(name = "mis_organization_name", nullable = false, length = 100)
    public String getMisOrganizationName() {
        return misOrganizationName;
    }

    public void setMisOrganizationName(String misOrganizationName) {
        this.misOrganizationName = misOrganizationName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MisOrganization that = (MisOrganization) o;
        return Objects.equals(misOrganizationId, that.misOrganizationId) && Objects.equals(misOrganizationName, that.misOrganizationName) && Objects.equals(createBy, that.createBy) && Objects.equals(createTime, that.createTime) && Objects.equals(updateBy, that.updateBy) && Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(misOrganizationId, misOrganizationName, createBy, createTime, updateBy, updateTime);
    }
}
