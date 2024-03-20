package com.asl.prd004.dao;

import com.asl.prd004.entity.MisCmsAutolinkCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * @description: TODO
 * @author: billy
 * @date: 2022/12/1 10:03
 * @version: 1.0
 */
public interface MisCmsAutolinkConditionlDao extends JpaRepository<MisCmsAutolinkCondition, String> {
    @Modifying
    @Query(nativeQuery = true,value="delete from mis_cms_autolink_condition where  if(?1!='',cms_autolink_condition_id =?1,1=1) and  if(?3!='',mis_column_id =?3,1=1)  and cms_autolink_id =?2")
    void deleteByContidionId(String cmsAutolinkConditionId, String cmsAutolinkId, String cmsColumnId);
    @Modifying
    @Query(nativeQuery = true,value="update mis_cms_autolink_condition set cms_autolink_condition=?3 , cms_autolink_value=?4 where  cms_autolink_condition_id =?1 and  mis_column_id =?2")
    void editAutolink(String cmsAutolinkConditionId, String misColumnId, String cmsAutolinkCondition, String cmsAutolinkValue);
}
