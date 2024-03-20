package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "MIS_AUDIT_LOG", schema = "MIS", catalog = "")
public class MisAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "MIS_AUDIT_ID",  nullable = false, length = 16)
    private String misAuditId;
    @Column(name = "MIS_AUDIT_USER", nullable = false, length = 20)
    private String misAuditUser;
    @Column(name = "MIS_AUDIT_OPERATION", nullable = false, length = 32)
    private String misAuditOperation;
    @Column(name = "MIS_AUDIT_TIME", nullable = false, length = 11)
    private Integer misAuditTime;
    @Column(name = "MIS_AUDIT_METHOD", nullable = false, length = 200)
    private String misAuditMethod;
    @Column(name = "MIS_AUDIT_PARAMS", nullable = true, length = 100)
    private String misAuditParams;
    @Column(name = "MIS_AUDIT_IP", nullable = true, length = 64)
    private String misAuditIp;
    @Column(name = "CREATE_TIME", nullable = true, length = 64)
    //@JsonFormat(pattern = "yyyy-MM-dd")
    private String createTime;

    public String getMisAuditId() {
        return misAuditId;
    }

    public void setMisAuditId(String misAuditId) {
        this.misAuditId = misAuditId;
    }

    public String getMisAuditUser() {
        return misAuditUser;
    }

    public void setMisAuditUser(String misAuditUser) {
        this.misAuditUser = misAuditUser;
    }

    public String getMisAuditOperation() {
        return misAuditOperation;
    }

    public void setMisAuditOperation(String misAuditOperation) {
        this.misAuditOperation = misAuditOperation;
    }

    public Integer getMisAuditTime() {
        return misAuditTime;
    }

    public void setMisAuditTime(Integer misAuditTime) {
        this.misAuditTime = misAuditTime;
    }

    public String getMisAuditMethod() {
        return misAuditMethod;
    }

    public void setMisAuditMethod(String misAuditMethod) {
        this.misAuditMethod = misAuditMethod;
    }

    public String getMisAuditParams() {
        return misAuditParams;
    }

    public void setMisAuditParams(String misAuditParams) {
        this.misAuditParams = misAuditParams;
    }

    public String getMisAuditIp() {
        return misAuditIp;
    }

    public void setMisAuditIp(String misAuditIp) {
        this.misAuditIp = misAuditIp;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
