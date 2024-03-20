package com.asl.prd004.service.impl;

import com.asl.prd004.dao.MisWfConfigDtlDao;
import com.asl.prd004.dao.WorkFlowProfileDao;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.workFlowProfileDTO;
import com.asl.prd004.entity.MisWfConfig;
import com.asl.prd004.entity.MisWfConfigDtl;
import com.asl.prd004.service.IWorkFlowProfileService;
import com.asl.prd004.utils.CastEntity;
import com.asl.prd004.utils.SerialNumberUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/10/24 18:06
 */
@Service
public class WorkFlowProfileServiceImpl implements IWorkFlowProfileService {
    @Autowired
    WorkFlowProfileDao workFlowProfileDao;
    @Autowired
    MisWfConfigDtlDao misWfConfigDtlDao;
    @Override
    public PageDataDto getAllWorkProfile(JSONObject pageState, JSONObject sort) {
        PageDataDto workProfileDto = null;
        try {
            int pageNum = pageState.getInt("page")-1;
            int pageSize =  pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);
                String sortField ="wfConfigId";
                if(sort.getString("sort").equalsIgnoreCase("asc")){
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
                }else{
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
                }
            Page<Object[]> worfProfilePage=workFlowProfileDao.getAllWorkProfile(pageable);
            List<workFlowProfileDTO> workFlowProfileList= CastEntity.castEntity(worfProfilePage.getContent(),workFlowProfileDTO.class);
            workProfileDto = new PageDataDto();
            workProfileDto.setData(workFlowProfileList);
            workProfileDto.setTotal( worfProfilePage.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workProfileDto;
    }

     @Transactional
    @Override
    public int addNewTypeByFlow(JSONObject json) {
        try {
            String wfConfigProfileName=json.getString("wfConfigProfileName");
            String autoSubmit=json.getString("autoSubmit");
            String misInitStatus=json.getString("misInitStatus");
            String processId=json.getString("processName");
            String draftCheckData=json.getString("draftCheckData");
            String draftTableData=json.getString("draftTableData");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> draftCheckMap = objectMapper.readValue(draftCheckData, Map.class);
            Map<String, Object> draftTableMap = objectMapper.readValue(draftTableData, Map.class);
            String misWfConfigId=SerialNumberUtils.getTableSequence("mis_wf_config");
            MisWfConfig misWfConfig=new MisWfConfig();
            misWfConfig.setMisWfConfigId(misWfConfigId);
            misWfConfig.setMisProfileName(wfConfigProfileName);
            misWfConfig.setMisIsAutoSubmit(autoSubmit);
            misWfConfig.setMisInitStatus(misInitStatus);
            misWfConfig.setWfProcessId(processId);
            workFlowProfileDao.saveAndFlush(misWfConfig);
            String misWfConfigDtlId=SerialNumberUtils.getTableSequence("mis_wf_config_dtl");
            if(draftTableMap.size()>0) {
                for (int i=1;i<=draftTableMap.size();i++) {
                    MisWfConfigDtl misWfConfigDtl=new MisWfConfigDtl();
                    String con=i+"";
                    String typeId= (String) draftTableMap.get(con);
                    Boolean isDraft= (Boolean) draftCheckMap.get(con);
                    misWfConfigDtl.setMisWfConfigDtlId(misWfConfigDtlId);
                    misWfConfigDtl.setMisWfConfigId(misWfConfigId);
                    misWfConfigDtl.setMisTypeId(typeId);
                    misWfConfigDtl.setMisIsDraft(isDraft==true?"1":"2");
                    misWfConfigDtlDao.saveAndFlush(misWfConfigDtl);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return 1;
    }
}


