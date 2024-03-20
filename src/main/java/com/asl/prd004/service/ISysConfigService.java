package com.asl.prd004.service;

import com.asl.prd004.dto.SaveSysConfigDTO;
import com.asl.prd004.entity.MisSysConfig;

import java.util.List;

public interface ISysConfigService {

    List<MisSysConfig> findByVisible(String visible);

    boolean saveSysConfig(SaveSysConfigDTO misSysConfigDTO);

    boolean updateSysConfig(MisSysConfig sysConfig);

    boolean deleteSysConfig(String misSysConfigId);

    MisSysConfig findByKey(String key);

    boolean getWorkflowSwitch();
}
