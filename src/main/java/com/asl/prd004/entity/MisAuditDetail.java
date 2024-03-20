package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_audit_detail", schema = "MIS", catalog = "")
public class MisAuditDetail {
    private String misAuditDtlId;
    private String misAuditId;
    private String misAuditTypeId;
    private String misAuditDtlAction;
    private String misAuditRecId;
    private String misAuditRechistBfid;
    private String misAuditRechistAfid;
    private String misOperator;
    private Timestamp misOperationTime;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_audit_dtl_id", nullable = false, length = 16)
    public String getMisAuditDtlId() {
        return misAuditDtlId;
    }

    public void setMisAuditDtlId(String misAuditDtlId) {
        this.misAuditDtlId = misAuditDtlId;
    }

    @Basic
    @Column(name = "mis_audit_id", nullable = false, length = 16)
    public String getMisAuditId() {
        return misAuditId;
    }

    public void setMisAuditId(String misAuditId) {
        this.misAuditId = misAuditId;
    }

    @Basic
    @Column(name = "mis_audit_type_id", nullable = false, length = 16)
    public String getMisAuditTypeId() {
        return misAuditTypeId;
    }

    public void setMisAuditTypeId(String misAuditTypeId) {
        this.misAuditTypeId = misAuditTypeId;
    }

    @Basic
    @Column(name = "mis_audit_dtl_action", nullable = false, length = 10)
    public String getMisAuditDtlAction() {
        return misAuditDtlAction;
    }

    public void setMisAuditDtlAction(String misAuditDtlAction) {
        this.misAuditDtlAction = misAuditDtlAction;
    }

    @Basic
    @Column(name = "mis_audit_rec_id", nullable = false, length = 16)
    public String getMisAuditRecId() {
        return misAuditRecId;
    }

    public void setMisAuditRecId(String misAuditRecId) {
        this.misAuditRecId = misAuditRecId;
    }

    @Basic
    @Column(name = "mis_audit_rechist_bfid", nullable = false, length = 16)
    public String getMisAuditRechistBfid() {
        return misAuditRechistBfid;
    }

    public void setMisAuditRechistBfid(String misAuditRechistBfid) {
        this.misAuditRechistBfid = misAuditRechistBfid;
    }

    @Basic
    @Column(name = "mis_audit_rechist_afid", nullable = false, length = 16)
    public String getMisAuditRechistAfid() {
        return misAuditRechistAfid;
    }

    public void setMisAuditRechistAfid(String misAuditRechistAfid) {
        this.misAuditRechistAfid = misAuditRechistAfid;
    }

    @Basic
    @Column(name = "mis_operator", nullable = false, length = 16)
    public String getMisOperator() {
        return misOperator;
    }

    public void setMisOperator(String misOperator) {
        this.misOperator = misOperator;
    }

    @Basic
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone= "GMT+8")
    @Column(name = "mis_operation_time", nullable = false)
    public Timestamp getMisOperationTime() {
        return misOperationTime;
    }

    public void setMisOperationTime(Timestamp misOperationTime) {
        this.misOperationTime = misOperationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisAuditDetail that = (MisAuditDetail) o;

        if (misAuditDtlId != null ? !misAuditDtlId.equals(that.misAuditDtlId) : that.misAuditDtlId != null)
            return false;
        if (misAuditId != null ? !misAuditId.equals(that.misAuditId) : that.misAuditId != null) return false;
        if (misAuditTypeId != null ? !misAuditTypeId.equals(that.misAuditTypeId) : that.misAuditTypeId != null)
            return false;
        if (misAuditDtlAction != null ? !misAuditDtlAction.equals(that.misAuditDtlAction) : that.misAuditDtlAction != null)
            return false;
        if (misAuditRecId != null ? !misAuditRecId.equals(that.misAuditRecId) : that.misAuditRecId != null)
            return false;
        if (misAuditRechistBfid != null ? !misAuditRechistBfid.equals(that.misAuditRechistBfid) : that.misAuditRechistBfid != null)
            return false;
        if (misAuditRechistAfid != null ? !misAuditRechistAfid.equals(that.misAuditRechistAfid) : that.misAuditRechistAfid != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misAuditDtlId != null ? misAuditDtlId.hashCode() : 0;
        result = 31 * result + (misAuditId != null ? misAuditId.hashCode() : 0);
        result = 31 * result + (misAuditTypeId != null ? misAuditTypeId.hashCode() : 0);
        result = 31 * result + (misAuditDtlAction != null ? misAuditDtlAction.hashCode() : 0);
        result = 31 * result + (misAuditRecId != null ? misAuditRecId.hashCode() : 0);
        result = 31 * result + (misAuditRechistBfid != null ? misAuditRechistBfid.hashCode() : 0);
        result = 31 * result + (misAuditRechistAfid != null ? misAuditRechistAfid.hashCode() : 0);
        return result;
    }
}
