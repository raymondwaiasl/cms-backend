package com.asl.prd004.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "mis_context_detail", schema = "MIS", catalog = "")
public class MisContextDetail implements Serializable {
    @Id
    @Column(name = "mis_context_detail_id", nullable = false)
    private String misContextDetailId;

    @Column(name = "mis_context_id", nullable = false)
    private String misContextId;

    @Column(name = "mis_context_ws_id", nullable = false)
    private String misContextWsId;

    public String getMisContextDetailId() {
        return misContextDetailId;
    }

    public void setMisContextDetailId(String misContextDetailId) {
        this.misContextDetailId = misContextDetailId;
    }

    public String getMisContextId() {
        return misContextId;
    }

    public void setMisContextId(String misContextId) {
        this.misContextId = misContextId;
    }

    public String getMisContextWsId() {
        return misContextWsId;
    }

    public void setMisContextWsId(String misContextWsId) {
        this.misContextWsId = misContextWsId;
    }
}
