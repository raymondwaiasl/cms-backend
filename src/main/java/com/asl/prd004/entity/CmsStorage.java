package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cms_storage", schema = "MIS", catalog = "")
public class CmsStorage {
    private String cmsStorageId;
    private String cmsStorageName;
    private String cmsStoragePath;
    private String cmsStorageEncrypt;
    private BigDecimal cmsStorageSpace;
    private BigDecimal cmsStorageUsed;
    private BigDecimal cmsStorageFree;
    private BigDecimal cmsStorageThreshold;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "cms_storage_id", nullable = false, length = 16)
    public String getCmsStorageId() {
        return cmsStorageId;
    }

    public void setCmsStorageId(String cmsStorageId) {
        this.cmsStorageId = cmsStorageId;
    }

    @Basic
    @Column(name = "cms_storage_name", nullable = false, length = 255)
    public String getCmsStorageName() {
        return cmsStorageName;
    }

    public void setCmsStorageName(String cmsStorageName) {
        this.cmsStorageName = cmsStorageName;
    }

    @Basic
    @Column(name = "cms_storage_path", nullable = false, length = 255)
    public String getCmsStoragePath() {
        return cmsStoragePath;
    }

    public void setCmsStoragePath(String cmsStoragePath) {
        this.cmsStoragePath = cmsStoragePath;
    }

    @Basic
    @Column(name = "cms_storage_encrypt", nullable = false, length = 1)
    public String getCmsStorageEncrypt() {
        return cmsStorageEncrypt;
    }

    public void setCmsStorageEncrypt(String cmsStorageEncrypt) {
        this.cmsStorageEncrypt = cmsStorageEncrypt;
    }

    @Basic
    @Column(name = "cms_storage_space", nullable = true, precision = 2)
    public BigDecimal getCmsStorageSpace() {
        return cmsStorageSpace;
    }

    public void setCmsStorageSpace(BigDecimal cmsStorageSpace) {
        this.cmsStorageSpace = cmsStorageSpace;
    }

    @Basic
    @Column(name = "cms_storage_used", nullable = true, precision = 2)
    public BigDecimal getCmsStorageUsed() {
        return cmsStorageUsed;
    }

    public void setCmsStorageUsed(BigDecimal cmsStorageUsed) {
        this.cmsStorageUsed = cmsStorageUsed;
    }

    @Basic
    @Column(name = "cms_storage_free", nullable = true, precision = 2)
    public BigDecimal getCmsStorageFree() {
        return cmsStorageFree;
    }

    public void setCmsStorageFree(BigDecimal cmsStorageFree) {
        this.cmsStorageFree = cmsStorageFree;
    }

    @Basic
    @Column(name = "cms_storage_threshold", nullable = true, precision = 2)
    public BigDecimal getCmsStorageThreshold() {
        return cmsStorageThreshold;
    }

    public void setCmsStorageThreshold(BigDecimal cmsStorageThreshold) {
        this.cmsStorageThreshold = cmsStorageThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CmsStorage that = (CmsStorage) o;

        if (cmsStorageId != null ? !cmsStorageId.equals(that.cmsStorageId) : that.cmsStorageId != null) return false;
        if (cmsStorageName != null ? !cmsStorageName.equals(that.cmsStorageName) : that.cmsStorageName != null)
            return false;
        if (cmsStoragePath != null ? !cmsStoragePath.equals(that.cmsStoragePath) : that.cmsStoragePath != null)
            return false;
        if (cmsStorageEncrypt != null ? !cmsStorageEncrypt.equals(that.cmsStorageEncrypt) : that.cmsStorageEncrypt != null)
            return false;
        if (cmsStorageSpace != null ? !cmsStorageSpace.equals(that.cmsStorageSpace) : that.cmsStorageSpace != null)
            return false;
        if (cmsStorageUsed != null ? !cmsStorageUsed.equals(that.cmsStorageUsed) : that.cmsStorageUsed != null)
            return false;
        if (cmsStorageFree != null ? !cmsStorageFree.equals(that.cmsStorageFree) : that.cmsStorageFree != null)
            return false;
        if (cmsStorageThreshold != null ? !cmsStorageThreshold.equals(that.cmsStorageThreshold) : that.cmsStorageThreshold != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cmsStorageId != null ? cmsStorageId.hashCode() : 0;
        result = 31 * result + (cmsStorageName != null ? cmsStorageName.hashCode() : 0);
        result = 31 * result + (cmsStoragePath != null ? cmsStoragePath.hashCode() : 0);
        result = 31 * result + (cmsStorageEncrypt != null ? cmsStorageEncrypt.hashCode() : 0);
        result = 31 * result + (cmsStorageSpace != null ? cmsStorageSpace.hashCode() : 0);
        result = 31 * result + (cmsStorageUsed != null ? cmsStorageUsed.hashCode() : 0);
        result = 31 * result + (cmsStorageFree != null ? cmsStorageFree.hashCode() : 0);
        result = 31 * result + (cmsStorageThreshold != null ? cmsStorageThreshold.hashCode() : 0);
        return result;
    }
}
