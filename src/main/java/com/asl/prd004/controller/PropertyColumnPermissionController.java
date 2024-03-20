package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.entity.MisPropertyColumnPermission;
import com.asl.prd004.service.IPropertyColumnPermissionService;
import com.asl.prd004.utils.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/propertyColumnPermission")
public class PropertyColumnPermissionController {
    @Autowired
    IPropertyColumnPermissionService propertyColumnPermissionService;

    @Log("Get property page.")
    @PostMapping(value="/editPropertyColumnPermission")
    public ResultGenerator editPropertyColumnPermission(@RequestBody  String data) throws  JSONException, JsonProcessingException {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        JSONArray misPropertyColumnPermissionJson= json.getJSONArray("columnPermission");

        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<MisPropertyColumnPermission> misPropertyColumnPermissions = objectMapper.readValue(misPropertyColumnPermissionJson.toString(),
                typeFactory.constructCollectionType(List.class, MisPropertyColumnPermission.class));
        String misPropertyConfigDetailColumnId= json.getString("misPropertyConfigDetailColumnId");
        propertyColumnPermissionService.deletePropertyColumnPermissionByColumnConfigId(misPropertyConfigDetailColumnId);
        return ResultGenerator.getSuccessResult(propertyColumnPermissionService.editPropertyColumnPermissionByColumnConfigId(misPropertyColumnPermissions));
    }

    @Log("Get property page.")
    @PostMapping(value="/getPropertyColumnPermission")
    public ResultGenerator getPropertyColumnPermission(@RequestBody  String data) throws IllegalAccessException, JSONException {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String misPropertyConfigDetailColumnId=json.getString("misPropertyConfigDetailColumnId");
        return ResultGenerator.getSuccessResult(propertyColumnPermissionService.getPropertyColumnPermissionByColumnConfigId(misPropertyConfigDetailColumnId));
    }

}
