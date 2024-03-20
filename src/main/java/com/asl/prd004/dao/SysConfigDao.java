package com.asl.prd004.dao;

import com.asl.prd004.entity.MisSysConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SysConfigDao extends JpaRepository<MisSysConfig, String> {


    List<MisSysConfig> findByMisSysConfigVisible(String misSysConfigVisible);

    @Query(value="select max(t.misSysConfigId) from MisSysConfig t")
    String getMaxId();

    @Query(value = "select * from mis_sys_config where mis_sys_config_key = :key limit 1",nativeQuery = true)
    MisSysConfig findByKey(@Param("key")String key);

    MisSysConfig getMisSysConfigByMisSysConfigKey(String key);
}
