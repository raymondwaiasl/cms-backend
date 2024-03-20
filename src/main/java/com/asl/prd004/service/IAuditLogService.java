package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.entity.MisAuditLog;
import org.json.JSONObject;

public interface IAuditLogService {
   String createAudit(JSONObject json);

    int saveAuditLog(MisAuditLog sysLog);

    PageDataDto getAllAudit(String auditUser, String auditOperation, String auditCreateTime, JSONObject... params);
}
