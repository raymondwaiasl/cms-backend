package com.asl.prd004.entity;


import javax.persistence.*;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/10/10 16:19
 */
@Entity
@Table(name = "mis_welcome", schema = "MIS", catalog = "")
public class MisWelcome extends BaseModel{
    private String misWelcomeId;
    private String welcomeContent;
    @Id
    @Column(name = "mis_welcome_id", nullable = false)
    public String getMisWelcomeId() {
        return misWelcomeId;
    }

    public void setMisWelcomeId(String misWelcomeId) {
        this.misWelcomeId = misWelcomeId;
    }
    @Column(name = "welcome_content", nullable = false)
    public String getWelcomeContent() {
        return welcomeContent;
    }

    public void setWelcomeContent(String welcomeContent) {
        this.welcomeContent = welcomeContent;
    }
}


