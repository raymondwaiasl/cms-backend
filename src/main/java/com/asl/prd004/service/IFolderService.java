package com.asl.prd004.service;

import com.asl.prd004.entity.MisFolder;

import java.util.List;

public interface IFolderService {

    List<MisFolder> getFolderList();

    Boolean saveFolder(String parentId,String folderName);

    MisFolder addFolder(String parentId,String folderName);

    Boolean updateFolder(String id,String name);

    Boolean deleteFolder(String misFolderId);

    Boolean updateFolderParentId(String id,String parentId);

    String getMaxId();

    int updateFolderData(String folderId, String misPermissionId);

    MisFolder getMisFolderById(String folderId);

    List<MisFolder> findByMisFolderParentId(String misFolderParentId);

    MisFolder createFolder(String parentId,String folderName,String permissionId);

    Boolean isPermission(String folderId,String userId,String pdRight);

    List<MisFolder> findByMisFolderName(String misFolderName);

    List<MisFolder> findByMisFolderIdAndMisFolderName(String misFolderId,String misFolderName);

    MisFolder getDefaultFolder();

    String getFolderChildList(String folderId);

}
