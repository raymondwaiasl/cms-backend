package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PermissionDTO;
import com.asl.prd004.dto.PermissionInfoDTO;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.service.IOrgChartMemberService;
import com.asl.prd004.service.IPermissionService;
import com.asl.prd004.utils.CastEntity;
import com.asl.prd004.utils.ConstUtils;
import com.asl.prd004.utils.Log;
import com.asl.prd004.utils.SerialNumberUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/userPermission")
public class PermissionController {
    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IOrgChartMemberService orgChartService;

    @Log("Get all permission.")
    @RequestMapping(value="/getAllPermission")
    public ResultGenerator getAllPermission(@RequestBody String data) throws Exception{
        JSONObject json = new JSONObject(data);
        String typeName=json.getString("typeName");
        String permName=json.getString("permName");
        List<Object[]> permissionInfoDTOList=permissionService.getAllPermission(typeName,permName);
        List<PermissionInfoDTO> permissionInfoDTOS= CastEntity.castEntity(permissionInfoDTOList,PermissionInfoDTO.class);
        return ResultGenerator.getSuccessResult(permissionInfoDTOS);
    }

    @Log("Delete permission.")
    @RequestMapping(value="/delPermission",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator delPermission(@RequestBody String data)throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String permissionId = json.getString("id");
        int delPerm=permissionService.delPermission(permissionId);
        int delPermValue=permissionService.delPermissionValue(permissionId);
        return ResultGenerator.getSuccessResult("删除成功!");
    }

    @Log("Save permission data.")
    @RequestMapping(value = "/savePermissionData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator savePermissionData(@RequestBody PermissionDTO permission) {
        if (permissionService.savePermission(permission)) {
            return ResultGenerator.getSuccessResult("operation success");
        } else {
            return ResultGenerator.getFailResult("operation fail");
        }
    }

    @Log("Query folder permission.")
    @RequestMapping(value = "/queryFolderPermission", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator queryPermissionData(@RequestBody TypeIdDto data){
        return ResultGenerator.getSuccessResult(permissionService.queryPermissionByFolderId(data.getId()));
    }

    @Log("Query record permission.")
    @RequestMapping(value = "/queryRecordPermission", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator queryRecordPermissions(@RequestBody String data)  throws Exception{
        JSONObject json = new JSONObject(data);
        String typeId=json.getString("typeId");
        String recordId=json.getString("recordId");
        return ResultGenerator.getSuccessResult(permissionService.queryPermissionByRecordId(typeId, recordId));
    }

    @Log("Insert permission.")
    @RequestMapping(value="/insertPermissionData",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator insertPermissionData(@RequestBody String data)throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String perMissionName = json.getString("perMissionName");
        String rightData = json.getString("rightData");
        String memberIds = json.getString("memberIds");
        String folderPer = json.getString("folderPer");
        if(!(memberIds!=null && "".equals(memberIds))){
            memberIds=memberIds.substring(0,memberIds.length()-1);
        }
        String oldmisPermissionId=permissionService.queryMisPermissionId(perMissionName,folderPer);
        String misPermissionId ="";
        if(oldmisPermissionId==""||oldmisPermissionId==null) {
            misPermissionId = SerialNumberUtils.getTableSequence("mis_permission");
        }else{
            misPermissionId=oldmisPermissionId;
        }
        int insertPermission=permissionService.insertMisPermission(misPermissionId,perMissionName,folderPer);
        if(memberIds!=null&&!"".equals(memberIds)) {
            String[] memberIdsArr=memberIds.split(",");
            for(int j=0;j<memberIdsArr.length;j++) {
                String childType = orgChartService.queryChildType(memberIdsArr[j]);
                if (childType == null) {
                    childType = ConstUtils.USERTYPE;//属性为用户
                } else {
                    childType = ConstUtils.GORUPTYPE;//属性为用户组
                }
                String oldmisPermissionDetailId=permissionService.queryMisPermissionDetailId(misPermissionId,childType,memberIdsArr[j],rightData);
                String misPerDetailId ="";
                if(oldmisPermissionDetailId==""||oldmisPermissionDetailId==null) {
                    misPerDetailId= SerialNumberUtils.getTableSequence(  "mis_permission_detail");
                }else{
                    misPerDetailId=oldmisPermissionDetailId;
                }
                int insertPermDetail=permissionService.insertPermDetail(misPerDetailId,misPermissionId,childType,memberIdsArr[j],rightData);
            }
        }
        return ResultGenerator.getSuccessResult("编辑数据成功!");
    }

}
