package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.service.IEmailTemplateService;
import com.asl.prd004.utils.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emailTemplate")
public class EmailTemplateController {
    
    @Autowired
    IEmailTemplateService emailTemplateService;
    
    @PostMapping(value = "/getList")
    public ResultGenerator getList(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String emailTemplateName = json.getString("emailTemplateName").isEmpty() ? "" : json.getString("emailTemplateName").trim();
        String emailSubject = json.getString("emailSubject").isEmpty() ? "" : json.getString("emailSubject").trim();

        JSONObject pageState = json.getJSONObject("pageState");

        JSONObject sort = json.getJSONObject("sortModel");

        return ResultGenerator.getSuccessResult(emailTemplateService.getList(emailTemplateName, emailSubject, pageState, sort));
    }
    
    @PostMapping(value = "/getDetail")
    public ResultGenerator getTemplateDetail(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();

        return ResultGenerator.getSuccessResult(emailTemplateService.getTemplateDetail(id));
    }

    @Log("Add Email Template.")
    @RequestMapping(value = "/addTemplate", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addTemplate(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String emailTemplateName = json.getString("emailTemplateName");
        String emailSubject = json.getString("emailSubject");
        String emailBody = json.getString("emailBody");
        String description = json.getString("description");

        if (emailTemplateService.addTemplate(emailTemplateName, emailSubject, emailBody, description)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Update Email Template.")
    @RequestMapping(value = "/editTemplate", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator editTemplate(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);
        String id = json.getString("id");
        String emailTemplateName = json.getString("emailTemplateName");
        String emailSubject = json.getString("emailSubject");
        String emailBody = json.getString("emailBody");
        String description = json.getString("description");

        if (emailTemplateService.editTemplate(id, emailTemplateName, emailSubject, emailBody, description)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete Email Template item.")
    @RequestMapping(value = "/deleteTemplate", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteTemplate(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();

        if (emailTemplateService.deleteTemplate(id)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

}
