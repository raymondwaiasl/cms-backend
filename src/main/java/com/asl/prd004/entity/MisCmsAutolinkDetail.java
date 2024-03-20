package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2022/12/1 10:06
 */
@Entity
@Table(name = "MIS_CMS_AUTOLINK_DETAIL", schema = "MIS", catalog = "")
public class MisCmsAutolinkDetail extends BaseModel{
    @Id
    @Column(name = "CMS_AUTOLINK_DETAIL_ID",  nullable = false, length = 20)
    private String cmsAutolinkDetailId;
    @Column(name = "CMS_AUTOLINK_ID",  nullable = false, length = 20)
    private String cmsAutolinkId;
    @Column(name = "CMS_FOLDER_LEVEL",  nullable = false, length = 1)
    private Integer cmsFolderLevel;
    @Column(name = "MIS_COLUMN_ID",  nullable = false, length = 20)
    private String misColumnId;

    @ManyToOne
    @JoinColumn(name="CMS_AUTOLINK_ID", insertable = false,updatable = false)
    @JsonIgnore
    private MisCmsAutolink autolink;

    public String getCmsAutolinkDetailId() {
        return cmsAutolinkDetailId;
    }

    public void setCmsAutolinkDetailId(String cmsAutolinkDetailId) {
        this.cmsAutolinkDetailId = cmsAutolinkDetailId;
    }

    public String getCmsAutolinkId() {
        return cmsAutolinkId;
    }

    public void setCmsAutolinkId(String cmsAutolinkId) {
        this.cmsAutolinkId = cmsAutolinkId;
    }

    public Integer getCmsFolderLevel() {
        return cmsFolderLevel;
    }

    public void setCmsFolderLevel(Integer cmsFolderLevel) {
        this.cmsFolderLevel = cmsFolderLevel;
    }

    public String getMisColumnId() {
        return misColumnId;
    }

    public void setMisColumnId(String misColumnId) {
        this.misColumnId = misColumnId;
    }

    public MisCmsAutolink getAutolink() {
        return autolink;
    }

    public void setAutolink(MisCmsAutolink autolink) {
        this.autolink = autolink;
    }

    @Override
    public String toString() {
        return "MisCmsAutolinkDetail{" +
                "cmsAutolinkDetailId='" + cmsAutolinkDetailId + '\'' +
                ", cmsAutolinkId='" + cmsAutolinkId + '\'' +
                ", cmsFolderLevel=" + cmsFolderLevel +
                ", misColumnId='" + misColumnId + '\'' +
                '}';
    }
}


