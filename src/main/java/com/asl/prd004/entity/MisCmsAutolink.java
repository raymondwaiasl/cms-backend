package com.asl.prd004.entity;

import javax.persistence.*;
import java.util.List;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2022/12/1 10:06
 */
@Entity
@Table(name = "MIS_CMS_AUTOLINK", schema = "MIS", catalog = "")
public class MisCmsAutolink extends BaseModel{
    @Id
    @Column(name = "CMS_AUTOLINK_ID",  nullable = false, length = 20)
    private String cmsAutolinkId;
    @Column(name = "MIS_TYPE_ID",  nullable = false, length = 20)
    private String misTypeId;
    @Column(name = "MIS_FOLDER_ID",  nullable = false, length = 20)
    private String misFolderId;
    @Column(name = "CMS_IS_CREATE_FOLDER",  nullable = false, length = 20)
    private String cmsIsCreateFolder;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "autolink")
    private List<MisCmsAutolinkDetail> details;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "autolink")
    private List<MisCmsAutolinkCondition> conditions;

    public String getCmsAutolinkId() {
        return cmsAutolinkId;
    }

    public void setCmsAutolinkId(String cmsAutolinkId) {
        this.cmsAutolinkId = cmsAutolinkId;
    }

    public String getMisTypeId() {
        return misTypeId;
    }

    public void setMisTypeId(String misTypeId) {
        this.misTypeId = misTypeId;
    }

    public String getMisFolderId() {
        return misFolderId;
    }

    public void setMisFolderId(String misFolderId) {
        this.misFolderId = misFolderId;
    }

    public String getCmsIsCreateFolder() {
        return cmsIsCreateFolder;
    }

    public void setCmsIsCreateFolder(String cmsIsCreateFolder) {
        this.cmsIsCreateFolder = cmsIsCreateFolder;
    }

    public List<MisCmsAutolinkDetail> getDetails() {
        return details;
    }

    public void setDetails(List<MisCmsAutolinkDetail> details) {
        this.details = details;
    }

    public List<MisCmsAutolinkCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<MisCmsAutolinkCondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return "MisCmsAutolink{" +
                "cmsAutolinkId='" + cmsAutolinkId + '\'' +
                ", misTypeId='" + misTypeId + '\'' +
                ", misFolderId='" + misFolderId + '\'' +
                ", cmsIsCreateFolder='" + cmsIsCreateFolder + '\'' +
                '}';
    }
}


