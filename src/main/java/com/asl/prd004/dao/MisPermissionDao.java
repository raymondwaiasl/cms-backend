package com.asl.prd004.dao;

import com.asl.prd004.entity.MisPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MisPermissionDao extends JpaRepository<MisPermission, String> {
    @Query(nativeQuery = true,value="select id,name,mis_permission_id,mis_permission_name,mis_permission_type,mis_pd_type,mis_pd_performer_id,mis_pd_right from(select a.id,a.name,b.mis_permission_id,b.mis_permission_name,(case when b.mis_permission_type=\"1\" then '文件夹' else '文件夹及子文件夹' end )as mis_permission_type,\n" +
    "(case when b.mis_pd_type='1' then '用户组' else '用户' end)as mis_pd_type,mis_pd_performer_id,replace(replace(replace(replace(b.mis_pd_right,'0','node'),'3','read'),'5','write'),'7','delete')as mis_pd_right from (\n" +
     "select mu.mis_user_id as id ,mu.mis_user_name as name from mis_user mu \n" +
    "union\n" +
    "select mg.mis_group_id as id ,mg.mis_group_name as name from mis_group mg )a,\n" +
    "(select mp.mis_permission_id,mp.mis_permission_name,mp.mis_permission_type ,mpd.mis_pd_type,mpd.mis_pd_right,mpd.mis_pd_performer_id from mis_permission mp ,mis_permission_detail mpd where mp.mis_permission_id =mpd.mis_permission_id )b\n" +
    "where a.id=b.mis_pd_performer_id) aa where  if(?1!='',name=?1,1=1) and if(?2!='',mis_permission_name=?2,1=1) ")
    List<Object[]> getAllPermission(String typeName, String permName);
    @Modifying
    @Query("delete from MisPermission where misPermissionId =?1")
    void delPermissionById(String permissionId);
    @Query(nativeQuery = true,value="select mis_permission_id from mis_permission mp where mp.mis_permission_name =?1 and mp.mis_permission_type =?2")
    String queryMisPermissionId(String perMissionName, String folderPer);

    MisPermission findMisPermissionByMisPermissionId(String permissionId);
}
