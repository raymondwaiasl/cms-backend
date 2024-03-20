package com.asl.prd004.service.impl;

import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.dao.OrganizationDao;
import com.asl.prd004.entity.MisOrganization;
import com.asl.prd004.service.IOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationServiceImpl implements IOrganizationService {

    @Autowired
    private OrganizationDao organizationDao;

    @Override
    public Boolean saveOrganization(String orgName) {
        MisOrganization misOrganization = new MisOrganization();
        misOrganization.setMisOrganizationId(SerialNumberUtils.getTableSequence("mis_organization"));
        misOrganization.setMisOrganizationName(orgName);
        organizationDao.save(misOrganization);
        return true;
    }

    @Override
    public List<MisOrganization> getOrgList() {
        return organizationDao.findAll();
    }

    @Override
    public Boolean deleteOrg(String orgId) {
        organizationDao.deleteById(orgId);
        return true;
    }
}
