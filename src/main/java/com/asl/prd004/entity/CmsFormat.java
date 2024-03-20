package com.asl.prd004.entity;

import javax.persistence.*;

@Entity
@Table(name = "cms_format", schema = "MIS", catalog = "")
public class CmsFormat {
    private String cmsFormatId;
    private String cmsFormatDosExt;
    private String cmsFormat;

    @Id
    @Column(name = "cms_format_id", nullable = false, length = 16)
    public String getCmsFormatId() {
        return cmsFormatId;
    }

    public void setCmsFormatId(String cmsFormatId) {
        this.cmsFormatId = cmsFormatId;
    }

    @Basic
    @Column(name = "cms_format_dos_ext", nullable = false, length = 8)
    public String getCmsFormatDosExt() {
        return cmsFormatDosExt;
    }

    public void setCmsFormatDosExt(String cmsFormatDosExt) {
        this.cmsFormatDosExt = cmsFormatDosExt;
    }

    @Basic
    @Column(name = "cms_format", nullable = false, length = 255)
    public String getCmsFormat() {
        return cmsFormat;
    }

    public void setCmsFormat(String cmsFormat) {
        this.cmsFormat = cmsFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CmsFormat cmsFormat1 = (CmsFormat) o;

        if (cmsFormatId != null ? !cmsFormatId.equals(cmsFormat1.cmsFormatId) : cmsFormat1.cmsFormatId != null)
            return false;
        if (cmsFormatDosExt != null ? !cmsFormatDosExt.equals(cmsFormat1.cmsFormatDosExt) : cmsFormat1.cmsFormatDosExt != null)
            return false;
        if (cmsFormat != null ? !cmsFormat.equals(cmsFormat1.cmsFormat) : cmsFormat1.cmsFormat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cmsFormatId != null ? cmsFormatId.hashCode() : 0;
        result = 31 * result + (cmsFormatDosExt != null ? cmsFormatDosExt.hashCode() : 0);
        result = 31 * result + (cmsFormat != null ? cmsFormat.hashCode() : 0);
        return result;
    }
}
