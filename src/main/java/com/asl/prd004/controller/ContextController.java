package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.RoleDao;
import com.asl.prd004.dto.ContextDTO;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.WorkspaceListPageDto;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.IContextService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/context")
public class ContextController {


    @Resource
    private IContextService contextService;

    @Autowired
    private RoleDao roleDao;

    @Log("Find context list.")
    @RequestMapping(value="/findContextList",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator<PageDataDto> findContextList(@RequestBody PageableDto pageable){
        return ResultGenerator.getSuccessResult(contextService.findContextList(pageable));
    }

    @Log("Find workspace list.")
    @RequestMapping(value="/findWorkspaceList")
    public ResultGenerator findWorkspaceList(){
        List<WorkspaceListPageDto> list = contextService.findWorkspaceList();
        return ResultGenerator.getSuccessResult(list);
    }

    @Log("Find role list.")
    @RequestMapping(value="/findRoleList")
    public ResultGenerator findRoleList(){
        return ResultGenerator.getSuccessResult(roleDao.findAll());
    }

    @Log("Add context.")
    @RequestMapping(value="/addContext", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addContext(@RequestBody String data) throws JSONException {
        //解析接受到的json数据
        JSONObject json =new JSONObject(data);
        String contextName = json.getString("contextName");
        String roleId = json.getString("roleId");
        String wsId = json.getString("wsId");
        boolean flag = contextService.addContext(contextName,roleId,wsId);
        return ResultGenerator.getSuccessResult(flag);
    }

    @Log("Update context.")
    @RequestMapping(value="/updateContext", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateContext(@RequestBody String data) throws JSONException {
        //解析接受到的json数据
        JSONObject json =new JSONObject(data);
        String contextId = json.getString("contextId");
        String contextName = json.getString("contextName");
        String roleId = json.getString("roleId");
        String wsId = json.getString("wsId");
        boolean flag = contextService.updateContext(contextId,contextName,roleId,wsId);
        return ResultGenerator.getSuccessResult(flag);
    }

    @Log("Get context detail.")
    @RequestMapping(value="/detailContext", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator detailContext(@RequestBody String data) throws JSONException {
        //解析接受到的json数据
        JSONObject json =new JSONObject(data);
        String misContextId = json.getString("misContextId");
        MisContext misContext = contextService.findById(misContextId);
        List<MisContextDetail> detailList = contextService.findDetailByContextId(misContextId);
        List<WorkspaceListPageDto> rightList = new ArrayList<>();
        for (int i = 0; i < detailList.size(); i++) {
            WorkspaceListPageDto misWorkspace = contextService.findWorkspaceById(detailList.get(i).getMisContextWsId());
            rightList.add(misWorkspace);
        }
        List<WorkspaceListPageDto> list = contextService.findWorkspaceList();
        list.removeAll(rightList);
        List<WorkspaceListPageDto> leftList = list;

        Map<String,Object> map = new HashMap<>();
        map.put("misContext",misContext);
        map.put("leftList",leftList);
        map.put("rightList",rightList);
        return ResultGenerator.getSuccessResult(map);
    }

    @Log("Delete context.")
    @RequestMapping(value="/deleteContext", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteContext(@RequestBody String param) throws JSONException {
        JSONObject json =new JSONObject(param);
        String id = json.getString("id");
        return ResultGenerator.getSuccessResult(contextService.deleteContext(id));
    }

}
