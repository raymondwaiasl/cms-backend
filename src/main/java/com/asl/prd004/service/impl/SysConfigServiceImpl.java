package com.asl.prd004.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.asl.prd004.constant.FileDirConstant;
import com.asl.prd004.dao.SysConfigDao;
import com.asl.prd004.dto.SaveSysConfigDTO;
import com.asl.prd004.entity.MisSysConfig;
import com.asl.prd004.service.FilesStorageService;
import com.asl.prd004.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class SysConfigServiceImpl implements ISysConfigService {

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private FilesStorageService filesStorageService;

    @Override
    public List<MisSysConfig> findByVisible(String visible) {
        return sysConfigDao.findByMisSysConfigVisible(visible);
    }

    @Override
    public boolean saveSysConfig(SaveSysConfigDTO misSysConfigDTO) {
        MultipartFile misSysConfigImage = misSysConfigDTO.getMisSysConfigImage();
        if(Objects.nonNull(misSysConfigImage)){
            filesStorageService.save(FileDirConstant.SYS_CONFIG,misSysConfigImage);
        }
        MisSysConfig misSysConfig = BeanUtil.copyProperties(misSysConfigDTO, MisSysConfig.class);
        sysConfigDao.saveAndFlush(misSysConfig);
        return true;
    }

    @Override
    public boolean updateSysConfig(MisSysConfig sysConfig) {
        sysConfigDao.saveAndFlush(sysConfig);;
        return true;
    }

    @Override
    public boolean deleteSysConfig(String misSysConfigId) {
        sysConfigDao.deleteById(misSysConfigId);;
        return true;
    }

    @Override
    public MisSysConfig findByKey(String key) {
        return sysConfigDao.findByKey(key);
    }

    @Override
    public boolean getWorkflowSwitch() {
        MisSysConfig sysConfig = sysConfigDao.getMisSysConfigByMisSysConfigKey("WorkflowSwitch");
        if(sysConfig != null){
            if("true".equals(sysConfig.getMisSysConfigValue())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }
}
