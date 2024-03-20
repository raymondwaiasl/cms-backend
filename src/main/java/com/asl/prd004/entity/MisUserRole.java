package com.asl.prd004.entity;

import javax.persistence.*;

@Entity
@Table(name = "mis_user_role", schema = "MIS", catalog = "")
public class MisUserRole {
    private String misUserId;
    private String misRoleId;

    @Id
    @Column(name = "mis_user_id", nullable = false, length = 16)
    public String getMisUserId() {
        return misUserId;
    }

    public void setMisUserId(String misUserId) {
        this.misUserId = misUserId;
    }

    @Basic
    @Column(name = "mis_role_id", nullable = false, length = 16)
    public String getMisRoleId() {
        return misRoleId;
    }

    public void setMisRoleId(String misRoleId) {
        this.misRoleId = misRoleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisUserRole that = (MisUserRole) o;

        if (misUserId != null ? !misUserId.equals(that.misUserId) : that.misUserId != null) return false;
        if (misRoleId != null ? !misRoleId.equals(that.misRoleId) : that.misRoleId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misUserId != null ? misUserId.hashCode() : 0;
        result = 31 * result + (misRoleId != null ? misRoleId.hashCode() : 0);
        return result;
    }
}
