package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_property_column_permission", schema = "MIS", catalog = "")
public class MisPropertyColumnPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_property_column_permission_id", nullable = false, length = 16)
    private String misPropertyColumnPermissionId;
    @Basic
    @Column(name = "mis_property_config_detail_column_id", nullable = false, length = 40)
    private String misPropertyConfigDetailColumnId;
    @Basic
    @Column(name = "mis_pd_type", nullable = false, length = 40)
    private String misPdType;
    @Basic
    @Column(name = "mis_pd_performer_id", nullable = false, length = 40)
    private String misPdPerformerId;
    @Basic
    @Column(name = "mis_pd_action", nullable = false, length = 1)
    private String misPdAction;


    public String getMisPropertyColumnPermissionId() {
        return misPropertyColumnPermissionId;
    }

    public void setMisPropertyColumnPermissionId(String misPropertyColumnPermissionId) {
        this.misPropertyColumnPermissionId = misPropertyColumnPermissionId;
    }

    public String getMisPropertyConfigDetailColumnId() {
        return misPropertyConfigDetailColumnId;
    }

    public void setMisPropertyConfigDetailColumnId(String misPropertyConfigDetailColumnId) {
        this.misPropertyConfigDetailColumnId = misPropertyConfigDetailColumnId;
    }

    public String getMisPdType() {
        return misPdType;
    }

    public void setMisPdType(String misPdType) {
        this.misPdType = misPdType;
    }

    public String getMisPdPerformerId() {
        return misPdPerformerId;
    }

    public void setMisPdPerformerId(String misPdPerformerId) {
        this.misPdPerformerId = misPdPerformerId;
    }

    public String getMisPdAction() {
        return misPdAction;
    }

    public void setMisPdAction(String misPdAction) {
        this.misPdAction = misPdAction;
    }
}
