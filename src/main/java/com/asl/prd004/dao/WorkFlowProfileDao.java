package com.asl.prd004.dao;

import com.asl.prd004.entity.MisWfConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @description: TODO
 * @author: billy
 * @date: 2023/10/24 17:55
 * @version: 1.0
 */
public interface WorkFlowProfileDao extends JpaRepository<MisWfConfig, String> {
    @Query(nativeQuery = true,value = "select a.wfConfigId as wfConfigId ,a.wfConfigProfileName as wfConfigProfileName,b.mis_type_id as typeId,b.mis_type_name as typeName,a.mis_is_auto_submit as autoSubmit,a.wf_process_id as processId,a.wf_process_name as processName ,a.mis_init_status as misInitStatus,b.mis_is_draft as misIsDraft  from (\n" +
            "select mwc.mis_wf_config_id as wfConfigId,mwc.mis_profile_name as wfConfigProfileName,mwc.mis_is_auto_submit,mwc.mis_init_status,mwc.wf_process_id,wp.wf_process_name   from mis_wf_config mwc ,wf_process wp where mwc.wf_process_id =wp.wf_process_id)a, \n" +
            "(select mwcd.mis_wf_config_id,mwcd.mis_is_draft,mt.mis_type_id ,mt.mis_type_name from mis_wf_config_dtl mwcd ,mis_type mt where mwcd.mis_type_id =mt.mis_type_id)b\n" +
            "where a.wfConfigId=b.mis_wf_config_id",countQuery ="select count(1)  from ( " +
            "  select mwc.mis_wf_config_id as wfConfigId,mwc.mis_profile_name as wfConfigProfileName,mwc.mis_is_auto_submit,mwc.mis_init_status,mwc.wf_process_id,wp.wf_process_name   from mis_wf_config mwc ,wf_process wp where mwc.wf_process_id =wp.wf_process_id)a, "  +
            "  (select mwcd.mis_wf_config_id,mwcd.mis_is_draft,mt.mis_type_id ,mt.mis_type_name from mis_wf_config_dtl mwcd ,mis_type mt where mwcd.mis_type_id =mt.mis_type_id)b " +
            "   where a.wfConfigId=b.mis_wf_config_id ")
    Page<Object[]> getAllWorkProfile(Pageable pageable);
}
