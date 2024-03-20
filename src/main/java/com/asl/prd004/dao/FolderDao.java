package com.asl.prd004.dao;

import com.asl.prd004.entity.MisFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FolderDao extends JpaRepository<MisFolder, String> {


    @Query("from MisFolder where delFlag = '0'")
    List<MisFolder> getFolderList();


    @Query(value="select max(misFolderId) from MisFolder")
    String getMaxId();
    @Modifying
    @Query(nativeQuery = true,value="update mis_folder set mis_permission_id = :misPermissionId where mis_folder_id = :folderId")
    int updateFolderData(@Param("folderId") String folderId, @Param("misPermissionId") String misPermissionId);

    @Modifying
    @Query(value="update MisFolder set misFolderName =?2  where misFolderId =?1")
    void updateFolder(String id, String name);

    @Modifying
    @Query(value="update MisFolder set misFolderParentId =?2  where misFolderId =?1")
    void updateFolderParentId(String id, String misFolderParentId);



    @Query(nativeQuery = true,value="select mis_permission_id from mis_folder where mis_folder_id = :folderId")
    String getPermissionIdByFolderId(@Param("folderId") String folderId);

    MisFolder findMisFolderByMisFolderId(String folderId);


    @Query(nativeQuery = true,value="select getFolderList(:folderId)")
    String getFolderChildList(@Param("folderId") String folderId);

    MisFolder findMisFolderByMisFolderParentIdAndMisFolderName(String folderParentId,String folderName);

    List<MisFolder> findByMisFolderParentId(String misFolderParentId);

    List<MisFolder> findByMisFolderName(String misFolderName);

    @Query(value = "select * from mis_folder where if(?1 !='',mis_folder_id=?1,1=1) and if(?2 !='',mis_folder_name like concat('%',?2,'%'),1=1)",nativeQuery = true)
    List<MisFolder> findByMisFolderIdAndMisFolderName(String misFolderId,String misFolderName);

    @Query(value = "select * from mis_folder where mis_folder_name =\n" +
            "(SELECT mis_sys_config_value FROM mis_sys_config WHERE mis_sys_config_key = 'defaultFolder') limit 1",nativeQuery = true)
    MisFolder getDefaultFolder();
}
