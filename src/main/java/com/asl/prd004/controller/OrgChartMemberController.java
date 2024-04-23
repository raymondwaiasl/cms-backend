package com.asl.prd004.controller;


import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.service.IOrgChartMemberService;
import com.asl.prd004.service.IPermissionService;
import com.asl.prd004.service.IUserAdminService;
import com.asl.prd004.utils.CastEntity;
import com.asl.prd004.utils.ConstUtils;
import com.asl.prd004.utils.Log;
import com.asl.prd004.utils.SerialNumberUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 用户角色权限控制类
 */
@RestController
@RequestMapping("/UserRolePerm")
public class OrgChartMemberController {

    @Autowired
    private IOrgChartMemberService orgChartService;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IUserAdminService userAdminService;



    @Log("Query Org chart data.")
    @RequestMapping(value="/findAllOrgChart")
    public ResultGenerator findAllOrgChart(){
        return ResultGenerator.getSuccessResult(orgChartService.findAllOrgChartData());
    }

    @Log("Query member data.")
    @RequestMapping(value="/getMember",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getMember(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String level = json.getString("level");
        String nodeId = json.getString("nodeId");
        JSONObject  pageState = json.getJSONObject("pageState");
        JSONObject  sort = json.getJSONObject("sortModel");
        if(level==null ||"".equals(level)){
            return ResultGenerator.getFailResult("没有传递参数!");
        }else if("root".equals(level)){
            PageDataDto roleDataLs=orgChartService.getMemberByRole(nodeId, pageState,sort);
            return ResultGenerator.getSuccessResult(roleDataLs);
        }else if("1".equals(level)|| ConstUtils.ROLETYPE.equals(level)){
            PageDataDto groupDataLs=orgChartService.getMemberByGroupAndUser(nodeId,pageState,sort);
            return ResultGenerator.getSuccessResult(groupDataLs);
        }else if(ConstUtils.GORUPTYPE.equals(level)){
            PageDataDto userDataLs=orgChartService.getMemberByGroupAndUser(nodeId,pageState,sort);
            return ResultGenerator.getSuccessResult(userDataLs);
        }
        return ResultGenerator.getSuccessResult(orgChartService.findAllOrgChartData());
    }

    @Log("Query member info by role.")
    @RequestMapping(value="/getMemberInfoByRole",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getMemberInfoByRole(@RequestBody String data)throws JSONException {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json = new JSONObject(data);
        String roleId;
        if(json.has("misRoleId")){
            roleId = json.getString("misRoleId");
        }else {
            roleId = json.getString("roleId");
        }
        return ResultGenerator.getSuccessResult(orgChartService.getMemberInfoByRole(roleId));
    }

    @Log("Query group info by id.")
    @RequestMapping(value="/getGroupInfoById",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getGroupInfoById(@RequestBody String data)throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String roleId = json.getString("roleId");
        List<Object[]> orgInfoDTOList=orgChartService.getGroupInfoById(roleId);
        List<OrgInfoDTO> orgInfoDTOS= CastEntity.castEntity(orgInfoDTOList,OrgInfoDTO.class);
        return ResultGenerator.getSuccessResult(orgInfoDTOS);
    }

    @Log("Update member info by role id.")
    @RequestMapping(value="/updateMemberByRole",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateMemberByRole(@RequestBody String data)throws JSONException{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String roleId = json.getString("roleId");
        String roleName = json.getString("roleName");
        String memberIds = json.getString("memberIds");
        orgChartService.insertUserRole(roleId, memberIds);


        /*String[] memberChildIds=new String[]{};
        if("".equals(json.getString("memberIds"))||json.getString("memberIds")==null){

        }else {
            memberChildIds = json.getString("memberIds").split(",");
        }
        int updateFlag=orgChartService.updateRole(roleId,roleName);
        String oldMemberIds=orgChartService.queryOldMemberIds(roleId);
        String[] oldMemberIdsArr=new String[]{};
        if(oldMemberIds==""||oldMemberIds==null){

        }else{
            oldMemberIdsArr=oldMemberIds.split(",");
        }
        Arrays.sort(memberChildIds);
        Arrays.sort(oldMemberIdsArr);
        if(oldMemberIdsArr.length>memberChildIds.length){//旧的数据比编辑之后数据要多，进行清除
            int oldData=oldMemberIdsArr.length-1;
            for(int j=0;j<=oldData;j++){
                if(!Arrays.asList(memberChildIds).contains(oldMemberIdsArr[j])){
                    //parentMemberId=orgChartService.queryParentMemberId(oldMemberIdsArr[j]);
                    orgChartService.deleteMember(roleId,oldMemberIdsArr[j]);
                }
            }
        }
        if(memberChildIds.length>0) {
            for (int i = 0; i < memberChildIds.length; i++) {
                //角色下根据id去判断下是用户组还是用户
                String childType = orgChartService.queryChildType(memberChildIds[i]);
                if (childType == null) {
                    childType = ConstUtils.USERTYPE;//属性为用户
                } else {
                    childType = ConstUtils.GORUPTYPE;//属性为用户组
                }
                String misMemberId = orgChartService.queryMemberId(roleId, memberChildIds[i]);
                if (misMemberId == null) {
                    String memberId = SerialNumberUtils.getTableSequence("mis_member");
                    int insertFlag = orgChartService.insertMember(memberId, roleId, memberChildIds[i], childType);
                } else {
                    int insertFlag = orgChartService.insertMember(misMemberId, roleId, memberChildIds[i], childType);
                }
            }
        }*/
        return ResultGenerator.getSuccessResult("编辑成功!");
    }

    @Log("Insert member info by role.")
    @RequestMapping(value="/insertMemberByRole",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator insertMemberByRole(@RequestBody String data)throws JSONException {
        JSONObject json = new JSONObject(data);
        if (data==null||"".equals(data)) {
            return ResultGenerator.getFailResult("添加失败,参数不能为空!");
        }
            String roleName = json.getString("roleName");
            String[] memberChildIds=new String[]{};
            if("".equals(json.getString("memberIds"))||json.getString("memberIds")==null){

            }else {
                memberChildIds = json.getString("memberIds").split(",");
            }
            String roleId = SerialNumberUtils.getTableSequence(  "mis_role");
            orgChartService.insertRole(roleId, roleName);
            String memberRId = SerialNumberUtils.getTableSequence("mis_member");
            int insertMem = orgChartService.insertMember(memberRId, ConstUtils.RootId, roleId, ConstUtils.ROLETYPE);
            if(memberChildIds.length>0) {
                for (int i = 0; i < memberChildIds.length; i++) {
                    //角色下根据id去判断下是用户组还是用户
                    String childType = orgChartService.queryChildType(memberChildIds[i]);
                    if (childType == null) {
                        childType = ConstUtils.USERTYPE;//属性为用户
                    } else {
                        childType = ConstUtils.GORUPTYPE;//属性为用户组
                    }

                    String memberId = SerialNumberUtils.getTableSequence("mis_member");
                    int insertFlag = orgChartService.insertMember(memberId, roleId, memberChildIds[i], childType);
                }
            }
            return ResultGenerator.getSuccessResult("添加成功!");
        }

    @Log("Query member by group.")
    @RequestMapping(value="/getMemberInfoByGroup",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getMemberInfoByGroup(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String groupId = json.getString("id");
        List<Object[]> orgInfoDTOList=orgChartService.getMemberInfoByGroup(groupId);
        List<OrgInfoDTO> includeUsers= CastEntity.castEntity(orgInfoDTOList,OrgInfoDTO.class);
        List<Object[]> orgInfoDTONoGroupList=orgChartService.getMemberInfoByNoGroup(groupId);
        List<OrgInfoDTO> excludeUsers= CastEntity.castEntity(orgInfoDTONoGroupList,OrgInfoDTO.class);
        HashMap<String, Object> result = new HashMap<>();
        result.put("includeUsers", includeUsers);
        result.put("excludeUsers", excludeUsers);
        return ResultGenerator.getSuccessResult(result);
    }

    @Log("Query user info by id.")
    @RequestMapping(value="/getUserInfoById",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getUserInfoById(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String groupId = json.getString("id");
        List<Object[]> orgInfoDTOList=orgChartService.getMemberInfoByNoGroup(groupId);
        List<OrgInfoDTO> orgInfoDTOS= CastEntity.castEntity(orgInfoDTOList,OrgInfoDTO.class);
        return ResultGenerator.getSuccessResult(orgInfoDTOS);
    }

    @Log("Update member info by group.")
    @RequestMapping(value="/updateMemberByGroup",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateMemberByGroup(@RequestBody String data)throws JSONException{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String groupId = json.getString("groupId");
        String groupName = json.getString("groupName");
        String isAdmin = json.getString("isAdmin");
        String defaultFolderId = json.getString("defaultFolderId");
        String[] memberChildIds=null;
        if((json.getString("memberIds")==null) ||"".equals(json.getString("memberIds"))){

        }else{
            memberChildIds=json.getString("memberIds").split(",");
        }
        int updateFlag=orgChartService.updateGroup(groupId,groupName,isAdmin,defaultFolderId);
        //如果编辑的数据比之前的成员要少，则要删除多余的数据
        String oldMemberIds=orgChartService.queryOldMemberIdsByGroup(groupId);
        //逻辑判断:组下之前有成员,同时新增加成员,且旧比新增加的多或旧比新增加的少;组下之前没成员,现新增加成员;组下之前没成员,现也没增加;
        if(oldMemberIds!=null&&memberChildIds!=null) {
            String[] oldMemberIdsArr = oldMemberIds.split(",");
            Arrays.sort(memberChildIds);
            Arrays.sort(oldMemberIdsArr);
            for (int j = 0; j < oldMemberIdsArr.length; j++) {
                if (!Arrays.asList(memberChildIds).contains(oldMemberIdsArr[j])) {
                    orgChartService.deleteMemberByGroup(groupId, oldMemberIdsArr[j]);
                }
            }
        }else if(oldMemberIds!=null&&memberChildIds==null){
            String[] oldMemberIdsArr = oldMemberIds.split(",");
            for (int j = 0; j < oldMemberIdsArr.length; j++) {
                 orgChartService.deleteMemberByGroup(groupId, oldMemberIdsArr[j]);
            }
        }
        if(memberChildIds!=null) {
            for (int i = 0; i < memberChildIds.length; i++) {
                String childType = ConstUtils.USERTYPE;//属性为用户
                String misMemberId = orgChartService.queryMemberIdByGroup(groupId, memberChildIds[i]);
                if (misMemberId == null) {
                    String memberId = SerialNumberUtils.getTableSequence("mis_member");
                    int insertFlag = orgChartService.insertMember(memberId, groupId, memberChildIds[i], childType);
                } else {
                    int insertFlag = orgChartService.insertMember(misMemberId, groupId, memberChildIds[i], childType);
                }
            }
        }
        return ResultGenerator.getSuccessResult("编辑成功!");
    }

    @Log("Query user info by group.")
    @RequestMapping(value="/getUserInfoByGroup")
    public ResultGenerator getUserInfoByGroup() throws Exception{

        List<Object[]> orgInfoDTOList=orgChartService.getUserInfoByGroup();
        List<OrgInfoDTO> orgInfoDTOS= CastEntity.castEntity(orgInfoDTOList,OrgInfoDTO.class);
        return ResultGenerator.getSuccessResult(orgInfoDTOS);
    }

    @Log("Query user info by group.")
    @RequestMapping(value="/queryUserInfo")
    public ResultGenerator queryUserInfo(@RequestBody String data) throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String groupId = json.getString("nodeId");
        List<MisUser> userInfoList=orgChartService.queryUserInfo(groupId);
        return ResultGenerator.getSuccessResult(userInfoList);
    }


    @Log("Insert member info by group.")
    @RequestMapping(value="/insertMemberByGroup",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator insertMemberByGroup(@RequestBody String data)throws JSONException{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String groupName = json.getString("groupName");
        String isAdmin = json.getString("isAdmin");
        String defaultFolderId = json.getString("defaultFolderId");
        String nodeId = json.getString("nodeId");
        String level=json.getString("level");
        String[] memberChildIds=new String[]{};
        if(!"".equals(json.getString("memberIds")) && json.getString("memberIds") != null){
            memberChildIds = json.getString("memberIds").split(",");
        }
        String groupId= SerialNumberUtils.getTableSequence(  "mis_group");
        //如果它上级是组,判断下它的父级有几级,防止无限组中有组,目前暂定组的父组不能超过6级,超过6级不能新创建组,或者父级下一级组默认不能超过10个
        if(level.equals(ConstUtils.GORUPTYPE)){
           int levelData=orgChartService.queryGroupLevel(nodeId);
           int childGroupData=orgChartService.queryChildGroupData(nodeId);
           if(levelData>6||childGroupData>10){
               return ResultGenerator.getFailResult("group level exceed limit");
           }
        }
        int updateFlag=orgChartService.insertGroup(groupId,nodeId,groupName, isAdmin, defaultFolderId);
        String memberId= SerialNumberUtils.getTableSequence(  "mis_member");
        int insertFlag=orgChartService.insertMemberByGroup(memberId,nodeId,ConstUtils.ROLETYPE,groupId,ConstUtils.GORUPTYPE);//新增的组插入成员表
        for(int i=0;i<memberChildIds.length;i++){
            String childType= ConstUtils.USERTYPE;//属性为用户
            String memberNewId= SerialNumberUtils.getTableSequence(  "mis_member");
            int insertValue=orgChartService.insertMember(memberNewId,groupId,memberChildIds[i],childType);
        }
        return ResultGenerator.getSuccessResult("新增成功!");
    }


    @Log("Delete group user.")
    @RequestMapping(value="/delGroupUser",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator delGroupUser(@RequestBody String data)throws JSONException{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String id = json.getString("id");
        String type = json.getString("type");
        String nodeId = json.getString("nodeId");
        if(type!=null&& ConstUtils.GORUPTYPE.equals(type)){//删除用户是组，则递归删除组下面的用户
            String childNodeIds=orgChartService.queryChildNodeIds(id);
            if(childNodeIds!=null){
                String []childNodeAllIds=childNodeIds.split(",");
                for(int j=0;j<childNodeAllIds.length;j++){
                    int delGroupUser=orgChartService.delGroupUser(id,childNodeAllIds[j]);
                }
            }
            int delGroup=orgChartService.delGroupUser(nodeId,id);
        }else{
            int delUser=orgChartService.delGroupUser(nodeId,id);
        }
        permissionService.deletePermissionByUserId(id);//这个用户组对应的权限也删除
        return ResultGenerator.getSuccessResult("删除成功!");
    }

    @Log("Query user info by user id.")
    @RequestMapping(value="/getUserInfoByUserId",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getUserInfoByUserId(@RequestBody String data) throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        String id;
        JSONObject json =new JSONObject(data);
        if(json.has("nodeId")){
            id = json.getString("nodeId");
        }else {
            id = json.getString("id");
            //String type = json.getJSONObject("type").getString("type");
            //String nodeId = json.getJSONObject("nodeId").getString("nodeId");
        }
            List<MisUser> userList = orgChartService.getUserInfoByUserId(id);
            return ResultGenerator.getSuccessResult(userList);
    }

    @Log("Update user info.")
    @RequestMapping(value="/updateUserInfo",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateUserInfo(@RequestBody String data) throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String misUserId = "";
        if(!json.has("misUserId")){
            //misUserId =SerialNumberUtils.getTableSequence(  "mis_user");
        }else{
            misUserId = json.getString("misUserId");
        }
        String misUserLoginId = json.getString("misUserLoginId");
        String misUserName = json.getString("misUserName");
        String misUserLocation = json.getString("misUserLocation");
        String misUserPassword = json.getString("misUserPassword");
        String misEmail = json.getString("misEmail");
        String nodeId = json.getString("nodeId");
        String level ="";
        if(json.has("level")){
            level = json.getString("level");
        }
        MisUser misUser=new MisUser();
        misUser.setMisUserId(misUserId);
        misUser.setMisUserLoginId(misUserLoginId);
        misUser.setMisUserName(misUserName);
        misUser.setMisUserLocation(misUserLocation);
        misUser.setMisUserPassword(misUserPassword);
        misUser.setMisEmail(misEmail);
        String misNewUserId=userAdminService.updateUserInfo(misUser);
        if(level!=null&&ConstUtils.GORUPTYPE.equals(level)&&!json.has("misUserId")){
            String memberId= SerialNumberUtils.getTableSequence(  "mis_member");
            int insertFlag=orgChartService.insertMemberByGroup(memberId,nodeId,ConstUtils.GORUPTYPE,misUserId==""?misNewUserId:misUserId,ConstUtils.USERTYPE);//新增的组插入成员表
        }
        userAdminService.updateUserInfo(misUser);
        return ResultGenerator.getSuccessResult("编辑成功!");
    }

    @Log("Delete role.")
    @RequestMapping(value="/delRole",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator delRole(@RequestBody String data)throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        String roleId = json.getString("misRoleId");
        String childNodeIds=orgChartService.queryChildNodeIds(roleId);
        if(childNodeIds!=null){//循环删除该角色下子成员
            String []childNodeAllIds=childNodeIds.split(",");
            for(int j=0;j<childNodeAllIds.length;j++){
                int delGroupUser=orgChartService.delGroupUser(roleId,childNodeAllIds[j]);
            }
        }
        int delGroup=orgChartService.delGroupUser(ConstUtils.RootId,roleId);
        int delRole=orgChartService.delRoleById(roleId);
        return ResultGenerator.getSuccessResult("删除成功!");
    }

    @Log("Query group data.")
    @PostMapping(value = "/queryGroupData")
    public ResultGenerator queryGroupData(@RequestBody TypeIdDto id) throws Exception {
        if ("3".equals(id.getId())) {
            List<Object[]> orgInfoDTOList = orgChartService.queryAllGroup();
            List<OrgInfoDTO> orgInfoDTOS = CastEntity.castEntity(orgInfoDTOList, OrgInfoDTO.class);
            return ResultGenerator.getSuccessResult(orgInfoDTOS);
        } else {
            List<Object[]> orgInfoDTOList = orgChartService.queryAllUser();
            List<OrgInfoDTO> orgInfoDTOS = CastEntity.castEntity(orgInfoDTOList, OrgInfoDTO.class);
            return ResultGenerator.getSuccessResult(orgInfoDTOS);
        }
    }



    @Log("Query all user info.")
    @RequestMapping(value="/queryAllUserInfo",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator queryAllUserInfo()throws Exception{
        List<MisUser> misUserList=orgChartService.queryAllUserInfo();
        return ResultGenerator.getSuccessResult(misUserList);
    }

    @Log("Get Group Data by userId.")
    @RequestMapping(value="/getGroupsByUserId",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getGroupsByUserId(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(orgChartService.getGroupsByUserId(dto.getId()));
    }

    @Log("Get My Profile by userId.")
    @RequestMapping(value="/getMyProfileByUserId",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getMyProfileByUserId(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(orgChartService.getMyProfileByUserId(dto.getId()));
    }
    @Log("Query All group data.")
    @PostMapping(value = "/queryAllGroupData")
    public ResultGenerator queryAllGroupData(@RequestBody String data) throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        //String id = json.getString("id");
        JSONObject  pageState = json.getJSONObject("pageState");
        JSONObject  sort = json.getJSONObject("sortModel");
        PageDataDto groupList = orgChartService.queryAllGroupData(pageState,sort);
        return ResultGenerator.getSuccessResult(groupList);
    }


}
