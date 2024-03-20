package com.asl.prd004.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mis_gp_type_permission", schema = "MIS", catalog = "")
public class MisGpTypePermission extends BaseModel {
    @Id
    @Column(name = "mis_gp_type_permission_id",  nullable = false, length = 30)
    private String misGpTypePermissionId;
    @Column(name = "mis_gp_id",  nullable = false, length = 20)
    private String misGpId;
    @Column(name = "mis_type_id",  nullable = false, length = 20)
    private String misTypeId;
    @Column(name = "access",   length = 1)
    private String access;
    @Column(name = "direct_create",   length = 1)
    private String directCreate;
    @Column(name = "direct_edit",   length = 1)
    private String directEdit;
    @Column(name = "direct_delete",   length = 1)
    private String directDelete;

    public String getMisGpTypePermissionId() {
        return misGpTypePermissionId;
    }

    public void setMisGpTypePermissionId(String misGpTypePermissionId) {
        this.misGpTypePermissionId = misGpTypePermissionId;
    }

    public String getMisGpId() {
        return misGpId;
    }

    public void setMisGpId(String misGpId) {
        this.misGpId = misGpId;
    }

    public String getMisTypeId() {
        return misTypeId;
    }

    public void setMisTypeId(String misTypeId) {
        this.misTypeId = misTypeId;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getDirectCreate() {
        return directCreate;
    }

    public void setDirectCreate(String directCreate) {
        this.directCreate = directCreate;
    }

    public String getDirectEdit() {
        return directEdit;
    }

    public void setDirectEdit(String directEdit) {
        this.directEdit = directEdit;
    }

    public String getDirectDelete() {
        return directDelete;
    }

    public void setDirectDelete(String directDelete) {
        this.directDelete = directDelete;
    }
}
