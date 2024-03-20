package com.asl.prd004.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_report", schema = "MIS", catalog = "")
public class MisReport {
    private String misReportId;
    private String misReportName;
    private String misReportPath;
    private Timestamp misReportDate;
    private String misReportUserId;

    @Id
    @Column(name = "mis_report_id", nullable = false, length = 16)
    public String getMisReportId() {
        return misReportId;
    }

    public void setMisReportId(String misReportId) {
        this.misReportId = misReportId;
    }

    @Basic
    @Column(name = "mis_report_name", nullable = false, length = 255)
    public String getMisReportName() {
        return misReportName;
    }

    public void setMisReportName(String misReportName) {
        this.misReportName = misReportName;
    }

    @Basic
    @Column(name = "mis_report_path", nullable = false, length = 255)
    public String getMisReportPath() {
        return misReportPath;
    }

    public void setMisReportPath(String misReportPath) {
        this.misReportPath = misReportPath;
    }

    @Basic
    @Column(name = "mis_report_date", nullable = true)
    public Timestamp getMisReportDate() {
        return misReportDate;
    }

    public void setMisReportDate(Timestamp misReportDate) {
        this.misReportDate = misReportDate;
    }

    @Basic
    @Column(name = "mis_report_user_id", nullable = false, length = 16)
    public String getMisReportUserId() {
        return misReportUserId;
    }

    public void setMisReportUserId(String misReportUserId) {
        this.misReportUserId = misReportUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisReport misReport = (MisReport) o;

        if (misReportId != null ? !misReportId.equals(misReport.misReportId) : misReport.misReportId != null)
            return false;
        if (misReportName != null ? !misReportName.equals(misReport.misReportName) : misReport.misReportName != null)
            return false;
        if (misReportPath != null ? !misReportPath.equals(misReport.misReportPath) : misReport.misReportPath != null)
            return false;
        if (misReportDate != null ? !misReportDate.equals(misReport.misReportDate) : misReport.misReportDate != null)
            return false;
        if (misReportUserId != null ? !misReportUserId.equals(misReport.misReportUserId) : misReport.misReportUserId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misReportId != null ? misReportId.hashCode() : 0;
        result = 31 * result + (misReportName != null ? misReportName.hashCode() : 0);
        result = 31 * result + (misReportPath != null ? misReportPath.hashCode() : 0);
        result = 31 * result + (misReportDate != null ? misReportDate.hashCode() : 0);
        result = 31 * result + (misReportUserId != null ? misReportUserId.hashCode() : 0);
        return result;
    }
}
