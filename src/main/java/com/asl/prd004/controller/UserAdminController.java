package com.asl.prd004.controller;

import cn.hutool.core.util.StrUtil;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.ChangeAccountSettingDTO;
import com.asl.prd004.dto.ChangePasswordDTO;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.SearchUserDto;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.service.IPermissionService;
import com.asl.prd004.service.IUserAdminService;
import com.asl.prd004.utils.AESUtil;
import com.asl.prd004.utils.Log;
import com.asl.prd004.utils.SerialNumberUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/userAdmin")
public class UserAdminController {

    @Autowired
    private IUserAdminService userAdminService;

    @Autowired
    private IPermissionService permissionService;

    @Log("Update user data.")
    @RequestMapping(value="/editUserData",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator editUserData(@RequestBody String data)throws Exception{
        try {
            if(data==null||"".equals(data)){
                return ResultGenerator.getFailResult("Parameter is empty!");
            }
            JSONObject json =new JSONObject(data);
            String misUserId = "";
            if(!json.has("misUserId")){
                misUserId = SerialNumberUtils.getTableSequence(  "mis_user");
            }else{
                misUserId = json.getString("misUserId");
            }
            String misUserLoginId = json.getString("misUserLoginId");
            String misUserName = json.getString("misUserName");
            String surnameEng = json.getString("surnameEng");
            String givenNameEng = json.getString("givenNameEng");
            String otherNameEng = json.getString("otherNameEng");
            String district = json.getString("district");
            String tel = json.getString("tel");
            String fax = json.getString("fax");
            String misUserLocation = json.getString("misUserLocation");
            String misUserPassword = json.getString("misUserPassword");
            String misEmail = json.getString("misEmail");
            String isAdmin = json.getString("isAdmin");
            String misUserStatus = json.getString("misUserStatus");
//            String isChange = json.getString("isChange");
            String currentGroup = "";
            if(json.has("currentGroup")){
                currentGroup = json.getString("currentGroup");
            }
            String isLocked = json.getString("isLocked");
            MisUser misUser=new MisUser();
            misUser.setMisUserId(misUserId);
            misUser.setMisUserLoginId(misUserLoginId==null?"":misUserLoginId);
            misUser.setMisUserName(misUserName==null?"":misUserName);
            misUser.setSurnameEng(surnameEng==null?"":surnameEng);
            misUser.setGivenNameEng(givenNameEng==null?"":givenNameEng);
            misUser.setOtherNameEng(otherNameEng==null?"":otherNameEng);
            misUser.setDistrict(district == null ? "" : district);
            misUser.setTel(tel==null?"":tel);
            misUser.setFax(fax==null?"":fax);
            misUser.setMisUserLocation(misUserLocation==null?"":misUserLocation);
            misUser.setMisUserPassword(misUserPassword==null?"": AESUtil.encryptAES(misUserPassword));
            misUser.setMisEmail(misEmail==null?"":misEmail);
            misUser.setIsAdmin("Y".equals(isAdmin) ? isAdmin : "N");
            misUser.setLocked(isLocked.equals("Y") ? true : false);
            misUser.setMisUserStatus(misUserStatus);
            misUser.setCurrentGroup(currentGroup);
            misUser.setCreateBy(ContextHolder.getUserId());
            misUser.setCreateTime(Timestamp.valueOf((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis())));
            String misNewUserId=userAdminService.updateUserInfo(misUser);
        } catch (JSONException e) {
            e.printStackTrace();
            return ResultGenerator.getFailResult("Edit failed\n!");
        }
        return ResultGenerator.getSuccessResult("Edit data successfully!");
    }


    @Log("Delete user data.")
    @RequestMapping(value="/delUser",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator delUser(@RequestBody String data)throws Exception{
        try {
            if(data==null||"".equals(data)){
                return ResultGenerator.getFailResult("Parameter is empty!");
            }
            JSONObject json =new JSONObject(data);
            String misUserId=json.getString("misUserId");
            // cannot delete itself
            if(misUserId.equals(ContextHolder.getUserId())){
                return ResultGenerator.getFailResult("Delete Failed!");
            }
            userAdminService.delUser(misUserId);
            userAdminService.deleteMemberByUserId(misUserId);//删除用户，同时也要删除成员表的用户
            permissionService.deletePermissionByUserId(misUserId);//这个用户对应的权限也删除
        } catch (JSONException e) {
            e.printStackTrace();
            return ResultGenerator.getFailResult("Edit failed\n!");
        }
        return ResultGenerator.getSuccessResult("删除成功");
    }


    @Log("Query user data.")
    @RequestMapping(value="/queryUserAll",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator queryUserAll(@RequestBody String data)throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty!");
        }
        JSONObject json = new JSONObject(data);
        String  misUserName = json.getString("misUserName");
        JSONObject  pageState = json.getJSONObject("pageState");
        JSONObject  sort = json.getJSONObject("sortModel");
        PageDataDto userDto=userAdminService.queryUserAll(misUserName,pageState,sort);
        return ResultGenerator.getSuccessResult(userDto);
    }

    @Log("Change Account Setting.")
    @RequestMapping(value="/getAccountSetting",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getAccountSetting()throws Exception{
        return ResultGenerator.getSuccessResult(userAdminService.getAccountSetting());
    }

    @Log("Change Account Setting.")
    @RequestMapping(value="/changeAccountSetting",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator changeAccountSetting(@RequestBody ChangeAccountSettingDTO data)throws Exception{
        if(StrUtil.isBlank(data.getLoginName())){
            return ResultGenerator.getFailResult("LoginName is empty!");
        }
        if(StrUtil.isBlank(data.getUserName())){
            return ResultGenerator.getFailResult("UserName is empty!");
        }
        if(StrUtil.isBlank(data.getEmailAddress())){
            return ResultGenerator.getFailResult("EmailAddress is empty!");
        }
        return userAdminService.changeAccountSetting(data);
    }

    @Log("Change Password")
    @RequestMapping(value="/changePassword",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator changePassword(@RequestBody ChangePasswordDTO data)throws Exception{
        if(StrUtil.isBlank(data.getCurrentPassword())){
            return ResultGenerator.getFailResult("CurrentPassword is empty!");
        }
        if(StrUtil.isBlank(data.getNewPassword())){
            return ResultGenerator.getFailResult("NewPassword is empty!");
        }
        if(StrUtil.isBlank(data.getConfirmPassword())){
            return ResultGenerator.getFailResult("ConfirmPassword is empty!");
        }
        if(!StrUtil.equals(data.getNewPassword(),data.getConfirmPassword())){
            return ResultGenerator.getFailResult("The new password and confirmed password are different!");
        }
        return userAdminService.changePassword(data);
    }

    @Log("Search user.")
    @RequestMapping(value="/searchUser",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator searchUser(@RequestBody SearchUserDto data){
        PageDataDto userDto = userAdminService.searchUser(data);
        return ResultGenerator.getSuccessResult(userDto);
    }
}
