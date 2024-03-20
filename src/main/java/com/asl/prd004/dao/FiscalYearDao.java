package com.asl.prd004.dao;

import com.asl.prd004.entity.FiscalYearS;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FiscalYearDao extends JpaRepository<FiscalYearS, String> {
}