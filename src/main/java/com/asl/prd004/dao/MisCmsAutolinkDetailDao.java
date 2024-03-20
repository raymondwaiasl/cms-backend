package com.asl.prd004.dao;

import com.asl.prd004.entity.MisCmsAutolinkDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * @description: TODO
 * @author: billy
 * @date: 2022/12/1 10:03
 * @version: 1.0
 */
public interface MisCmsAutolinkDetailDao extends JpaRepository<MisCmsAutolinkDetail, String> {
    @Modifying
    @Query(nativeQuery = true,value="delete from mis_cms_autolink_detail where if(?1!='',cms_autolink_id =?1,1=1) and  if(?2!='',mis_column_id =?2,1=1)")
    void deleteByCmsAutolinkId(String cmsAutolinkId,String cmsColumnId);
    @Modifying
    @Query(nativeQuery = true,value="select mcad.cms_folder_level ,mcad.mis_column_id from mis_cms_autolink_detail mcad where cms_autolink_id =?1 order by cms_folder_level")
    List<Object[]> countAutoLinkDetail(String cmsAutolinkId);
}
