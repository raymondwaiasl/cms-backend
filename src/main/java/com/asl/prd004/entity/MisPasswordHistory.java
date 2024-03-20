package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_password_history", schema = "MIS", catalog = "")
public class MisPasswordHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_password_history_id", nullable = false)
    private String misPasswordHistoryId;
    @Basic
    @Column(name = "mis_user_id", nullable = false, length = 16)
    private String misUserId;
    @Basic
    @Column(name = "password", nullable = true, length = 100)
    private String password;
    @Basic
    @Column(name = "create_time", nullable = true)
    private Timestamp createTime;


    public String getMisPasswordHistoryId() {
        return misPasswordHistoryId;
    }

    public void setMisPasswordHistoryId(String misPasswordHistoryId) {
        this.misPasswordHistoryId = misPasswordHistoryId;
    }

    public String getMisUserId() {
        return misUserId;
    }

    public void setMisUserId(String misUserId) {
        this.misUserId = misUserId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
