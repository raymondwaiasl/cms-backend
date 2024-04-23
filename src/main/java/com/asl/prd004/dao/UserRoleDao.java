package com.asl.prd004.dao;

import com.asl.prd004.entity.MisUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleDao extends JpaRepository<MisUserRole, Long> {

}
