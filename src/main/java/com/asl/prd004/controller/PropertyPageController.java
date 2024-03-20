package com.asl.prd004.controller;


import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.MisPropertyConfigDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.service.IPropertyConfigService;
import com.asl.prd004.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/property")
public class PropertyPageController {


    @Autowired
    MisPropertyConfigDao propertyConfigDao;

    @Autowired
    IPropertyConfigService propertyConfigService;


    @Log("Get property page.")
    @PostMapping(value="/getPropertyPage")
    public ResultGenerator getPropertyPage(@RequestBody PageableDto pageable){
        return ResultGenerator.getSuccessResult(propertyConfigService.getAllPropertyConfigs(pageable));
    }

    @Log("Select property by id.")
    @PostMapping(value="/selectPropertyById")
    public ResultGenerator selectPropertyById(@RequestBody TypeIdDto dto){




        return ResultGenerator.getSuccessResult(propertyConfigService.selectPropertyById(dto.getId()));
    }

    @Log("Add Property page.")
    @PostMapping(value="/addPropertyPage")
    public ResultGenerator addPropertyPage(@RequestBody PropertyPageDto dto){
        if(propertyConfigService.addPropertyPage(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Edit property page.")
    @PostMapping(value="/editPropertyPage")
    public ResultGenerator editPropertyPage(@RequestBody PropertyPageDto dto){
        if(propertyConfigService.editPropertyPage(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete Property.")
    @PostMapping(value="/deleteProperty")
    public ResultGenerator deleteProperty(@RequestBody TypeIdDto dto){
        if(propertyConfigService.deleteProperty(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }
    @Log("Select property config detail by id.")
    @PostMapping(value="/selectPropertyConfigDetailById")
    public ResultGenerator selectPropertyConfigDetailById(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(propertyConfigService.selectPropertyConfigDetailById(dto.getId()));
    }

}
