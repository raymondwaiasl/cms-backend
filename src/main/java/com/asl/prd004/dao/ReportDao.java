package com.asl.prd004.dao;

import com.asl.prd004.entity.MisReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportDao extends JpaRepository<MisReport, String> {
}
