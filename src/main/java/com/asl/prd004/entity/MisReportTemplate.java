package com.asl.prd004.entity;

import javax.persistence.*;

@Entity
@Table(name = "mis_report_template", schema = "MIS", catalog = "")
public class MisReportTemplate {
    private String misReportTempId;
    private String misReportTempName;
    private String misReportTempPath1;
    private String misReportTempPath2;
    private String misPermissionId;

    @Id
    @Column(name = "mis_report_temp_id", nullable = false, length = 16)
    public String getMisReportTempId() {
        return misReportTempId;
    }

    public void setMisReportTempId(String misReportTempId) {
        this.misReportTempId = misReportTempId;
    }

    @Basic
    @Column(name = "mis_report_temp_name", nullable = false, length = 255)
    public String getMisReportTempName() {
        return misReportTempName;
    }

    public void setMisReportTempName(String misReportTempName) {
        this.misReportTempName = misReportTempName;
    }

    @Basic
    @Column(name = "mis_report_temp_path1", nullable = false, length = 255)
    public String getMisReportTempPath1() {
        return misReportTempPath1;
    }

    public void setMisReportTempPath1(String misReportTempPath1) {
        this.misReportTempPath1 = misReportTempPath1;
    }

    @Basic
    @Column(name = "mis_report_temp_path2", nullable = false, length = 255)
    public String getMisReportTempPath2() {
        return misReportTempPath2;
    }

    public void setMisReportTempPath2(String misReportTempPath2) {
        this.misReportTempPath2 = misReportTempPath2;
    }

    @Basic
    @Column(name = "mis_permission_id", nullable = false, length = 16)
    public String getMisPermissionId() {
        return misPermissionId;
    }

    public void setMisPermissionId(String misPermissionId) {
        this.misPermissionId = misPermissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisReportTemplate that = (MisReportTemplate) o;

        if (misReportTempId != null ? !misReportTempId.equals(that.misReportTempId) : that.misReportTempId != null)
            return false;
        if (misReportTempName != null ? !misReportTempName.equals(that.misReportTempName) : that.misReportTempName != null)
            return false;
        if (misReportTempPath1 != null ? !misReportTempPath1.equals(that.misReportTempPath1) : that.misReportTempPath1 != null)
            return false;
        if (misReportTempPath2 != null ? !misReportTempPath2.equals(that.misReportTempPath2) : that.misReportTempPath2 != null)
            return false;
        if (misPermissionId != null ? !misPermissionId.equals(that.misPermissionId) : that.misPermissionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misReportTempId != null ? misReportTempId.hashCode() : 0;
        result = 31 * result + (misReportTempName != null ? misReportTempName.hashCode() : 0);
        result = 31 * result + (misReportTempPath1 != null ? misReportTempPath1.hashCode() : 0);
        result = 31 * result + (misReportTempPath2 != null ? misReportTempPath2.hashCode() : 0);
        result = 31 * result + (misPermissionId != null ? misPermissionId.hashCode() : 0);
        return result;
    }
}
