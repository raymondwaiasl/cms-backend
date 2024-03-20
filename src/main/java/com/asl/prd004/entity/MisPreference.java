package com.asl.prd004.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "mis_preference", schema = "MIS", catalog = "")
public class MisPreference implements Serializable {
    @Id
    @Column(name = "mis_preference_id", nullable = false)
    private String misPreferenceId;
    @Column(name = "mis_preference_name", nullable = false)
    private String misPreferenceName;
    @Column(name = "mis_preference_value", nullable = false)
    private String misPreferenceValue;
    @Column(name = "mis_preference_visible", nullable = true)
    private String misPreferenceVisible;
    @Column(name = "create_time", nullable = true)
    private String createTime;
    @Column(name = "mis_user_id", nullable = false)
    private String misUserId;

    public String getMisPreferenceId() {
        return misPreferenceId;
    }

    public void setMisPreferenceId(String misPreferenceId) {
        this.misPreferenceId = misPreferenceId;
    }

    public String getMisPreferenceName() {
        return misPreferenceName;
    }

    public void setMisPreferenceName(String misPreferenceName) {
        this.misPreferenceName = misPreferenceName;
    }

    public String getMisPreferenceValue() {
        return misPreferenceValue;
    }

    public void setMisPreferenceValue(String misPreferenceValue) {
        this.misPreferenceValue = misPreferenceValue;
    }

    public String getMisPreferenceVisible() {
        return misPreferenceVisible;
    }

    public void setMisPreferenceVisible(String misPreferenceVisible) {
        this.misPreferenceVisible = misPreferenceVisible;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMisUserId() {
        return misUserId;
    }

    public void setMisUserId(String misUserId) {
        this.misUserId = misUserId;
    }
}
