package com.asl.prd004.dao;

import com.asl.prd004.entity.FormInputEntryS;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormInputEntryDao extends JpaRepository<FormInputEntryS, String> {
}