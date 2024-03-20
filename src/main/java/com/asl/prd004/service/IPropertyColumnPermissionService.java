package com.asl.prd004.service;

import com.asl.prd004.dto.PropertyColumnCondDto;
import com.asl.prd004.dto.PropertyColumnPermissionDto;
import com.asl.prd004.entity.MisPropertyColumnPermission;
import org.json.JSONArray;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public interface IPropertyColumnPermissionService {


    Boolean editPropertyColumnPermissionByColumnConfigId(List<MisPropertyColumnPermission> propertyColumnPermissionDto);

    @Transactional
    Boolean deletePropertyColumnPermissionByColumnConfigId(String misPropertyConfigDetailColumnId);

    List<MisPropertyColumnPermission> getPropertyColumnPermissionByColumnConfigId(String columnConfigId);


}
