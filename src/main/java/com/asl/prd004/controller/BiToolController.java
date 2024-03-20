package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.BiToolConfig;
import com.asl.prd004.enums.WfStatusEnum;
import com.asl.prd004.service.BiToolService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/6/5 16:06
 */
@RestController
@RequestMapping("/biTool")
@CrossOrigin(origins = {"http://localhost:3000/"}, allowCredentials = "true", allowedHeaders = {"X-Requested-With"},
        maxAge = 3600L, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
public class BiToolController {
    @Autowired
    private BiToolService biToolService;
    @Log("Query all biTool page data")
    @RequestMapping(value="/getAllBiToolByPage")
    public ResultGenerator getAllBiToolByPage(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            JSONObject  pageState = json.getJSONObject("pageState");
            JSONObject  sort = json.getJSONObject("sortModel");
            PageDataDto biToolList=biToolService.getAllBiToolByPage(pageState,sort);
            return ResultGenerator.getSuccessResult(biToolList);
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("query fail");
        }
    }
    @Log("Query all biTool data")
    @RequestMapping(value="/getAllBiTool")
    public ResultGenerator getAllBiTool(){
            List<BiToolConfig> biToolList=biToolService.getAllBiTool();
            return ResultGenerator.getSuccessResult(biToolList);
    }
    @Log("Query biTool data")
    @RequestMapping(value="/getBiTool")
    public ResultGenerator getBiTool(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            String misBiConfigId = json.getString("misBiConfigId");
            BiToolConfig biTool = biToolService.getBiTool(misBiConfigId);
            return ResultGenerator.getSuccessResult(biTool);
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("query fail");
        }
    }
    @Log("Query table data")
    @RequestMapping(value="/queryTableData")
    public ResultGenerator queryTableData(){
        List<TypeListDto> typeListDtoList=biToolService.queryTableData();
        return ResultGenerator.getSuccessResult(typeListDtoList);
    }
    @Log("Query column data")
    @RequestMapping(value="/queryColumnData")
    public ResultGenerator queryColumnData(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            String typeId=json.getString("misBiConfigTypeId");
            List<DicDto> columnData=biToolService.queryColumnData(typeId);
            return ResultGenerator.getSuccessResult(columnData);
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("query fail");
        }

    }
    @Log("Add bi tool")
    @RequestMapping(value="/addBiTool")
    public ResultGenerator addBiTool(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            boolean saveBiTool= biToolService.editBiTool(json);
            return ResultGenerator.getSuccessResult("save success");
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("save fail");
        }
    }
    @Log("Edit bi tool")
    @RequestMapping(value="/editBiTool")
    public ResultGenerator editBiTool(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            boolean saveBiTool= biToolService.editBiTool(json);
            return ResultGenerator.getSuccessResult("save success");
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("save fail");
        }
    }
    @Log("Del bi tool")
    @RequestMapping(value="/delBiTool")
    public ResultGenerator deleteBiTool(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            boolean delBiTool= biToolService.deleteBiTool(json);
            return ResultGenerator.getSuccessResult("delete success");
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("delete fail");
        }
    }
    @Log("Count table column data")
    @RequestMapping(value="/countTableColumnData")
    public ResultGenerator countTableColumnData(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            List<CountColumnDTO> countColumnDTOS= biToolService.countTableColumnData(json);
            return ResultGenerator.getSuccessResult(countColumnDTOS);
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("save fail");
        }
    }
    @Log("Count table column dataByDate")
    @RequestMapping(value="/countTableColumnDataByDate")
    public ResultGenerator countTableColumnDataByDate(@RequestBody String data){
        try {
            JSONObject json = new JSONObject(data);
            List<CountColumnDTO> countColumnDTOS= biToolService.countTableColumnDataByDate(json);
            return ResultGenerator.getSuccessResult(countColumnDTOS);
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("save fail");
        }
    }
    @Log("Count workFlowData")
    @RequestMapping(value="/countWorkFlowData")
    public  ResultGenerator countWorkFlowData() {
        List<Object[]> workflowData= biToolService.countWorkflowData();
        List<CountColumnDTO> countColumnDTOS = new ArrayList<>();
        for(int i=0;i<workflowData.size();i++){
            CountColumnDTO countColumnDTO = new CountColumnDTO();
            countColumnDTO.setColumnName(WfStatusEnum.getDescByCode((Integer) workflowData.get(i)[0]));
            countColumnDTO.setCountData((BigInteger) workflowData.get(i)[1]);
            countColumnDTOS.add(countColumnDTO);
        }
        return ResultGenerator.getSuccessResult(countColumnDTOS);
    }
    @Log("Count workFlow dataByDate")
    @RequestMapping(value="/countWorkFlowDataByDate")
    public  ResultGenerator countWorkFlowDataByDate(@RequestBody String data) throws JSONException{
        List<Object[]> workflowData= biToolService.countWorkflowDataByDate();
        List<CountColumnDTO> countColumnDTOS = new ArrayList<>();
        for(int i=0;i<workflowData.size();i++){
            CountColumnDTO countColumnDTO = new CountColumnDTO();
            countColumnDTO.setDate(workflowData.get(i)[0].toString());
            countColumnDTO.setColumnName(WfStatusEnum.getDescByCode(Integer.valueOf((String) workflowData.get(i)[1])));
            countColumnDTO.setCountData((BigInteger) workflowData.get(i)[2]);
            countColumnDTOS.add(countColumnDTO);
        }
        return ResultGenerator.getSuccessResult(countColumnDTOS);
    }


}


