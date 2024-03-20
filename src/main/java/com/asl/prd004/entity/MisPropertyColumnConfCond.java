package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_property_column_conf_cond", schema = "MIS", catalog = "")
public class MisPropertyColumnConfCond {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_property_col_conf_cond_id", nullable = false, length = 16)
    private String misPropertyColConfCondId;

    @Basic
    @Column(name = "mis_property_col_conf_id", nullable = false, length = 40)
    private String misPropertyColConfId;

    @Basic
    @Column(name = "mis_property_col_conf_type", nullable = false, length = 40)
    private String misPropertyColConfType;

    @Basic
    @Column(name = "mis_property_col_conf_cond_key", nullable = false, length = 40)
    private String misPropertyColConfCondKey;

    @Basic
    @Column(name = "mis_property_col_conf_condition", nullable = false, length = 40)
    private String misPropertyColConfCondition;

    @Basic
    @Column(name = "mis_property_col_conf_cond_value", nullable = false, length = 40)
    private String misPropertyColConfCondValue;

    @Basic
    @Column(name = "mis_property_col_conf_cond_join", nullable = false, length = 40)
    private String misPropertyColConfCondJoin;

    @ManyToOne
    @JoinColumn(name="mis_property_col_conf_id", insertable = false, updatable = false)
    @JsonIgnore
    private MisPropertyColumnConf propertyColumnConfConds;

    public MisPropertyColumnConf getPropertyColumnConfConds() {
        return propertyColumnConfConds;
    }

    public void setPropertyColumnConfConds(MisPropertyColumnConf propertyColumnConfConds) {
        this.propertyColumnConfConds = propertyColumnConfConds;
    }

    public String getMisPropertyColConfCondId() {
        return misPropertyColConfCondId;
    }

    public void setMisPropertyColConfCondId(String misPropertyColConfCondId) {
        this.misPropertyColConfCondId = misPropertyColConfCondId;
    }

    public String getMisPropertyColConfId() {
        return misPropertyColConfId;
    }

    public void setMisPropertyColConfId(String misPropertyColConfId) {
        this.misPropertyColConfId = misPropertyColConfId;
    }

    public String getMisPropertyColConfCondKey() {
        return misPropertyColConfCondKey;
    }

    public void setMisPropertyColConfCondKey(String misPropertyColConfCondKey) {
        this.misPropertyColConfCondKey = misPropertyColConfCondKey;
    }

    public String getMisPropertyColConfCondition() {
        return misPropertyColConfCondition;
    }

    public void setMisPropertyColConfCondition(String misPropertyColConfCondition) {
        this.misPropertyColConfCondition = misPropertyColConfCondition;
    }

    public String getMisPropertyColConfCondValue() {
        return misPropertyColConfCondValue;
    }

    public void setMisPropertyColConfCondValue(String misPropertyColConfCondValue) {
        this.misPropertyColConfCondValue = misPropertyColConfCondValue;
    }

    public String getMisPropertyColConfCondJoin() {
        return misPropertyColConfCondJoin;
    }

    public void setMisPropertyColConfCondJoin(String misPropertyColConfCondJoin) {
        this.misPropertyColConfCondJoin = misPropertyColConfCondJoin;
    }

    public String getMisPropertyColConfType() {
        return misPropertyColConfType;
    }

    public void setMisPropertyColConfType(String misPropertyColConfType) {
        this.misPropertyColConfType = misPropertyColConfType;
    }
}
