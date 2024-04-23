package com.asl.prd004.dao;

import com.asl.prd004.entity.EmailTemplateS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;

public interface EmailTemplateDao extends JpaRepository<EmailTemplateS, String> {

    @Query(nativeQuery = true, value = "SELECT id, email_template_name, email_subject " +
            " from email_template_s ets" +
            " where 1 = 1" +
            " and if(?1 != '', email_template_name like concat('%', ?1, '%'), 1=1)" +
            " and if(?2 != '', email_subject like concat('%', ?2, '%'), 1=1)",
            countQuery = "select count(1) from " +
                    "(" +
                    "   SELECT id \" +\n" +
                    "            \" from email_template_s ets\" +\n" +
                    "            \" where 1 = 1\" +\n" +
                    "            \" and if(?1 != '', email_template_name like concat('%', ?1, '%'), 1=1)\" +\n" +
                    "            \" and if(?2 != '', email_subject like concat('%', ?2, '%'), 1=1)" +
                    ") t")
    Page<Map<String, Object>> findAll(String emailTemplateName, String emailSubject, Pageable pageable);
}