package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.entity.CmsStorage;
import com.asl.prd004.service.IStorageService;
import com.asl.prd004.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
public class StorageController {


    @Autowired
    IStorageService storageService;

    @Log("Get all storage")
    @PostMapping(value="/getAllStorage")
    public ResultGenerator getAllStorage(@RequestBody PageableDto page){
        return ResultGenerator.getSuccessResult(storageService.getAllStorage(page));
    }



    @Log("Add new storage")
    @PostMapping(value="/addNewStorage")
    public ResultGenerator addNewStorage(@RequestBody CmsStorage dto){
        if(storageService.addNewStorage(dto)){
            return ResultGenerator.getSuccessResult("add storage success");
        }else {
            return ResultGenerator.getFailResult("add storage fail");
        }
    }
    @Log("Get storageById")
    @PostMapping(value="/getStorageById")
    public ResultGenerator getStorageById(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(storageService.getStorageById(dto.getId()));
    }
    @Log("Delete storage")
    @PostMapping(value="/deleteStorage")
    public ResultGenerator deleteStorage(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(storageService.deleteStorage(dto));
    }


}
