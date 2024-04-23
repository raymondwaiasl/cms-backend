package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.service.IConsolidateDataService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consolidateData")
public class ConsolidateDataController {

    @Autowired
    IConsolidateDataService consolidateDataService;

    @PostMapping(value = "/searchConsolidateDataList")
    public ResultGenerator searchConsolidateDataList(@RequestBody String data) throws Exception {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json = new JSONObject(data);

        String categoryCode = json.getString("categoryCode");

        Integer yearStart = json.getInt("yearStart");

        Integer yearEnd = json.getInt("yearEnd");

        String lang = json.getString("lang");

        JSONObject pageState = json.getJSONObject("pageState");

        JSONObject sort = json.getJSONObject("sortModel");

        PageDataDto pageDataDto = consolidateDataService.searchConsolidateDataList(categoryCode, yearStart, yearEnd, lang, pageState, sort);

        return ResultGenerator.getSuccessResult(pageDataDto);
    }

    @PostMapping(value = "/getMOFormDataByMonth")
    public ResultGenerator getMOFormDataByMonth(@RequestBody String data) throws JSONException {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        Integer year = json.getInt("year");

        return ResultGenerator.getSuccessResult(consolidateDataService.getMOFormDataByMonth(categoryCode, year));
    }

   @PostMapping(value = "/getMOFormDataSummary")
    public ResultGenerator getMOFormDataSummary(@RequestBody String data) throws JSONException {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

       String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
       Integer year = json.getInt("year");

        return ResultGenerator.getSuccessResult(consolidateDataService.getMOFormDataSummary(categoryCode, year));
    }

    @PostMapping(value = "/getMOFormDataTotal")
    public ResultGenerator getMOFormDataTotal(@RequestBody String data) throws JSONException {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        Integer year = json.getInt("year");

        return ResultGenerator.getSuccessResult(consolidateDataService.getMOFormDataTotal(categoryCode, year));
    }
}
