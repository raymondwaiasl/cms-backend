package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.SaveSysConfigDTO;
import com.asl.prd004.entity.MisSysConfig;
import com.asl.prd004.service.ISysConfigService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/sysConfig")
public class SysConfigController {

    @Resource
    private ISysConfigService sysConfigService;


    @Log("Find system config list.")
    @RequestMapping(value="/findSysConfigList")
    public ResultGenerator findSysConfigList(){
        List<MisSysConfig> sysConfigList = sysConfigService.findByVisible("0");
        return ResultGenerator.getSuccessResult(sysConfigList);
    }

    @Log("Find system config by key.")
    @RequestMapping(value="/findSysConfigByKey")
    public ResultGenerator findSysConfigByKey(@RequestBody MisSysConfig data){
        return ResultGenerator.getSuccessResult(sysConfigService.findByKey(data.getMisSysConfigKey()));
    }

    @Log("Save system config.")
    @RequestMapping(value="/saveSysConfig", method = RequestMethod.POST, produces = "application/json",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultGenerator saveSysConfig(
            @RequestParam("misSysConfigId") String misSysConfigId,
            @RequestParam("misSysConfigKey") String misSysConfigKey,
            @RequestParam("misSysConfigValue") String misSysConfigValue,
            @RequestParam(name = "misSysConfigImage",required = false) MultipartFile misSysConfigImage,
            @RequestParam("misSysConfigVisible") String misSysConfigVisible,
            @RequestParam("misSysConfigType") String misSysConfigType
    ) {
        sysConfigService.saveSysConfig(
            SaveSysConfigDTO.builder()
                .misSysConfigId(misSysConfigId)
                .misSysConfigKey(misSysConfigKey)
                .misSysConfigValue(misSysConfigValue)
                .misSysConfigImage(misSysConfigImage)
                .misSysConfigVisible(misSysConfigVisible)
                .misSysConfigType(misSysConfigType)
                .build());
        return ResultGenerator.getSuccessResult("success");
    }

    @Log("Delete system config.")
    @RequestMapping(value="/deleteSysConfig", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteSysConfig(@RequestBody String data) throws JSONException {
        JSONObject json = new JSONObject(data);
        String misSysConfigId = json.getString("misSysConfigId");
        if(sysConfigService.deleteSysConfig(misSysConfigId)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return ResultGenerator.getSuccessResult("failed");
        }
    }

    @Log("Get workflow switch.")
    @RequestMapping(value="/getWorkflowSwitch")
    public ResultGenerator getWorkflowSwitch(){
        return ResultGenerator.getSuccessResult(sysConfigService.getWorkflowSwitch());
    }



}
