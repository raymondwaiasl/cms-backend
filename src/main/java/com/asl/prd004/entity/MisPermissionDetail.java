package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "mis_permission_detail", schema = "MIS", catalog = "")
public class MisPermissionDetail  {


    private String misPdId;


    private String misPermissionId;


    private String misPdType;


    private String misPdPerformerId;


    private String misPdRight;



    public MisPermission permission;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_pd_id", nullable = false)
    public String getMisPdId() {
        return misPdId;
    }

    public void setMisPdId(String misPdId) {
        this.misPdId = misPdId;
    }

    @Basic
    @Column(name = "mis_permission_id", nullable = false)
    public String getMisPermissionId() {
        return misPermissionId;
    }

    public void setMisPermissionId(String misPermissionId) {
        this.misPermissionId = misPermissionId;
    }

    @Basic
    @Column(name = "mis_pd_type", nullable = false)
    public String getMisPdType() {
        return misPdType;
    }

    public void setMisPdType(String misPdType) {
        this.misPdType = misPdType;
    }

    @Basic
    @Column(name = "mis_pd_performer_id", nullable = false)
    public String getMisPdPerformerId() {
        return misPdPerformerId;
    }

    public void setMisPdPerformerId(String misPdPerformerId) {
        this.misPdPerformerId = misPdPerformerId;
    }

    @Basic
    @Column(name = "mis_pd_right", nullable = false)
    public String getMisPdRight() {
        return misPdRight;
    }

    public void setMisPdRight(String misPdRight) {
        this.misPdRight = misPdRight;
    }


    @ManyToOne
    @JoinColumn(name="mis_permission_id" , insertable = false,updatable = false)
    @JsonIgnore
    public MisPermission getPermission() {
        return permission;
    }

    public void setPermission(MisPermission permission) {
        this.permission = permission;
    }
}
