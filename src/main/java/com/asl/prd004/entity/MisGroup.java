package com.asl.prd004.entity;

import javax.persistence.*;
@Entity
@Table(name = "mis_group", schema = "MIS", catalog = "")
public class MisGroup extends BaseModel  {
    private String misGroupId;
    private String misParentGroupId;
    private String misGroupName;
    private String delFlag;
    private String misGroupIcon;
    private String misGroupIsAdmin;
    private String misGroupDefaultFolder;



    @Id
    @Column(name = "mis_group_id", nullable = false, length = 30)
    public String getMisGroupId() {
        return misGroupId;
    }

    public void setMisGroupId(String misGroupId) {
        this.misGroupId = misGroupId;
    }

    @Basic
    @Column(name = "mis_parent_group_id", nullable = true, length = 30)
    public String getMisParentGroupId() {
        return misParentGroupId;
    }

    public void setMisParentGroupId(String misParentGroupId) {
        this.misParentGroupId = misParentGroupId;
    }

    @Basic
    @Column(name = "mis_group_name", nullable = false, length = 30)
    public String getMisGroupName() {
        return misGroupName;
    }

    public void setMisGroupName(String misGroupName) {
        this.misGroupName = misGroupName;
    }

    @Basic
    @Column(name = "del_flag", nullable = true, length = 1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Basic
    @Column(name = "mis_group_icon", nullable = true, length = 1)
    public String getMisGroupIcon() {
        return misGroupIcon;
    }

    public void setMisGroupIcon(String misGroupIcon) {
        this.misGroupIcon = misGroupIcon;
    }

    @Basic
    @Column(name = "mis_group_is_admin", nullable = true, length = 1)
    public String getMisGroupIsAdmin() {
        return misGroupIsAdmin;
    }

    public void setMisGroupIsAdmin(String misGroupIsAdmin) {
        this.misGroupIsAdmin = misGroupIsAdmin;
    }


    @Basic
    @Column(name = "mis_group_default_folder", nullable = true, length = 1)
    public String getMisGroupDefaultFolder() {
        return misGroupDefaultFolder;
    }

    public void setMisGroupDefaultFolder(String misGroupDefaultFolder) {
        this.misGroupDefaultFolder = misGroupDefaultFolder;
    }

    @Override
    public String toString() {
        return "MisGroup{" +
                "misGroupId='" + misGroupId + '\'' +
                ", misParentGroupId='" + misParentGroupId + '\'' +
                ", misGroupName='" + misGroupName + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
