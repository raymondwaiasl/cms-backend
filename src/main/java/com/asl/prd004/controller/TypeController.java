package com.asl.prd004.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.MisColumnDao;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisType;
import com.asl.prd004.service.ITypeService;
import com.asl.prd004.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/type")
public class TypeController {

    @Autowired
    MisTypeDao misTypeDao;

    @Autowired
    MisColumnDao misColumnDao;

    @Autowired
    ITypeService misTypeService;
    @Log("Query all typesNoWith")
    @PostMapping(value="/queryAllTypesNoWith")
    public ResultGenerator getAllTypes(){
        return ResultGenerator.getSuccessResult(misTypeService.getAllTypes());
    }

    @Log("Get all types.")
    @PostMapping(value="/getAllTypes")
    public ResultGenerator<PageDataDto> getAllTypes(@RequestBody PageableDto page){
        return ResultGenerator.getSuccessResult(misTypeService.getAllTypes(page));
    }

    @Log("Select type by id.")
    @PostMapping(value="/selectTypeById")
    public ResultGenerator selectTypeById(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(misTypeService.selectTypeById(dto.getId()));
    }

    @Log("Add new type.")
    @PostMapping(value="/addNewType")
    public ResultGenerator addNewType(@RequestBody MisType dto){
        TypeIdDto typeIdDto = misTypeService.addNewType(dto);
        return ResultGenerator.getSuccessResult(typeIdDto);
    }

    @Log("Update type.")
    @PostMapping(value="/updateType")
    public ResultGenerator updateType(@RequestBody MisType dto){
        if(misTypeService.updateType(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete Type.")
    @PostMapping(value="/deleteType")
    public ResultGenerator deleteType(@RequestBody TypeIdDto dto){
        if(misTypeService.deleteType(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Add new column.")
    @PostMapping(value="/addNewColumn")
    public ResultGenerator addNewColumn(@RequestBody MisColumnDTO dto){
        MisColumn entity = BeanUtil.copyProperties(dto, MisColumn.class);
        if(Objects.nonNull(dto.getMisColumnComputeFormula())){
            entity.setMisColumnComputeFormula(JSON.toJSONString(dto.getMisColumnComputeFormula()));
        }
        if(misTypeService.addNewColumn(entity)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Update column.")
    @PostMapping(value="/updateColumn")
    public ResultGenerator updateColumn(@RequestBody MisColumnDTO dto){
        MisColumn entity = BeanUtil.copyProperties(dto, MisColumn.class);
        if(Objects.nonNull(dto.getMisColumnComputeFormula())){
            entity.setMisColumnComputeFormula(JSON.toJSONString(dto.getMisColumnComputeFormula()));
        }
        if(misTypeService.updateColumn(entity)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete column.")
    @PostMapping(value="/deleteColumn")
    public ResultGenerator deleteColumn(@RequestBody TypeIdDto dto){
        if(misTypeService.deleteColumn(dto)){
            return ResultGenerator.getSuccessResult("success");
        }else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Get dictionary list.")
    @RequestMapping(value = "/getDicList")
    public ResultGenerator getDicList() {
        return ResultGenerator.getSuccessResult(misTypeService.getDicList());
    }

     /**
     * @description:根据typeId查询对应列字段数据
     * @author: billy
     * @date: 2022/11/29 16:56
     * @param: [dto]
     * @return: com.asl.prd004.common.utils.result.ResponseBody
     **/
     @Log("Query columnByTypeId")
    @PostMapping(value="/queryColumnByTypeId")
    public ResultGenerator queryColumnByTypeId(@RequestBody TypeIdDto dto){
        if(dto.getId()==null){
            ResultGenerator.getFailResult("参数不能为空!");
        }
        String[] idArr=dto.getId().split("-");
        return ResultGenerator.getSuccessResult(misTypeService.queryColumnByTypeId(idArr[0]));
    }
    @Log("Add new type.")
    @PostMapping(value="/addNewTypeAndDraf")
    public ResultGenerator addNewTypeAndDraf(@RequestBody TypeAndDrafDto dto){
        TypeIdDto typeIdDto = misTypeService.addNewTypeAndDraf(dto);
        return ResultGenerator.getSuccessResult(typeIdDto);
    }
}
