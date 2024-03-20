package com.asl.prd004.service;

import com.asl.prd004.dto.MisPermissionDto;
import com.asl.prd004.dto.PermissionDTO;

import java.util.List;

public interface IPermissionService {

    List<Object[]> getAllPermission(String typeName, String permName);

    int delPermission(String permissionId);

    int delPermissionValue(String permissionId);

    boolean savePermission(PermissionDTO permissionDTO);

    MisPermissionDto queryPermissionByFolderId(String folderId);

    MisPermissionDto queryPermissionByRecordId(String typeId, String recordId);

    String queryMisPermissionId(String perMissionName, String folderPer);

    String queryMisPermissionDetailId(String misPermissionId, String childType, String s, String rightData);

    int insertMisPermission(String misPermissionId, String perMissionName, String folderPer);

    int insertPermDetail(String misPerDetailId, String misPermissionId, String childType, String misPdPerformerId, String rightData);

    void deletePermissionByUserId(String misUserId);


    String createPermission(String misPermissionName,String detail);

    String updatePermission(String misPermissionId,String detail);

    String deletePermission(String misPermissionId);

    String appendMember(String misPermissionId, String detail);

    String removeMember(String misPermissionId, String detail);
}
