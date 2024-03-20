package com.asl.prd004.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "MIS_SEQUENCE", schema = "MIS", catalog = "")
public class MisSequence {
    private String seqName;
    private Integer curNo;
    private String prefix;
    private Timestamp updateTime;
    private Timestamp createTime;

    @Id
    @Column(name = "SEQ_NAME", nullable = false, length = 100)
    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    @Basic
    @Column(name = "CUR_NO", nullable = true)
    public Integer getCurNo() {
        return curNo;
    }

    public void setCurNo(Integer curNo) {
        this.curNo = curNo;
    }

    @Basic
    @Column(name = "PREFIX", nullable = true, length = 16)
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Basic
    @Column(name = "UPDATE_TIME", nullable = false)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "CREATE_TIME", nullable = true)
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MisSequence sequence = (MisSequence) o;
        return Objects.equals(seqName, sequence.seqName) && Objects.equals(curNo, sequence.curNo) && Objects.equals(prefix, sequence.prefix) && Objects.equals(updateTime, sequence.updateTime) && Objects.equals(createTime, sequence.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seqName, curNo, prefix, updateTime, createTime);
    }
}
