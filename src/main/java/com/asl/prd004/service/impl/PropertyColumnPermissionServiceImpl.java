package com.asl.prd004.service.impl;

import com.asl.prd004.dao.MisPropertyColumnPermissionDao;
import com.asl.prd004.entity.MisPropertyColumnPermission;
import com.asl.prd004.service.IPropertyColumnPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PropertyColumnPermissionServiceImpl implements IPropertyColumnPermissionService {
    @Autowired
    MisPropertyColumnPermissionDao misPropertyColumnPermissionDao;

    @Override
    @Transactional
    public Boolean editPropertyColumnPermissionByColumnConfigId(List<MisPropertyColumnPermission> misPropertyColumnPermissions) {
        misPropertyColumnPermissionDao.saveAll(misPropertyColumnPermissions);
        return true;
    }

    @Override
    @Transactional
    public Boolean deletePropertyColumnPermissionByColumnConfigId(String misPropertyConfigDetailColumnId) {
        misPropertyColumnPermissionDao.deleteByMisPropertyConfigDetailColumnId(misPropertyConfigDetailColumnId);
        return true;
    }


    @Override
    public List<MisPropertyColumnPermission> getPropertyColumnPermissionByColumnConfigId(String columnConfigId) {
         List<MisPropertyColumnPermission>  misPropertyColumnPermissions =
                 misPropertyColumnPermissionDao.findByMisPropertyConfigDetailColumnId(columnConfigId);
        return misPropertyColumnPermissions;
    }


}
