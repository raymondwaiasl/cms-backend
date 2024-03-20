package com.asl.prd004.dao;

import com.asl.prd004.entity.MisOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationDao extends JpaRepository<MisOrganization, String> {

}
