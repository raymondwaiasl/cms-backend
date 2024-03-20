package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mis_permission", schema = "MIS", catalog = "")
public class MisPermission {

    private String misPermissionId;


    private String misPermissionName;


    private String misPermissionType;


    public List<MisPermissionDetail> details;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_permission_id", nullable = false)
    public String getMisPermissionId() {
        return misPermissionId;
    }

    public void setMisPermissionId(String misPermissionId) {
        this.misPermissionId = misPermissionId;
    }

    @Basic
    @Column(name = "mis_permission_name", nullable = false)
    public String getMisPermissionName() {
        return misPermissionName;
    }

    public void setMisPermissionName(String misPermissionName) {
        this.misPermissionName = misPermissionName;
    }

    @Basic
    @Column(name = "mis_permission_type", nullable = false)
    public String getMisPermissionType() {
        return misPermissionType;
    }

    public void setMisPermissionType(String misPermissionType) {
        this.misPermissionType = misPermissionType;
    }

    @OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL,orphanRemoval = true,mappedBy = "permission")
//    @JoinColumn(name = "mis_permission_id")
    public List<MisPermissionDetail> getDetails() {
        return details;
    }

    public void setDetails(List<MisPermissionDetail> details) {
        this.details = details;
    }
}
