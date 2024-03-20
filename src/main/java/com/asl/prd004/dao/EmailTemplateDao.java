package com.asl.prd004.dao;

import com.asl.prd004.entity.EmailTemplateS;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateDao extends JpaRepository<EmailTemplateS, String> {
}