package com.asl.prd004.dao;

import com.asl.prd004.entity.FormInputValueS;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormInputValueDao extends JpaRepository<FormInputValueS, String> {
}