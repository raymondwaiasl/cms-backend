package com.asl.prd004.dao;

import com.asl.prd004.entity.MisReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTemplateDao extends JpaRepository<MisReportTemplate, String> {
}
