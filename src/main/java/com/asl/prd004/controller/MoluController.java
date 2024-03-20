package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.SearchMoluOfficeDto;
import com.asl.prd004.service.IMoluService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/molu")
public class MoluController {

    @Autowired
    IMoluService moluService;

    @Log("Get all Molu list.")
    @GetMapping(value = "/getMoluList")
    public ResultGenerator getMoluList(@RequestBody SearchMoluOfficeDto data) {
        return ResultGenerator.getSuccessResult(moluService.getMoluList(data));
    }

    @Log("Get Molu details.")
    @GetMapping(value = "/getMoluDetail")
    public ResultGenerator getMoluDetail(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();

        return ResultGenerator.getSuccessResult(moluService.getMoluDetail(id));
    }

    @Log("Get Molu by moluType.")
    @GetMapping(value = "/getMoluByType")
    public ResultGenerator getMoluByType(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String moluType = json.getString("moluType").isEmpty() ? "" : json.getString("moluType").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        return ResultGenerator.getSuccessResult(moluService.getMoluByType(moluType, lang));
    }

    @Log("Get Molu by moCode.")
    @GetMapping(value = "/getMoluByMOCode")
    public ResultGenerator getMoluByMOCode(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        return ResultGenerator.getSuccessResult(moluService.getMoluByMOCode(moCode, lang));
    }

    @Log("Add Molu.")
    @RequestMapping(value = "/addMolu", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addMolu(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String moluCode = json.getString("moluCode").isEmpty() ? "" : json.getString("moluCode").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();
        String moluType = json.getString("moluType").isEmpty() ? "" : json.getString("moluType").trim();
        String moluNameEn = json.getString("moluNameEn").isEmpty() ? "" : json.getString("moluNameEn").trim();
        String moluNameTc = json.getString("moluNameTc").isEmpty() ? "" : json.getString("moluNameTc").trim();
        Boolean active = !json.getBoolean("active") ? false : json.getBoolean("active");
        int activeIntValue = active ? 1 : 0;

        if (moluCode.length() > 10 || moCode.length() > 10) {
            return ResultGenerator.getFailResult("The length of moluCode or moCode must less than 10\n!");
        }
        if (moluNameTc.length() > 50) {
            return ResultGenerator.getFailResult("The length of moluNameTc must less than 50\n!");
        }
        if (moluNameEn.length() > 100) {
            return ResultGenerator.getFailResult("The length of moluNameEn must less than 100\n!");
        }

        // when addMolu need check mo_code is unique
        List<Object> listsByMoCode = moluService.getMoluByMOCode(moCode, "EN");
        if (listsByMoCode.size() > 0) {
            return ResultGenerator.getFailResult("mo code need be unique\n!");
        }

        if (moluService.addMolu(moluCode, moCode, moluType, moluNameEn, moluNameTc, activeIntValue)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Update Molu.")
    @RequestMapping(value = "/editMolu", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator editMolu(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        String moluCode = json.getString("moluCode").isEmpty() ? "" : json.getString("moluCode").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();
        String moluType = json.getString("moluType").isEmpty() ? "" : json.getString("moluType").trim();
        String moluNameEn = json.getString("moluNameEn").isEmpty() ? "" : json.getString("moluNameEn").trim();
        String moluNameTc = json.getString("moluNameTc").isEmpty() ? "" : json.getString("moluNameTc").trim();
        Boolean active = !json.getBoolean("active") ? false : json.getBoolean("active");
        int activeIntValue = active ? 1 : 0;

        if (moluCode.length() > 10 || moCode.length() > 10) {
            return ResultGenerator.getFailResult("The length of moluCode or moCode must less than 10\n!");
        }
        if (moluNameTc.length() > 50) {
            return ResultGenerator.getFailResult("The length of moluNameTc must less than 50\n!");
        }
        if (moluNameEn.length() > 100) {
            return ResultGenerator.getFailResult("The length of moluNameEn must less than 100\n!");
        }

        if (moluService.editMolu(id, moluCode, moCode, moluType, moluNameEn, moluNameTc, activeIntValue)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete Molu item.")
    @RequestMapping(value = "/deleteMolu", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteMolu(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        if (moluService.deleteMolu(id)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

}
