package com.asl.prd004.dao;

import com.asl.prd004.entity.MisPropertyColumnPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MisPropertyColumnPermissionDao extends JpaRepository<MisPropertyColumnPermission, String> {
    List<MisPropertyColumnPermission> findByMisPropertyConfigDetailColumnId(String columnConfigId);

    void deleteByMisPropertyConfigDetailColumnId(String misPropertyConfigDetailColumnId);
}
