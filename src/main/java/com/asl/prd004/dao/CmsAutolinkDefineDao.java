package com.asl.prd004.dao;

import com.asl.prd004.entity.AutolinkDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * @description: TODO
 * @author: billy
 * @date: 2022/12/1 10:03
 * @version: 1.0
 */
public interface CmsAutolinkDefineDao extends JpaRepository<AutolinkDetail, String>, JpaSpecificationExecutor<AutolinkDetail> {
    @Query(nativeQuery = true,value="select mcac.cms_autolink_condition_id,mcac.mis_column_id,mf1.mis_folder_id ,mca.mis_type_id , mca.cms_autolink_id as cms_autolink_id ,mf1.mis_folder_name as mis_folder_name,mcad.cms_folder_level as cms_folder_level,mc.mis_column_label,(case when mcac.cms_autolink_condition='0' then 'is' when mcac.cms_autolink_condition='1' then 'is not'\n" +
            "when mcac.cms_autolink_condition='2' then 'contains'\n" +
            "when mcac.cms_autolink_condition='3' then 'greater'\n" +
            "when mcac.cms_autolink_condition='4' then 'less' end\n" +
            ") cms_autolink_condition,mcac.cms_autolink_value as cms_autolink_value,(case when mcac.cms_autolink_condition_rel='1' then 'And' when mcac.cms_autolink_condition_rel='2' then 'Or' else '' end)as cms_autolink_condition_rel from mis_folder mf1 inner join mis_cms_autolink mca\n" +
            "            on mf1.mis_folder_id =mca.mis_folder_id\n" +
            "            inner join mis_cms_autolink_detail mcad  on mca.cms_autolink_id=mcad.cms_autolink_id\n" +
            "            inner join mis_cms_autolink_condition mcac on mcad.cms_autolink_id =mcac.cms_autolink_id and mcad.mis_column_id=mcac.mis_column_id \n" +
            "            inner join mis_column mc on mcad.mis_column_id =mc.mis_column_id \n" +
            "            where  if(?1!='',mca.mis_folder_id =?1,1=1) and if(?2!='',mca.mis_type_id =?2,1=1)  ",countQuery = "select count(1)  from mis_folder mf1 inner join mis_cms_autolink mca\n" +
            "            on mf1.mis_folder_id =mca.mis_folder_id\n" +
            "             inner join mis_cms_autolink_detail mcad  on mca.cms_autolink_id=mcad.cms_autolink_id\n" +
            "             inner join mis_cms_autolink_condition mcac on mcad.cms_autolink_id =mcac.cms_autolink_id and mcad.mis_column_id=mcac.mis_column_id \n" +
            "            where  if(?1!='',mca.mis_folder_id =?1,1=1) and if(?2!='',mca.mis_type_id =?2,1=1)")
    Page<AutolinkDetail> getAllAutoLink(String folderId, String typeId, Pageable pageable);
    @Query(nativeQuery = true,value="select mca.cms_autolink_id as cms_autolink_id ,mf1.mis_folder_name as mis_folder_name,mcad.cms_folder_level as cms_folder_level,'' mis_column_label,mcac.cms_autolink_condition as cms_autolink_condition,mcac.cms_autolink_value as cms_autolink_value,mcac.cms_autolink_condition_rel as cms_autolink_condition_rel from mis_folder mf1 inner join mis_cms_autolink mca\n" +
            "            on mf1.mis_folder_id =mca.mis_folder_id\n" +
            "             inner join mis_cms_autolink_detail mcad  on mca.cms_autolink_id=mcad.cms_autolink_id\n" +
            "             inner join mis_cms_autolink_condition mcac on mcad.cms_autolink_id =mcac.cms_autolink_id\n" +
            "            where  mca.mis_folder_id ='0015000000000114' and mca.mis_type_id ='0028000000000074'")
    List<AutolinkDetail> getAllAutoLink1();
}
