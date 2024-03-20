package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.SearchIndicatorDto;
import com.asl.prd004.entity.CategoryS;
import com.asl.prd004.service.ICategoryService;
import com.asl.prd004.service.IIndicatorService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/indicator")
public class IndicatorController {

    @Autowired
    IIndicatorService indicatorService;

    @Log("Get all Indicator list.")
    @GetMapping(value = "/getIndicatorList")
    public ResultGenerator getIndicatorList(@RequestBody SearchIndicatorDto data) {
        return ResultGenerator.getSuccessResult(indicatorService.getIndicatorList(data));
    }

    @Log("Get Indicator details.")
    @GetMapping(value = "/getIndicatorDetail")
    public ResultGenerator getIndicatorDetail(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();

        return ResultGenerator.getSuccessResult(indicatorService.getIndicatorDetail(id));
    }

    @Log("Add Indicator.")
    @RequestMapping(value = "/addIndicator", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addIndicator(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        String subCategoryCode = json.getString("subCategoryCode").isEmpty() ? "" : json.getString("subCategoryCode").trim();
        String indicatorCode = json.getString("indicatorCode").isEmpty() ? "" : json.getString("indicatorCode").trim();
        String indicatorNameEn = json.getString("indicatorNameEn").isEmpty() ? "" : json.getString("indicatorNameEn").trim();
        String indicatorNameTc = json.getString("indicatorNameTc").isEmpty() ? "" : json.getString("indicatorNameTc").trim();
        String dataType = json.getString("dataType").isEmpty() ? "" : json.getString("dataType").trim();
        String currency = json.getString("currency").isEmpty() ? "" : json.getString("currency").trim();

        Boolean active = !json.getBoolean("active") ? false : json.getBoolean("active");
        int activeIntValue = active ? 1 : 0;

        if (indicatorCode.length() > 10) {
            return ResultGenerator.getFailResult("The length of indicatorCode must less than 10\n!");
        }
        if (indicatorNameTc.length() > 50) {
            return ResultGenerator.getFailResult("The length of indicatorNameTc must less than 50\n!");
        }
        if (indicatorNameEn.length() > 100) {
            return ResultGenerator.getFailResult("The length of indicatorNameTc must less than 100\n!");
        }

        if (indicatorService.addIndicator(categoryCode, subCategoryCode, indicatorCode,
                indicatorNameEn, indicatorNameTc, dataType, currency, activeIntValue)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Edit Indicator.")
    @RequestMapping(value = "/editIndicator", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator editIndicator(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        String subCategoryCode = json.getString("subCategoryCode").isEmpty() ? "" : json.getString("subCategoryCode").trim();
        String indicatorCode = json.getString("indicatorCode").isEmpty() ? "" : json.getString("indicatorCode").trim();
        String indicatorNameEn = json.getString("indicatorNameEn").isEmpty() ? "" : json.getString("indicatorNameEn").trim();
        String indicatorNameTc = json.getString("indicatorNameTc").isEmpty() ? "" : json.getString("indicatorNameTc").trim();
        String dataType = json.getString("dataType").isEmpty() ? "" : json.getString("dataType").trim();
        String currency = json.getString("currency").isEmpty() ? "" : json.getString("currency").trim();

        Boolean active = !json.getBoolean("active") ? false : json.getBoolean("active");
        int activeIntValue = active ? 1 : 0;

        if (indicatorCode.length() > 10) {
            return ResultGenerator.getFailResult("The length of indicatorCode must less than 10\n!");
        }
        if (indicatorNameTc.length() > 50) {
            return ResultGenerator.getFailResult("The length of indicatorNameTc must less than 50\n!");
        }
        if (indicatorNameEn.length() > 100) {
            return ResultGenerator.getFailResult("The length of indicatorNameTc must less than 100\n!");
        }

        if (indicatorService.editIndicator(id, categoryCode, subCategoryCode, indicatorCode,
                indicatorNameEn, indicatorNameTc, dataType, currency, activeIntValue)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete Indicator item.")
    @RequestMapping(value = "/deleteIndicator", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteIndicator(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        if (indicatorService.deleteIndicator(id)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Get all Indicator list by category code.")
    @GetMapping(value = "/getIndicatorByCategoryCode")
    public ResultGenerator getIndicatorByCategoryCode(@RequestBody String data) throws Exception {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        return ResultGenerator.getSuccessResult(indicatorService.getIndicatorByCategoryCode(categoryCode, lang));
    }

    @Log("Get all Indicator list by sub category code.")
    @GetMapping(value = "/getIndicatorBySubcategoryCode")
    public ResultGenerator getIndicatorBySubcategoryCode(@RequestBody String data) throws Exception {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String subcategoryCode = json.getString("subcategoryCode").isEmpty() ? "" : json.getString("subcategoryCode").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        return ResultGenerator.getSuccessResult(indicatorService.getIndicatorBySubcategoryCode(subcategoryCode, lang));
    }

}
