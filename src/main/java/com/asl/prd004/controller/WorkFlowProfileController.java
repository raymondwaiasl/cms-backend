package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.service.IWorkFlowProfileService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workFlowProfile")
public class WorkFlowProfileController {

    @Autowired
    IWorkFlowProfileService iWorkFlowProfileService;
    @Log("Query all workFlowProfile")
    @PostMapping(value="/getAllWorkProfile")
    public ResultGenerator getAllWorkProfile(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            JSONObject  pageState = json.getJSONObject("pageState");
            JSONObject  sort = json.getJSONObject("sortModel");
            PageDataDto workProfileList=iWorkFlowProfileService.getAllWorkProfile(pageState,sort);
            return ResultGenerator.getSuccessResult(workProfileList);
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("query fail");
        }

    }
    @Log("add workFlow type data")
    @PostMapping(value="/addNewTypeByFlow")
    public ResultGenerator addNewTypeByFlow(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            int excelInt=iWorkFlowProfileService.addNewTypeByFlow(json);
            return ResultGenerator.getSuccessResult("add success");
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("add fail");
        }

    }


}
