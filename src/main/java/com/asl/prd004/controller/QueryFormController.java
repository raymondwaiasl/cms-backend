package com.asl.prd004.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.MisColumnDao;
import com.asl.prd004.dao.MisCrossRefDao;
import com.asl.prd004.dao.MisQueryFormDao;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisCrossRef;
import com.asl.prd004.entity.MisQueryForm;
import com.asl.prd004.entity.MisQueryFormCondition;
import com.asl.prd004.service.IQueryFormService;
import com.asl.prd004.service.IRecordService;
import com.asl.prd004.utils.Log;
import com.github.wenhao.jpa.Specifications;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/queryForm")
public class QueryFormController {


    @Autowired
    MisQueryFormDao queryFormDao;

    @Autowired
    MisTypeDao misTypeDao;

    @Autowired
    MisColumnDao misColumnDao;

    @Autowired
    IQueryFormService queryFormService;

    @Autowired
    IRecordService recordService;

    @Log("Get type dictionary.")
    @RequestMapping(value="/getTypeDic")
    public ResultGenerator getTypeDic(){
        return ResultGenerator.getSuccessResult(misTypeDao.getTypeDic());
    }

    @Log("Get query from list.")
    @RequestMapping(value="/getQueryFormList")
    public ResultGenerator getQueryFormList(){
        return ResultGenerator.getSuccessResult(queryFormService.getQueryList());
    }

    @Log("Get type dictionary by id.")
    @RequestMapping(value="/getTypeDicById",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getTypeDicById(@RequestBody String data) throws JSONException {
        JSONObject json = new JSONObject(data);
        String typeId = json.getString("typeId");
        return ResultGenerator.getSuccessResult(misTypeDao.getTypeDicById(typeId));
    }

    @Log("Get query form by id.")
    @PostMapping(value="/getQueryFormById")
    public ResultGenerator<MisQueryFormDTO> getQueryFormById(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(queryFormService.getQueryFormById(dto.getId()));
    }

    @Log("Get column dictionary.")
    @PostMapping(value="/getColumnDic")
    public ResultGenerator getColumnDic(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(
                recordService.findByMisTypeId(dto.getId(),dto.getAllowSearch())
                        .stream().map(c -> new DicDto(c.getMisColumnId(),c.getMisColumnLabel(),c.getMisColumnAllowSearch()))
                        .collect(Collectors.toList()));
    }

    @Log("Get checked column.")
    @PostMapping(value="/getCheckedColumn")
    public ResultGenerator getCheckedColumn(@RequestBody String data) throws JSONException {
        JSONObject json = new JSONObject(data);
        String typeId = json.getString("typeId");
        String qfId = json.getString("qfId");
        List<Object[]> list = misColumnDao.getCheckedColumn(typeId,qfId);
        List<ColumnDTO> columnDTOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ColumnDTO columnDTO = new ColumnDTO();
            columnDTO.setColumnId((String)list.get(i)[0]);
            columnDTO.setColumnName((String)list.get(i)[1]);
            columnDTO.setChecked((String)list.get(i)[2]);
            columnDTOList.add(columnDTO);
        }
        return ResultGenerator.getSuccessResult(columnDTOList);
    }

    @Log("Get checked condition.")
    @PostMapping(value="/getCheckedCondition")
    public ResultGenerator getCheckedCondition(@RequestBody String data) throws JSONException {
        JSONObject json = new JSONObject(data);
        String qfId = json.getString("qfId");
        QueryFormDto queryFormDto = queryFormService.selectQueryFormById(qfId);
        List<MisQueryFormCondition> list  = queryFormDto.getQfConditions();
        return ResultGenerator.getSuccessResult(list);
    }


    @Log("Get query list.")
    @PostMapping(value="/getQueryList")
    public ResultGenerator getQueryList(@RequestBody PageableDto pageable){
        return ResultGenerator.getSuccessResult(queryFormService.getAllQueryForms(pageable));
    }


    @Log("Get query form by id.")
    @PostMapping(value="/selectQueryFormById")
    public ResultGenerator selectQueryFormById(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(queryFormService.selectQueryFormById(dto.getId()));
    }

    @Log("Add query form.")
    @PostMapping(value="/addQueryForm")
    public ResultGenerator addQueryForm(@RequestBody MisQueryFormDTO dto){
        if(queryFormService.addQueryForm(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Edit query form.")
    @PostMapping(value="/editQueryFormByMisQueryForm")
    public ResultGenerator editQueryFormByMisQueryForm(@RequestBody MisQueryFormDTO dto){
        if(queryFormService.editQueryForm(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Save search form.")
    @PostMapping(value="/saveSearchForm")
    public ResultGenerator saveSearchForm(@RequestBody MisQueryFormDTO dto){
        dto.setMisQfId("");
        if(CollectionUtil.isNotEmpty(dto.getQfConditions())){
            dto.getQfConditions().forEach(c -> {c.setMisQfId("");c.setMisQfc2Id("");});
        }
        if(CollectionUtil.isNotEmpty(dto.getQfColumns())){
            dto.getQfColumns().forEach(c -> {c.setMisQfId("");c.setMisQfcId("");});

        }
        if(CollectionUtil.isNotEmpty(dto.getCrossRef())){
            dto.getCrossRef().forEach(cr -> {
                cr.setMisQfId("");
                if(CollectionUtil.isNotEmpty(cr.getQfConditions())){
                    cr.getQfConditions().forEach(c -> {c.setMisQfId("");c.setMisQfc2Id("");});
                }
                if(CollectionUtil.isNotEmpty(cr.getQfColumns())){
                    cr.getQfColumns().forEach(c -> {c.setMisQfId("");c.setMisQfcId("");});
                }
            });
        }

        if(queryFormService.addQueryForm(dto)){
            return ResultGenerator.getSuccessResult(dto.getMisQfId());
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Edit query form.")
    @PostMapping(value="/editQueryForm")
    public ResultGenerator editQueryForm(@RequestBody QueryFormDto dto){
        if(queryFormService.editQueryForm(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete query form.")
    @PostMapping(value="/deleteQueryForm")
    public ResultGenerator deleteQueryForm(@RequestBody TypeIdDto dto){
        if(queryFormService.deleteQueryForm(dto.getId())){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

}
