package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.SearchIndicatorTargetDto;
import com.asl.prd004.entity.IndicatorsTargetS;
import com.asl.prd004.service.IIndicatorTargetService;
import com.asl.prd004.utils.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/indicatorTarget")
public class IndicatorTargetController {

    @Autowired
    IIndicatorTargetService indicatorTargetService;

    @Log("Get all Indicator Target list.")
    @GetMapping(value = "/getIndicatorTargetList")
    public ResultGenerator getIndicatorTargetList(@RequestBody SearchIndicatorTargetDto data) {
        return ResultGenerator.getSuccessResult(indicatorTargetService.getIndicatorTargetList(data));
    }

    @Log("Get Indicator Target details.")
    @GetMapping(value = "/getIndicatorTargetDetail")
    public ResultGenerator getIndicatorTargetDetail(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();

        return ResultGenerator.getSuccessResult(indicatorTargetService.getIndicatorTargetDetail(id));
    }

    @Log("Add Indicator Target.")
    @RequestMapping(value = "/addIndicatorTarget", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addIndicatorTarget(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONArray jsonArray = new JSONArray(data);

        ArrayList<IndicatorsTargetS> targetList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            String indCode = json.getString("indCode").isEmpty() ? "" : json.getString("indCode").trim();
            String moluCode = json.getString("moluCode").isEmpty() ? "" : json.getString("moluCode").trim();
            String year = json.getString("year").isEmpty() ? "" : json.getString("year").trim();
            String target = json.getString("target").isEmpty() ? "" : json.getString("target").trim();
            Integer yearInt = Integer.parseInt(year);
            Double targetDouble = Double.parseDouble(target);

            if (moluCode.length() > 10 || indCode.length() > 10) {
                return ResultGenerator.getFailResult("The length of moluCode or indCode must less than 10\n!");
            }

            IndicatorsTargetS indicatorsTargetS = new IndicatorsTargetS();
            indicatorsTargetS.setIndCode(indCode);
            indicatorsTargetS.setMoluCode(moluCode);
            indicatorsTargetS.setYear(yearInt);
            indicatorsTargetS.setTarget(targetDouble);
            targetList.add(indicatorsTargetS);
        }

        List<IndicatorsTargetS> collect = targetList.stream().distinct().collect(Collectors.toList());

        if (collect.size() != targetList.size()) {
            return ResultGenerator.getFailResult("year and indicator need unique @1\n!");
        }

        // when addIndicatorTarget: “year” and “indicator” need unique
        for (int i = 0; i < collect.size(); i++) {
            if (indicatorTargetService.checkIndicatorTargetByIndCodeAndYear(collect.get(i).getIndCode(), collect.get(i).getYear())) {
                return ResultGenerator.getFailResult("year and indicator need unique @2\n!");
            }
        }

        if (indicatorTargetService.bitchAddIndicatorTarget(collect)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Update Indicator Target.")
    @RequestMapping(value = "/editIndicatorTarget", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator editIndicatorTarget(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        String indCode = json.getString("indCode").isEmpty() ? "" : json.getString("indCode").trim();
        String moluCode = json.getString("moluCode").isEmpty() ? "" : json.getString("moluCode").trim();
        String year = json.getString("year").isEmpty() ? "" : json.getString("year").trim();
        String target = json.getString("target").isEmpty() ? "" : json.getString("target").trim();
        Integer yearInt = Integer.parseInt(year);
        Double targetDouble = Double.parseDouble(target);

        if (moluCode.length() > 10 || indCode.length() > 10) {
            return ResultGenerator.getFailResult("The length of moluCode or indCode must less than 10\n!");
        }

        if (indicatorTargetService.editIndicatorTarget(id, indCode, moluCode, yearInt, targetDouble)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete Indicator Target item.")
    @RequestMapping(value = "/deleteIndicator", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteIndicator(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        if (indicatorTargetService.deleteIndicator(id)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

}
