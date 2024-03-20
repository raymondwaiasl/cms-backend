package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "mis_cross_ref", schema = "mis", catalog = "")
public class MisCrossRef {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_cross_ref_id", nullable = false)
    private String misCrossRefId;

    @Column(name = "mis_cross_ref_name", nullable = false)
    private String misCrossRefName;

    @Column(name = "mis_cross_ref_parent_table", nullable = false)
    private String misCrossRefParentTable;

    @Column(name = "mis_cross_ref_parent_key", nullable = false)
    private String misCrossRefParentKey;

    @Column(name = "mis_cross_ref_child_table", nullable = false)
    private String misCrossRefChildTable;

    @Column(name = "mis_cross_ref_child_key", nullable = false)
    private String misCrossRefChildKey;


    public String getMisCrossRefId() {
        return misCrossRefId;
    }

    public void setMisCrossRefId(String misCrossRefId) {
        this.misCrossRefId = misCrossRefId;
    }

    public String getMisCrossRefName() {
        return misCrossRefName;
    }

    public void setMisCrossRefName(String misCrossRefName) {
        this.misCrossRefName = misCrossRefName;
    }

    public String getMisCrossRefParentTable() {
        return misCrossRefParentTable;
    }

    public void setMisCrossRefParentTable(String misCrossRefParentTable) {
        this.misCrossRefParentTable = misCrossRefParentTable;
    }

    public String getMisCrossRefParentKey() {
        return misCrossRefParentKey;
    }

    public void setMisCrossRefParentKey(String misCrossRefParentKey) {
        this.misCrossRefParentKey = misCrossRefParentKey;
    }

    public String getMisCrossRefChildTable() {
        return misCrossRefChildTable;
    }

    public void setMisCrossRefChildTable(String misCrossRefChildTable) {
        this.misCrossRefChildTable = misCrossRefChildTable;
    }

    public String getMisCrossRefChildKey() {
        return misCrossRefChildKey;
    }

    public void setMisCrossRefChildKey(String misCrossRefChildKey) {
        this.misCrossRefChildKey = misCrossRefChildKey;
    }
}
