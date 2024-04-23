package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.SubCategoryRequestDto;
import com.asl.prd004.entity.SubcategoryS;
import com.asl.prd004.service.ISubcategoryService;
import com.asl.prd004.utils.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subcategory")
public class SubcategoryController {

    @Autowired
    ISubcategoryService subcategoryService;

    @Log("Get Subcategory list.")
    @PostMapping(value="/getAllSubcategory")
    public ResultGenerator getAllSubcategory(@RequestBody SubCategoryRequestDto subCategoryRequestDto) {
        return ResultGenerator.getSuccessResult(subcategoryService.getAllSubcategory(subCategoryRequestDto));
    }

    @Log("Get Subcategory list by Category Code.")
    @PostMapping(value="/getSubcategoryList")
    public ResultGenerator getSubcategory(@RequestBody String data) throws Exception {
        String lang="";
        String categoryCode="";
        if(data!=null && !data.isEmpty()){
            JSONObject json = new JSONObject(data);
            lang=json.getString("lang");
            categoryCode=json.getString("categoryCode");
        }
        List<Object> subcategoryDataList=subcategoryService.getSubcategory(lang,categoryCode);
        return ResultGenerator.getSuccessResult(subcategoryDataList);
    }

    @Log("Get all Subcategory Detail by ID.")
    @PostMapping(value="/getSubcategoryDetail")
    public ResultGenerator getSubcategoryDetail(@RequestBody String data) throws Exception {
        String id="";
        if(data!=null && !data.isEmpty()){
            JSONObject json = new JSONObject(data);
            id=json.getString("id");
        }
        SubcategoryS subcategoryData=subcategoryService.getSubcategoryDetail(id);
        return ResultGenerator.getSuccessResult(subcategoryData);
    }


    @Log("Add Subcategory.")
    @RequestMapping(value="/addSubcategory",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addSubcategory(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String categoryCode= json.getString("categoryCode").isEmpty()?"":json.getString("categoryCode").trim();
        String subcategoryCode= json.getString("subcategoryCode").isEmpty()?"":json.getString("subcategoryCode").trim();
        String subcategoryNameEn=json.getString("subcategoryNameEn").isEmpty()?"":json.getString("subcategoryNameEn").trim();
        String subcategoryNameTc=json.getString("subcategoryNameTc").isEmpty()?"":json.getString("subcategoryNameTc").trim();

        if(subcategoryService.createSubcategory(categoryCode,subcategoryCode,subcategoryNameEn,subcategoryNameTc)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Update subcategory.")
    @RequestMapping(value="/updateSubcategory",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateSubcategory(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id=json.getString("id").isEmpty()?"":json.getString("id").trim();
        String categoryCode= json.getString("categoryCode").isEmpty()?"":json.getString("categoryCode").trim();
        String subcategoryCode=json.getString("subcategoryCode").isEmpty()?"":json.getString("subcategoryCode").trim();
        String subcategoryNameEn=json.getString("subcategoryNameEn").isEmpty()?"":json.getString("subcategoryNameEn").trim();
        String subcategoryNameTc=json.getString("subcategoryNameTc").isEmpty()?"":json.getString("subcategoryNameTc").trim();
        if(subcategoryService.updateSubcategory(id,categoryCode,subcategoryCode,subcategoryNameEn,subcategoryNameTc)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete Subcategory item.")
    @RequestMapping(value="/delSubcategory",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator delSubcategory(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id=json.getString("id").isEmpty()?"":json.getString("id").trim();
        if(subcategoryService.delSubcategory(id)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return ResultGenerator.getFailResult("failed");
        }
    }



}
