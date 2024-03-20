package com.asl.prd004.service;

import com.asl.prd004.entity.MisOrganization;

import java.util.List;

public interface IOrganizationService {

    Boolean saveOrganization(String orgName);

    List<MisOrganization> getOrgList();

    Boolean deleteOrg(String orgId);
}
