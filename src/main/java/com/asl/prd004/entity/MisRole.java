package com.asl.prd004.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class MisRole extends BaseModel implements Serializable {
    @Id
    @Column(name = "mis_role_id", nullable = false)
    private String misRoleId;

    @Column(name = "mis_role_name", nullable = false)
    private String misRoleName;

    public String getMisRoleId() {
        return misRoleId;
    }

    public void setMisRoleId(String misRoleId) {
        this.misRoleId = misRoleId;
    }

    public String getMisRoleName() {
        return misRoleName;
    }

    public void setMisRoleName(String misRoleName) {
        this.misRoleName = misRoleName;
    }
}
