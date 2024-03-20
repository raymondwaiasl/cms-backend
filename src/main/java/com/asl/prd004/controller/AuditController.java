package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.service.IAuditLogService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/AuditManage")
@CrossOrigin(origins = {"http://localhost:3000/"},allowCredentials = "true",allowedHeaders = {"X-Requested-With"},
        maxAge = 3600L, methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
public class AuditController {
    @Autowired
    private IAuditLogService auditLogService;

    @Log("Query all audit data")
    @RequestMapping(value="/getAllAudit")
    public ResultGenerator getAllAudit(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            String auditUser=json.getString("auditUser");
            String auditOperation=json.getString("auditOperation");
            String auditCreateTime=json.getString("auditCreateTime");
            JSONObject  pageState = json.getJSONObject("pageState");
            JSONObject  sort = json.getJSONObject("sortModel");
            PageDataDto auditList=auditLogService.getAllAudit(auditUser,auditOperation,auditCreateTime,pageState,sort);
            return ResultGenerator.getSuccessResult(auditList);
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("query fail");
        }
    }
}
