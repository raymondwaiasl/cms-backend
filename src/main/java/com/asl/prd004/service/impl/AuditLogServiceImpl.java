package com.asl.prd004.service.impl;

import com.asl.prd004.dao.MisAuditLogDao;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.dao.MisUserDao;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.entity.MisAuditLog;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.service.IAuditLogService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogServiceImpl implements IAuditLogService {
    @Autowired
    private MisAuditLogDao misAuditLogDao;
    @Autowired
    private MisTypeDao misTypeDao;
    @Autowired
    private MisUserDao misUserDao;

    @Override
    public String createAudit(JSONObject json) {
        try {
            String misAuditEvent = json.getString("MIS_AUDIT_EVENT");
            String misAuditUser = json.getString("MIS_AUDIT_USER");
            String misAuditTableId = json.getString("MIS_AUDIT_TABLE_ID");
            String misAuditRecId = json.getString("MIS_AUDIT_REC_ID");
            String misAuditTimestamp = json.getString("MIS_AUDIT_TIMESTAMP");
            String misAuditParameter = json.getString("MIS_AUDIT_PARAMETER");
            List<MisUser> misUsers=misUserDao.findByMisUserName(misAuditUser);
            String misTypes=  misTypeDao.getTableNameById(misAuditTableId);
            if(misUsers.size()==0){
                return "-3";
            }
            if(misTypes==null){
                return "-4";
            }
            MisAuditLog misAuditLog=new MisAuditLog();
            misAuditLog.setMisAuditUser(misAuditUser);
            misAuditLog.setMisAuditOperation(misAuditEvent);
            misAuditLog.setMisAuditParams(misAuditParameter);
            misAuditLog.setCreateTime(misAuditTimestamp);
            misAuditLog.setMisAuditTime(4);
            misAuditLog.setMisAuditMethod(misAuditEvent);
            MisAuditLog auditLog=misAuditLogDao.save(misAuditLog);

            return String.valueOf(auditLog.getMisAuditId());
        } catch (JSONException e) {
           return "-1";
        }
    }

    @Override
    public int saveAuditLog(MisAuditLog sysLog) {
//        misAuditLogDao.save(sysLog);
        return 1;
    }

    @Override
    public PageDataDto getAllAudit(String auditUser,String auditOperation,String auditCreateTime, JSONObject... params) {
        PageDataDto auditDto = null;
        try {
            JSONObject pageState = params[0];
            int pageNum = pageState.getInt("page")-1;
            int pageSize =  pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);

            if (params.length==2 && params[1].length()!=0) {
                JSONObject sortState = params[1];
                String sortField ="mis_audit_id";
                if(sortState.getString("sort").equalsIgnoreCase("asc")){
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
                }else{
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
                }
            }
            Page<MisAuditLog> auditPage=misAuditLogDao.getAllAudit(auditUser,auditOperation,auditCreateTime,pageable);
            List<MisAuditLog> auditList=auditPage.getContent();
            auditDto = new PageDataDto();
            auditDto.setData(auditList);
            auditDto.setTotal(auditPage.getTotalElements());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return auditDto;
    }
}
