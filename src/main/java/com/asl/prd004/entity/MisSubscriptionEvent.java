package com.asl.prd004.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mis_subscription_event", schema = "MIS", catalog = "")
public class MisSubscriptionEvent {
    private String misSubEventId;
    private String misSubscriptionId;
    private String misSubEventMsg;
    private Date misSubEventBuDate;
    private String misSubEventBuRepeat;

   // @OneToMany(mappedBy = "misSubscriptionMsg", cascade=CascadeType.ALL, orphanRemoval = true)
   // private List<MisSubscriptionMsg> columns;

    @Id
    @Column(name = "mis_sub_event_id", nullable = false, length = 16)
    public String getMisSubEventId() {
        return misSubEventId;
    }

    public void setMisSubEventId(String misSubEventId) {
        this.misSubEventId = misSubEventId;
    }

    @Basic
    @Column(name = "mis_subscription_id", nullable = false, length = 16)
    public String getMisSubscriptionId() {
        return misSubscriptionId;
    }

    public void setMisSubscriptionId(String misSubscriptionId) {
        this.misSubscriptionId = misSubscriptionId;
    }

    @Basic
    @Column(name = "mis_sub_event_msg", nullable = false, length = 255)
    public String getMisSubEventMsg() {
        return misSubEventMsg;
    }

    public void setMisSubEventMsg(String misSubEventMsg) {
        this.misSubEventMsg = misSubEventMsg;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mis_sub_event_bu_date")
    public Date getMisSubEventBuDate() {
        return misSubEventBuDate;
    }

    public void setMisSubEventBuDate(Date misSubEventBuDate) {
        this.misSubEventBuDate = misSubEventBuDate;
    }

    @Basic
    @Column(name = "mis_sub_event_bu_repeat", nullable = false, length = 1)
    public String getMisSubEventBuRepeat() {
        return misSubEventBuRepeat;
    }

    public void setMisSubEventBuRepeat(String misSubEventBuRepeat) {
        this.misSubEventBuRepeat = misSubEventBuRepeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisSubscriptionEvent that = (MisSubscriptionEvent) o;

        if (misSubEventId != null ? !misSubEventId.equals(that.misSubEventId) : that.misSubEventId != null)
            return false;
        if (misSubscriptionId != null ? !misSubscriptionId.equals(that.misSubscriptionId) : that.misSubscriptionId != null)
            return false;
        if (misSubEventMsg != null ? !misSubEventMsg.equals(that.misSubEventMsg) : that.misSubEventMsg != null)
            return false;
        if (misSubEventBuRepeat != null ? !misSubEventBuRepeat.equals(that.misSubEventBuRepeat) : that.misSubEventBuRepeat != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misSubEventId != null ? misSubEventId.hashCode() : 0;
        result = 31 * result + (misSubscriptionId != null ? misSubscriptionId.hashCode() : 0);
        result = 31 * result + (misSubEventMsg != null ? misSubEventMsg.hashCode() : 0);
        result = 31 * result + (misSubEventBuRepeat != null ? misSubEventBuRepeat.hashCode() : 0);
        return result;
    }
}
