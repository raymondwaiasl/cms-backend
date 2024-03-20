package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mis_subscription_msg", schema = "MIS", catalog = "")
public class MisSubscriptionMsg {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_subscription_msg_id", nullable = false, length = 16)
    private String misSubscriptionMsgId;

    @Basic
    @Column(name = "mis_subscription_id", nullable = false, length = 16)
    private String misSubscriptionId;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mis_subscription_msg_date", nullable = false)
    private Date misSubscriptionMsgDate;

    @Basic
    @Column(name = "mis_subscription_event_id", nullable = false, length = 16)
    private String misSubscriptionEventId;

    @Basic
    @Column(name = "mis_subscription_msg_has_read", nullable = false, length = 1)
    private String misSubscriptionMsgHasRead;

    // @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, optional = true)
    // @JoinColumn(name = "mis_subscription_id", insertable = false, updatable = false)
    // @JsonIgnore
    // private MisSubscription misSubscription;

    // @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, optional = true)
    // @JoinColumn(name = "mis_subscription_event_id", insertable = false, updatable = false)
    // @JsonIgnore
    // private MisSubscriptionEvent misSubscriptionEvent;

    
    public String getMisSubscriptionMsgId() {
        return misSubscriptionMsgId;
    }

    public void setMisSubscriptionMsgId(String misSubscriptionMsgId) {
        this.misSubscriptionMsgId = misSubscriptionMsgId;
    }

    public String getMisSubscriptionId() {
        return misSubscriptionId;
    }

    public void setMisSubscriptionId(String misSubscriptionId) {
        this.misSubscriptionId = misSubscriptionId;
    }

    public Date getMisSubscriptionMsgDate() {
        return misSubscriptionMsgDate;
    }

    public void setMisSubscriptionMsgDate(Date misSubscriptionMsgDate) {
        this.misSubscriptionMsgDate = misSubscriptionMsgDate;
    }

    public String getMisSubscriptionEventId() {
        return misSubscriptionEventId;
    }

    public void setMisSubscriptionEventId(String misSubscriptionEventId) {
        this.misSubscriptionEventId = misSubscriptionEventId;
    }

    public String getMisSubscriptionMsgHasRead() {
        return misSubscriptionMsgHasRead;
    }

    public void setMisSubscriptionMsgHasRead(String misSubscriptionMsgHasRead) {
        this.misSubscriptionMsgHasRead = misSubscriptionMsgHasRead;
    }


}