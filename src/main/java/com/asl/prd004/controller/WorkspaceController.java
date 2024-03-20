package com.asl.prd004.controller;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.ContextDetailDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisContextDetail;
import com.asl.prd004.entity.MisWorkspace;
import com.asl.prd004.service.IWorkspaceSevice;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workspace")
public class WorkspaceController {

    @Autowired
    IWorkspaceSevice workspaceSevice;

    @Autowired
    ContextDetailDao contextDetailDao;


    @Log("Get workspace list.")
    @PostMapping(value = "/getWorkspaceListPageable")
    public  ResultGenerator<PageDataDto> getWorkspaceListPageable(@RequestBody PageableDto pageable) {
        return ResultGenerator.getSuccessResult(workspaceSevice.getWorkspaceListPageable(pageable));
    }

    @Log("Get workspace widget.")
    @PostMapping(value="/selectWorkspaceWidgetById")
    public ResultGenerator<WorkspaceDto> selectWorkspaceWidgetById(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(workspaceSevice.selectWorkspaceWidgetById(dto.getId()));
    }

    @Log("Add workspace.")
    @PostMapping(value = "/addWorkspace")
    public ResultGenerator addWorkspace(@RequestBody WorkspaceDto dto) {

        if (workspaceSevice.addWorkspace(dto)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }


    @Log("Edit workspace.")
    @PostMapping(value = "/editWorkspace")
    public ResultGenerator<String> editWorkspace(@RequestBody WorkspaceDto dto) {
        if (workspaceSevice.editWorkspace(dto)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete workspace.")
    @PostMapping(value = "/deleteWorkspaceById")
    public ResultGenerator deleteWorkspaceById(@RequestBody TypeIdDto dto) {
        workspaceSevice.deleteWorkspaceById(dto.getId());
        return ResultGenerator.getSuccessResult("success");
    }

    @Log("Get context by user id.")
    @RequestMapping(value = "/getContextByUserId")
    public ResultGenerator<List<MenuItemDto>> getContextByUserId() {
        String userId= ContextHolder.getUserId();
        return ResultGenerator.getSuccessResult(workspaceSevice.getContextByUserId(userId));
    }

    @Log("Find context detail by id.")
    @RequestMapping(value = "/findContextDetailByWsId")
    public ResultGenerator findContextDetailByWsId(@RequestBody String data) throws JSONException {
        JSONObject json = new JSONObject(data);
        String id = json.getString("id");
        List<MisContextDetail> list = contextDetailDao.findDetailByWsId(id);
        return ResultGenerator.getSuccessResult(list);
    }

    @Log("Find workspace parent candidate by id.")
    @RequestMapping(value = "/findParentCandidateById")
    public ResultGenerator findParentCandidateById(@RequestBody MenuItemDto dto){
        return ResultGenerator.getSuccessResult(workspaceSevice.findParentCandidateById(dto.getId()));
    }

    @Log("Set workspace parent.")
    @RequestMapping(value = "/setParent")
    public ResultGenerator setParent(@RequestBody MenuItemDto dto){
        workspaceSevice.setParent(dto);
        return ResultGenerator.getSuccessResult(null);
    }

}
