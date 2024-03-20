package com.asl.prd004.dao;

import com.asl.prd004.entity.FormInputRequestS;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormInputRequestDao extends JpaRepository<FormInputRequestS, String> {
}