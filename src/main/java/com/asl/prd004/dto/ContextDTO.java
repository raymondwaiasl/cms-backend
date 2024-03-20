package com.asl.prd004.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class ContextDTO implements Serializable {

    private String misContextId;

    private String misContextName;

    private String misRoleName;

    public ContextDTO() {
    }

    public ContextDTO(String misContextId,String misContextName, String misRoleName) {
        this.misContextId = misContextId;
        this.misContextName = misContextName;
        this.misRoleName = misRoleName;
    }

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

    public String getMisRoleName() {
        return misRoleName;
    }

    public void setMisRoleName(String misRoleName) {
        this.misRoleName = misRoleName;
    }
}
