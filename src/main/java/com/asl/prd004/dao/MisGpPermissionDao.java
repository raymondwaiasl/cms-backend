package com.asl.prd004.dao;

import com.asl.prd004.entity.MisGpTypePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MisGpPermissionDao extends JpaRepository<MisGpTypePermission, String> {


    List<MisGpTypePermission> findByMisTypeIdAndCreateBy(String misTypeId, String createBy);

    List<MisGpTypePermission> findByMisTypeId(String typeId);
}
