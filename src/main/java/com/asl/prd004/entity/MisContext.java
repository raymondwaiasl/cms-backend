package com.asl.prd004.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "mis_context", schema = "MIS", catalog = "")
public class MisContext implements Serializable {
    @Id
    @Column(name = "mis_context_id", nullable = false)
    private String misContextId;

    @Column(name = "mis_context_name", nullable = false)
    private String misContextName;

    @Column(name = "mis_context_role_id", nullable = false)
    private String misContextRoleId;

    public String getMisContextId() {
        return misContextId;
    }

    public void setMisContextId(String misContextId) {
        this.misContextId = misContextId;
    }

    public String getMisContextName() {
        return misContextName;
    }

    public void setMisContextName(String misContextName) {
        this.misContextName = misContextName;
    }

    public String getMisContextRoleId() {
        return misContextRoleId;
    }

    public void setMisContextRoleId(String misContextRoleId) {
        this.misContextRoleId = misContextRoleId;
    }
}
