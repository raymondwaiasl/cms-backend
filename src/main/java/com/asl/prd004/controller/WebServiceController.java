package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.ColumnInterDTO;
import com.asl.prd004.dto.SubscriptionBuDto;
import com.asl.prd004.dto.TypeListInterDto;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.*;
import com.asl.prd004.utils.CastEntity;
import com.asl.prd004.utils.JwtUtil;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ws")
public class WebServiceController {

    @Resource
    private IFolderService folderService;

    @Resource
    private ISubscriptionService subscriptionService;

    @Resource
    private IPermissionService permissionService;

    @Resource
    private MisPermissionDetailDao permissionDetailDao;

    @Resource
    private GroupDao groupDao;

    @Resource
    private MisUserDao userDao;

    @Resource
    private IAuditLogService iAuditLogService;

    @Autowired
    IRecordMangeService recordMange;
    @Resource
    private IRenditionService renditionService;
    @Log("Create folder")
    @RequestMapping(value = "/FolderService/create", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator createFolder(@RequestBody String param, HttpServletRequest request) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misFolderParentId = json.getString("MIS_PARENT_FOLDER_ID");
        String misFolderName = json.getString("MIS_FOLDER_NAME");
        String misPermissionId = json.getString("MIS_PERMISSION_ID");
        String token = request.getHeader("authorization");
        Map<String, Object> map = JwtUtil.resolveToken(token);
        String userId = map.get("userId").toString();
        //是否有写的权限
        Boolean flag = folderService.isPermission(misFolderParentId, userId, "5");
        if (!flag) {
            return new ResultGenerator(-3, "No permission to link the selected parent folder", "");
        }
        //是否存在相同的folder
        List<MisFolder> list = folderService.findByMisFolderParentId(misFolderParentId);
        if (list.size() > 0) {
            for (MisFolder misFolder : list) {
                if (misFolder.getMisFolderName().equals(misFolderName)) {
                    return new ResultGenerator(-4, "Folder already exists", "");
                }
            }
        }
        MisFolder misFolder = folderService.createFolder(misFolderParentId, misFolderName, misPermissionId);
        //insert to subscription msg
        List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId("0000000000000000", misFolderParentId);
        if (!subscriptionList.isEmpty()) {
            for (SubscriptionBuDto subscription : subscriptionList) {
                if (subscription.getMisSubEventMsg().equals("New")) {
                    subscriptionService.insertMsg(subscription.getMisSubscriptionId(), subscription.getMisSubscriptionEventId());
                }
            }
        }
        return new ResultGenerator(0, "Folder Created", misFolder.getMisFolderId());
    }
    @Log("Update folder")
    @RequestMapping(value = "/FolderService/update", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateFolder(@RequestBody String param, HttpServletRequest request) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misFolderId = json.getString("MIS_FOLDER_ID");
        String misFolderName = json.getString("MIS_FOLDER_NAME");
        String misPermissionId = json.getString("MIS_PERMISSION_ID");
        String token = request.getHeader("authorization");
        Map<String, Object> map = JwtUtil.resolveToken(token);
        String userId = map.get("userId").toString();
        //是否有写的权限
        Boolean flag = folderService.isPermission(misFolderId, userId, "5");
        if (!flag) {
            return new ResultGenerator(-3, "No permission to link the selected parent folder", "");
        }
        //是否存在相同的folder
        List<MisFolder> list = folderService.findByMisFolderName(misFolderName);
        if (list.size() > 0) {
            for (MisFolder misFolder : list) {
                if (misFolder.getMisFolderName().equals(misFolderName) && !misFolder.getMisFolderId().equals(misFolderId)) {
                    return new ResultGenerator(-4, "Folder already exists", "");
                }
            }
        }
        folderService.updateFolder(misFolderId, misFolderName);
        //insert to subscription msg
        List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId("0000000000000000", misFolderId);
        if (!subscriptionList.isEmpty()) {
            for (SubscriptionBuDto subscription : subscriptionList) {
                if (subscription.getMisSubEventMsg().equals("Modified")) {
                    subscriptionService.insertMsg(subscription.getMisSubscriptionId(), subscription.getMisSubscriptionEventId());
                }
            }
        }
        return new ResultGenerator(0, "Folder updated", "");
    }

    @Log("Change link")
    @RequestMapping(value = "/FolderService/changelink", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator changelink(@RequestBody String param, HttpServletRequest request) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misFolderId = json.getString("MIS_FOLDER_ID");
        String link = json.getString("LINK");
        String token = request.getHeader("authorization");
        Map<String, Object> map = JwtUtil.resolveToken(token);
        String userId = map.get("userId").toString();
        //是否有写的权限
        Boolean flag = folderService.isPermission(misFolderId, userId, "5");
        if (!flag) {
            return new ResultGenerator(-3, "No permission to link the selected parent folder", "");
        }
        MisFolder folder  = folderService.getMisFolderById(link);
        if(null == folder){
            return new ResultGenerator(-5, "the link is not exists", "");
        }
        folderService.updateFolderParentId(misFolderId,link);
        return new ResultGenerator(0, "Folder link(s) changed", null);
    }
    @Log("Delete folder")
    @RequestMapping(value = "/FolderService/delete", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteFolder(@RequestBody String param, HttpServletRequest request) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misFolderId = json.getString("MIS_FOLDER_ID");
        String token = request.getHeader("authorization");
        Map<String, Object> map = JwtUtil.resolveToken(token);
        String userId = map.get("userId").toString();
        MisFolder misFolder = folderService.getMisFolderById(misFolderId);
        if(null == misFolder){
            return new ResultGenerator(-3, "the folder is not exist", "");
        }
        List<MisFolder> parentFolder = folderService.findByMisFolderParentId(misFolderId);
        if(parentFolder.size() >0){
            return new ResultGenerator(-4, "the folder has subfolders and cannot be deleted", "");
        }
        //是否有删的权限
        Boolean flag = folderService.isPermission(misFolderId, userId, "7");
        if (!flag) {
            return new ResultGenerator(-5, "No permission to link the selected parent folder", "");
        }
        folderService.deleteFolder(misFolderId);
        //insert to subscription msg
        if (misFolder.getMisFolderParentId() != null && !misFolder.getMisFolderParentId().equals("0") && flag) {
            List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId("0000000000000000", misFolder.getMisFolderParentId());
            if (!subscriptionList.isEmpty() && flag) {
                for (SubscriptionBuDto subscription : subscriptionList) {
                    if (subscription.getMisSubEventMsg().equals("Deleted")) {
                        subscriptionService.insertMsg(subscription.getMisSubscriptionId(), subscription.getMisSubscriptionEventId());
                    }
                }
            }
        }
        return new ResultGenerator(0, "Folder deleted", "");
    }
    @Log("Search folder")
    @RequestMapping(value = "/FolderService/search", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator searchFolder(@RequestBody String param) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misFolderId = json.getString("MIS_FOLDER_ID");
        String misFolderName = json.getString("MIS_FOLDER_NAME");
        String misIsIncludeChild = json.getString("MIS_IS_INCLUDE_CHILD");
        List<MisFolder> list = folderService.findByMisFolderIdAndMisFolderName(misFolderId, misFolderName);
        List<MisFolder> result = new ArrayList<>();
        if ("Y".equals(misIsIncludeChild)) {
            for (MisFolder misFolder : list) {
                List<MisFolder> childFolder = folderService.findByMisFolderParentId(misFolder.getMisFolderId());
                result.addAll(childFolder);
            }
        }
        list.addAll(result);
        List<Map<String, Object>> returnList = new ArrayList<>();
        for (MisFolder misFolder : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("MIS_FOLDER_ID",misFolder.getMisFolderId());
            map.put("MIS_FOLDER_NAME",misFolder.getMisFolderName());
            map.put("MIS_FOLDER_FULL_PATH",misFolder.getMisFolderFullPath());
            map.put("MIS_FOLDER_PARENT_ID",misFolder.getMisFolderParentId());
            map.put("MIS_PERMISSION_ID",misFolder.getMisPermissionId());
            returnList.add(map);
        }
        return ResultGenerator.getSuccessResult(returnList);
    }
    @Log("Create permission")
    @RequestMapping(value = "/PermissionService/create", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator createPermission(@RequestBody String param) throws JSONException {
        JSONObject json = new JSONObject(param);
        
        String misPermissionName = json.getString("MIS_PERMISSION_NAME");
        String detail = json.getString("DETAIL");
        String result = permissionService.createPermission(misPermissionName,detail);
        if("-3".equals(result)){
            return new ResultGenerator(-3, "Group or user not exists", "");
        }else if("-4".equals(result)){
            return new ResultGenerator(-4, "Invalid permission", "");
        }
        return ResultGenerator.getSuccessResult(result);
    }
    @Log("Update permission")
    @RequestMapping(value = "/PermissionService/update", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updatePermission(@RequestBody String param) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misPermissionId = json.getString("MIS_PERMISSION_ID");
        String detail = json.getString("DETAIL");
        String result = permissionService.updatePermission(misPermissionId,detail);
        if("-3".equals(result)){
            return new ResultGenerator(-3, "Group or user not exists", "");
        }else if("-4".equals(result)){
            return new ResultGenerator(-4, "Invalid permission", "");
        }else if("-5".equals(result)){
            return new ResultGenerator(-5, "Permission not exists", "");
        }
        return new ResultGenerator(0, "Permission updated", "");
    }
    @Log("Delete permission")
    @RequestMapping(value = "/PermissionService/delete", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deletePermission(@RequestBody String param) throws JSONException{

        JSONObject json = new JSONObject(param);
       
        String misPermissionId = json.getString("MIS_PERMISSION_ID");
        String result = permissionService.deletePermission(misPermissionId);
        if("-5".equals(result)){
            return new ResultGenerator(-5, "Permission not exists", "");
        }
        return new ResultGenerator(0, "Permission deleted", "");
    }
    /**
    * @description: Append Permission of Member
    * @author: billy
    * @date: 2023/1/17 14:14
    * @param: [param]
    * @return: com.asl.prd004.config.ResultGenerator
    **/
    @Log("Append permission member")
    @RequestMapping(value = "/PermissionService/appendMember", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator appendMember(@RequestBody String param) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misPermissionId = json.getString("MIS_PERMISSION_ID");
        String detail = json.getString("DETAIL");
        String result = permissionService.appendMember(misPermissionId,detail);
        if("-3".equals(result)){
            return new ResultGenerator(-3, "Group or user not exists", "");
        }else if("-4".equals(result)){
            return new ResultGenerator(-4, "Invalid give permission", "");
        }else if("-5".equals(result)){
            return new ResultGenerator(-5, "Permission not exists", "");
        }
        return ResultGenerator.getSuccessResult(result);
    }
    /**
     * @description: Remove Permission of Member
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Remove permission member")
    @RequestMapping(value = "/PermissionService/removeMember", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator removeMember(@RequestBody String param) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misPermissionId = json.getString("MIS_PERMISSION_ID");
        String detail = json.getString("DETAIL");
        String result = permissionService.removeMember(misPermissionId,detail);
        if("-3".equals(result)){
            return new ResultGenerator(-3, "Group or user not exists", "");
        }else if("-4".equals(result)){
            return new ResultGenerator(-4, "Invalid give permission", "");
        }else if("-5".equals(result)){
            return new ResultGenerator(-5, "Permission not exists", "");
        }
        return ResultGenerator.getSuccessResult(result);
    }
    /**
     * @description: Create Audit
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Create audit")
    @RequestMapping(value = "/AuditService/create", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator createAudit(@RequestBody String param) throws Exception {
        JSONObject json = new JSONObject(param);
        String result = iAuditLogService.createAudit(json);
        if("-3".equals(result)){
            return new ResultGenerator(-3, "user not exists", "");
        }else if("-4".equals(result)){
            return new ResultGenerator(-4, "table not exists", "");
        }else if("-1".equals(result)){
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }
        return ResultGenerator.getSuccessResult(result);
    }
    /**
     * @description: Search Record Type
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Search record")
    @RequestMapping(value = "/RecordService/create", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator searchRecord(@RequestBody String param) {
        List<TypeListInterDto> typeListInterDTOS= null;
        try {
            JSONObject json = new JSONObject(param);
            List<Object[]> result = recordMange.searchRecord(json);
            typeListInterDTOS = CastEntity.castEntity(result, TypeListInterDto.class);
        } catch (Exception e) {
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }
        return ResultGenerator.getSuccessResult(typeListInterDTOS);
    }
    /**
     * @description: Get Record Type
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Get record type")
    @RequestMapping(value = "/RecordService/getRecordType", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getRecordType(@RequestBody String param) {
        List<ColumnInterDTO> columnListInterDTOS=null;
        try {
            JSONObject json = new JSONObject(param);
            String misTypeId=json.getString("MIS_TYPE_ID");
            if(param==null||"".equals(param)||misTypeId==null||"".equals(misTypeId)){
                return new ResultGenerator(-3, "Invalid id", "");
            }

            List<Object[]> result = recordMange.getRecordType(json);
            columnListInterDTOS = CastEntity.castEntity(result, ColumnInterDTO.class);
        } catch (Exception e) {
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }

        return ResultGenerator.getSuccessResult(columnListInterDTOS);
    }
    /**
     * @description: Create Record
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Create record")
    @RequestMapping(value = "/RecordService/CreateRecord", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator createRecord(@RequestBody String param,HttpServletRequest request) {
        try {
            JSONObject json = new JSONObject(param);
            if(param==null||"".equals(param)){
                return new ResultGenerator(-4, "Invalid id", "");
            }
            String result=recordMange.createRecord(json,request);
            return ResultGenerator.getSuccessResult(result);
        } catch (Exception e) {
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }
    }
    /**
     * @description: Update Record
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Update record")
    @RequestMapping(value = "/RecordService/UpdateRecord", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateRecord(@RequestBody String param,HttpServletRequest request) {
        try {
            JSONObject json = new JSONObject(param);
            if(param==null||"".equals(param)){
                return new ResultGenerator(-4, "Invalid id", "");
            }
            String result=recordMange.updateRecord(json,request);
            return ResultGenerator.getSuccessResult(result);
        } catch (Exception e) {
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }
    }
    /**
     * @description: Change Record Link
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Change record link")
    @RequestMapping(value = "/RecordService/ChangeRecordLink", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator changeRecordLink(@RequestBody String param,HttpServletRequest request) {
        try {
            JSONObject json = new JSONObject(param);
            if(param==null||"".equals(param)){
                return new ResultGenerator(-3, "Invalid id", "");
            }
            String result=recordMange.changeRecordLink(json,request);
            return ResultGenerator.getSuccessResult(result);
        } catch (Exception e) {
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }
    }
    /**
     * @description: Delete Record
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Delete record")
    @RequestMapping(value = "/RecordService/DeleteRecord", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteRecord(@RequestBody String param,HttpServletRequest request) {
        try {
            JSONObject json = new JSONObject(param);
            if(param==null||"".equals(param)){
                return new ResultGenerator(-3, "Invalid id", "");
            }
            String result=recordMange.deleteRecord(json,request);
            return ResultGenerator.getSuccessResult(result);
        } catch (Exception e) {
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }
    }
    /**
     * @description: Search Record Data
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Search record")
    @RequestMapping(value = "/RecordService/searchRecord", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator searchRecordData(@RequestBody String param) {
        try {
            JSONObject json = new JSONObject(param);
            if(param==null||"".equals(param)){
                return new ResultGenerator(-3, "Invalid id", "");
            }
            List result=recordMange.searchRecordData(json);

            return ResultGenerator.getSuccessResult(result);
        } catch (Exception e) {
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }
    }
    /**
     * @description: Get Record
     * @author: billy
     * @date: 2023/1/17 14:14
     * @param: [param]
     * @return: com.asl.prd004.config.ResultGenerator
     **/
    @Log("Get record")
    @RequestMapping(value = "/RecordService/getRecord", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getRecord(@RequestBody String param) {
        try {
            JSONObject json = new JSONObject(param);
            String misTypeId=json.getString("MIS_TYPE_ID");
            String misRecordId=json.getString("MIS_RECORD_ID");
            if(param==null||"".equals(param)||misTypeId==null||misRecordId==null){
                return new ResultGenerator(-3, "Invalid id", "");
            }
            List result=recordMange.getRecord(json);

            return ResultGenerator.getSuccessResult(result);
        } catch (Exception e) {
            return new ResultGenerator(-1, "Service Invocation Fail", "");
        }
    }
    @Log("Get cmsRenditions by recordId")
    @RequestMapping(value = "/rendition/getCmsRenditionsByMisRecordId", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getCmsRenditionsByMisRecordId(@RequestBody String param) {
        try {
            JSONObject json = new JSONObject(param);
            String recordId = json.getString("recordId");
            List<CmsRendition> list = renditionService.getCmsRenditionsByMisRecordId(recordId);
            if(list.size()>0){
                return ResultGenerator.getSuccessResult(list.get(0).getCmsRenditionFile());
            }
        } catch (Exception e) {
            return new ResultGenerator(-1, "getCmsRenditionsByMisRecordId Fail", "");
        }
        return ResultGenerator.getFailResult("");
    }
    @Log("Save rendition")
    @RequestMapping(value = "/rendition/saveRendition", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator saveRendition(@RequestBody String param) {
        try {
            JSONObject json = new JSONObject(param);
            String recordId = json.getString("recordId");
            String typeId = json.getString("typeId");
            String image = json.getString("image");
            String format = json.getString("format");
            renditionService.saveRendition(recordId,typeId,image,format);
            return ResultGenerator.getSuccessResult(true);
        } catch (Exception e) {
            return new ResultGenerator(-1, "saveRendition Fail", "");
        }
    }
}
