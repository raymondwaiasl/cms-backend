package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.CategoryS;
import com.asl.prd004.service.ICategoryService;
import com.asl.prd004.service.IIndicatorService;
import com.asl.prd004.utils.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    ICategoryService categoryService;

    @Log("Get all Category list.")
    @PostMapping(value = "/getAllCategory")
    public ResultGenerator getAllCategory(@RequestBody PageableDto pageable) {
        return ResultGenerator.getSuccessResult(categoryService.getAllCategory(pageable));
    }

    @Log("Get all Category list by lang.")
    @PostMapping(value = "/getCategoryList")
    public ResultGenerator getCategory(@RequestBody String data) throws Exception {
        String lang = "";
        if (data != null && !data.isEmpty()) {
            JSONObject json = new JSONObject(data);
            lang = json.getString("lang");
        }
        List<Object> categoryDataList = categoryService.getCategory(lang);
        return ResultGenerator.getSuccessResult(categoryDataList);
    }

    @Log("Get all Category Detail by ID.")
    @PostMapping(value="/getCategoryDetail")
    public ResultGenerator getCategoryDetail(@RequestBody String data) throws Exception {
        String id="";
        if(data!=null && !data.isEmpty()){
            JSONObject json = new JSONObject(data);
            id=json.getString("id");
        }
        CategoryS categoryData=categoryService.getCategoryDetail(id);
        return ResultGenerator.getSuccessResult(categoryData);
    }



    @Log("Add Category.")
    @RequestMapping(value = "/addCategory", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addCategory(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        String categoryNameEn = json.getString("categoryNameEn").isEmpty() ? "" : json.getString("categoryNameEn").trim();
        String categoryNameTc = json.getString("categoryNameTc").isEmpty() ? "" : json.getString("categoryNameTc").trim();
        String yearType = json.getString("yearType").isEmpty() ? "" : json.getString("yearType").trim();
        if (categoryService.createCategory(categoryCode, categoryNameEn, categoryNameTc, yearType)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Update category.")
    @RequestMapping(value = "/updateCategory", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateCategory(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        String categoryNameEn = json.getString("categoryNameEn").isEmpty() ? "" : json.getString("categoryNameEn").trim();
        String categoryNameTc = json.getString("categoryNameTc").isEmpty() ? "" : json.getString("categoryNameTc").trim();
        String yearType = json.getString("yearType").isEmpty() ? "" : json.getString("yearType").trim();

        if (categoryService.updateCategory(id, categoryCode, categoryNameEn, categoryNameTc, yearType)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete Category item.")
    @RequestMapping(value = "/delCategory", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator delCategory(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        if (categoryService.delCategory(id)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

}
