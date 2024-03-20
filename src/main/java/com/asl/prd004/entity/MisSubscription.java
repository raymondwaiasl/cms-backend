package com.asl.prd004.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "mis_subscription", schema = "MIS", catalog = "")
public class MisSubscription {
    private String misSubscriptionId;
    private String misSubscriptionType;
    private String misSubscriptionObjId;
    private String misSubscriptionUserId;
    private Timestamp misSubscriptionDate;

   // @OneToMany(mappedBy = "misSubscriptionMsg", cascade=CascadeType.ALL, orphanRemoval = true)
   // private List<MisSubscriptionMsg> columns;
    
    @Id
    @Column(name = "mis_subscription_id", nullable = false, length = 16)
    public String getMisSubscriptionId() {
        return misSubscriptionId;
    }

    public void setMisSubscriptionId(String misSubscriptionId) {
        this.misSubscriptionId = misSubscriptionId;
    }

    @Basic
    @Column(name = "mis_subscription_type", nullable = false, length = 16)
    public String getMisSubscriptionType() {
        return misSubscriptionType;
    }

    public void setMisSubscriptionType(String misSubscriptionType) {
        this.misSubscriptionType = misSubscriptionType;
    }

    @Basic
    @Column(name = "mis_subscription_obj_id", nullable = false, length = 16)
    public String getMisSubscriptionObjId() {
        return misSubscriptionObjId;
    }

    public void setMisSubscriptionObjId(String misSubscriptionObjId) {
        this.misSubscriptionObjId = misSubscriptionObjId;
    }

    @Basic
    @Column(name = "mis_subscription_user_id", nullable = false, length = 16)
    public String getMisSubscriptionUserId() {
        return misSubscriptionUserId;
    }

    public void setMisSubscriptionUserId(String misSubscriptionUserId) {
        this.misSubscriptionUserId = misSubscriptionUserId;
    }

    @Basic
    @Column(name = "mis_subscription_date", nullable = false)
    public Timestamp getMisSubscriptionDate() {
        return misSubscriptionDate;
    }

    public void setMisSubscriptionDate(Timestamp misSubscriptionDate) {
        this.misSubscriptionDate = misSubscriptionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisSubscription that = (MisSubscription) o;

        if (misSubscriptionId != null ? !misSubscriptionId.equals(that.misSubscriptionId) : that.misSubscriptionId != null)
            return false;
        if (misSubscriptionType != null ? !misSubscriptionType.equals(that.misSubscriptionType) : that.misSubscriptionType != null)
            return false;
        if (misSubscriptionObjId != null ? !misSubscriptionObjId.equals(that.misSubscriptionObjId) : that.misSubscriptionObjId != null)
            return false;
        if (misSubscriptionUserId != null ? !misSubscriptionUserId.equals(that.misSubscriptionUserId) : that.misSubscriptionUserId != null)
            return false;
        if (misSubscriptionDate != null ? !misSubscriptionDate.equals(that.misSubscriptionDate) : that.misSubscriptionDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misSubscriptionId != null ? misSubscriptionId.hashCode() : 0;
        result = 31 * result + (misSubscriptionType != null ? misSubscriptionType.hashCode() : 0);
        result = 31 * result + (misSubscriptionObjId != null ? misSubscriptionObjId.hashCode() : 0);
        result = 31 * result + (misSubscriptionUserId != null ? misSubscriptionUserId.hashCode() : 0);
        result = 31 * result + (misSubscriptionDate != null ? misSubscriptionDate.hashCode() : 0);
        return result;
    }
}
