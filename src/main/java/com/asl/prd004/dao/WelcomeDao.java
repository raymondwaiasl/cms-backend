package com.asl.prd004.dao;

import com.asl.prd004.entity.MisWelcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @description: TODO
 * @author: billy
 * @date: 2023/10/10 16:23
 * @version: 1.0
 */
public interface WelcomeDao extends JpaRepository<MisWelcome, String> {
    @Query(nativeQuery = true,value="update mis_welcome set welcome_content=?2 where mis_welcome_id = ?1")
    @Modifying
    void updateWelcome(String welcomeId, String content);
}
