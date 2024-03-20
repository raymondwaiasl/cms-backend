package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.PropertyColumnCondDto;
import com.asl.prd004.service.IPropertyColumnConfService;
import com.asl.prd004.service.IPropertyConfigService;
import com.asl.prd004.service.impl.PropertyColumnConfServiceImpl;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/propertyColumnConf")
public class PropertyColumnConfController {

    @Autowired
    IPropertyColumnConfService propertyColumnConfService;

    @Log("Get property page.")
    @PostMapping(value="/getPropertyColumnConf")
    public ResultGenerator getPropertyColumnConfByColumnConfigId(@RequestBody String data) throws JSONException {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String misPropertyColConfId=json.getString("misPropertyColConfId");
        return ResultGenerator.getSuccessResult(propertyColumnConfService.getPropertyColumnConfByColumnConfigId(misPropertyColConfId));
    }

    @Log("Get property page.")
    @PostMapping(value="/addPropertyColumnConf")
    public ResultGenerator addPropertyColumnConf(@RequestBody PropertyColumnCondDto propertyColumnCondDto) throws IllegalAccessException {
        return ResultGenerator.getSuccessResult(propertyColumnConfService.addPropertyColumnConfByColumnConfigId(propertyColumnCondDto));
    }

    @Log("Get property page.")
    @PostMapping(value="/editPropertyColumnConf")
    public ResultGenerator editPropertyColumnConf(@RequestBody PropertyColumnCondDto propertyColumnCondDto) throws IllegalAccessException {
        return ResultGenerator.getSuccessResult(propertyColumnConfService.editPropertyColumnConfByColumnConfigId(propertyColumnCondDto));
    }
}
