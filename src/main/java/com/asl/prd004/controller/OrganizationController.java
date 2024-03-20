package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.service.IOrganizationService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Resource
    private IOrganizationService organizationService;

    @Log("Get org list.")
    @RequestMapping(value="/getOrgList")
    public ResultGenerator getOrgList(){
        return ResultGenerator.getSuccessResult(organizationService.getOrgList());
    }

    @Log("Save organization.")
    @RequestMapping(value="/saveOrganization", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator saveOrganization(
            @RequestParam("name") String data,
            @RequestParam("iconImage") MultipartFile iconImage) throws JSONException {
        //解析接受到的json数据
        JSONObject json =new JSONObject(data);
        String orgName = json.getString("orgName");
        Boolean flag = organizationService.saveOrganization(orgName);
        return ResultGenerator.getSuccessResult(flag);
    }

    @Log("Delete organization.")
    @RequestMapping(value="/deleteOrg", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteOrg(@RequestBody String param) throws JSONException {
        JSONObject json =new JSONObject(param);
        String orgId = json.getString("orgId");
        return ResultGenerator.getSuccessResult(organizationService.deleteOrg(orgId));
    }
}
