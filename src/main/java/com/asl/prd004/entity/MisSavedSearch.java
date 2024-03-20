package com.asl.prd004.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mis_saved_search", schema = "MIS", catalog = "")
public class MisSavedSearch {
    private String misSavedSearchId;
    private String misSavedSearchName;
    private String misQueryFormId;
    private String misSavedSearchUserId;
    private Date misSavedSearchDate;

    @Id
    @Column(name = "mis_saved_search_id", nullable = false, length = 16)
    public String getMisSavedSearchId() {
        return misSavedSearchId;
    }

    public void setMisSavedSearchId(String misSavedSearchId) {
        this.misSavedSearchId = misSavedSearchId;
    }

    @Basic
    @Column(name = "mis_saved_search_name", nullable = true, length = 40)
    public String getMisSavedSearchName() {
        return misSavedSearchName;
    }

    public void setMisSavedSearchName(String misSavedSearchName) {
        this.misSavedSearchName = misSavedSearchName;
    }

    @Basic
    @Column(name = "mis_query_form_id", nullable = false, length = 16)
    public String getMisQueryFormId() {
        return misQueryFormId;
    }

    public void setMisQueryFormId(String misQueryFormId) {
        this.misQueryFormId = misQueryFormId;
    }

    @Basic
    @Column(name = "mis_saved_search_user_id", nullable = false, length = 16)
    public String getMisSavedSearchUserId() {
        return misSavedSearchUserId;
    }

    public void setMisSavedSearchUserId(String misSavedSearchUserId) {
        this.misSavedSearchUserId = misSavedSearchUserId;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mis_saved_search_date", nullable = false)
    public Date getMisSavedSearchDate() {
        return misSavedSearchDate;
    }

    public void setMisSavedSearchDate(Date misSavedSearchDate) {
        this.misSavedSearchDate = misSavedSearchDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisSavedSearch that = (MisSavedSearch) o;

        if (misSavedSearchId != null ? !misSavedSearchId.equals(that.misSavedSearchId) : that.misSavedSearchId != null)
            return false;
        if (misSavedSearchName != null ? !misSavedSearchName.equals(that.misSavedSearchName) : that.misSavedSearchName != null)
            return false;
        if (misQueryFormId != null ? !misQueryFormId.equals(that.misQueryFormId) : that.misQueryFormId != null)
            return false;
        if (misSavedSearchUserId != null ? !misSavedSearchUserId.equals(that.misSavedSearchUserId) : that.misSavedSearchUserId != null)
            return false;
        if (misSavedSearchDate != null ? !misSavedSearchDate.equals(that.misSavedSearchDate) : that.misSavedSearchDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misSavedSearchId != null ? misSavedSearchId.hashCode() : 0;
        result = 31 * result + (misSavedSearchName != null ? misSavedSearchName.hashCode() : 0);
        result = 31 * result + (misQueryFormId != null ? misQueryFormId.hashCode() : 0);
        result = 31 * result + (misSavedSearchUserId != null ? misSavedSearchUserId.hashCode() : 0);
        result = 31 * result + (misSavedSearchDate != null ? misSavedSearchDate.hashCode() : 0);
        return result;
    }
}
