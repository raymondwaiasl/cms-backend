package com.asl.prd004.dao;

import com.asl.prd004.entity.MisPermissionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MisPermissionDetailDao extends JpaRepository<MisPermissionDetail, Long> {

    @Modifying
    @Query("delete from MisPermissionDetail where misPermissionId =?1")
    void delPermissionValueById(String permissionId);
    @Query(nativeQuery = true,value="select mis_pd_id from mis_permission_detail mpd where mpd.mis_pd_performer_id=?1 and mpd.mis_pd_type =?2 and mpd.mis_pd_performer_id =?3 and mpd.mis_pd_right =?4")
    String queryMisPermissionDetailId(String misPermissionId, String childType, String s, String rightData);
    @Query(nativeQuery = true,value="select mpd.mis_pd_id,mpd.mis_permission_id,mpd.mis_pd_type,(case when mpd.mis_pd_type='3' then (select DISTINCT mis_group_name from mis_group mg where mg.mis_group_id=mpd.mis_pd_performer_id)\n" +
            "else (select DISTINCT mis_user_name from mis_user mg where mg.mis_user_id=mpd.mis_pd_performer_id)\n" +
            "end )as typeName,mpd.mis_pd_performer_id,mpd.mis_pd_right from mis_permission_detail mpd where mpd.mis_permission_id in(\n" +
            "select mf.mis_permission_id from mis_folder mf where mf.mis_folder_id=?1)")
    List<Object[]> queryPermissionData(String folderId);
    @Query(nativeQuery = true,value="select * from mis_permission_detail where mis_permission_id=?1")
    List<MisPermissionDetail> getListByPermissionId(String misPermissionId);
    @Modifying
    @Query("delete from MisPermissionDetail where misPdPerformerId =?1")
    void deletePermissionByUserId(String misUserId);

    @Modifying
    @Query("delete from MisPermissionDetail where mis_permission_id=?1 and mis_pd_performer_id =?2")
    void deletePermissionById(String permissionId,String misPdPerformerId);
    @Query(nativeQuery = true,value="select mis_pd_right from mis_permission_detail where mis_permission_id=?1 and mis_pd_performer_id =?2")
    List queryPdRight(String misPermissionId, String userId);
}
