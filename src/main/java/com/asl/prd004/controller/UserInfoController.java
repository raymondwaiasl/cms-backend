package com.asl.prd004.controller;

import com.asl.prd004.config.DefinitionException;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.EmailDto;
import com.asl.prd004.dto.ResetPasswordDto;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.dto.UserDto;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.service.IUserInfoService;
import com.asl.prd004.utils.ActiveDirectory;
import com.asl.prd004.utils.JwtUtil;
import com.asl.prd004.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.ldap.LdapContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
//@CrossOrigin(origins = {"http://localhost:3000/"},allowCredentials = "true")
@RequestMapping("/userInfo")
public class UserInfoController {

    @Autowired
    IUserInfoService userInfoService;
    @Log("User login")
    @PostMapping(value="/login")
    public ResultGenerator<Map<String,Object>> login(@RequestBody UserDto dto){
        return userInfoService.verifyLogin(dto);
//        Date date = new Date();
//        Date nextDate = new  Date(date.getTime() + JwtUtil.EXPIRE_SECONDS);
//        if(null != user){
//            HashMap<String, Object> map = new HashMap<>(2);
//            map.put("userId",user.getMisUserId());
//            map.put("userLoginId",user.getMisUserLoginId());
//            String token = JwtUtil.generateToken(map);
//            HashMap<String, Object> resultMap = new HashMap<>(2);
//            resultMap.put("token",token);
//            resultMap.put("expire_time",nextDate);
//            resultMap.put("user_name",user.getMisUserName());
//            return ResultGenerator.getSuccessResult(resultMap);
//        }else{
//            try{
//                LdapContext ctx = ActiveDirectory.getConnection(dto.getLoginName(), dto.getPwd(),"d2.test","192.168.50.144:389/CN=Users,DC=d2,DC=test");
//                ctx.close();
//                HashMap<String, Object> map = new HashMap<>();
//                //question login name == user id ?
//                map.put("userId",dto.getLoginName());
//                String token = JwtUtil.generateToken(map);
//                HashMap<String, Object> resultMap = new HashMap<>(2);
//                resultMap.put("token",token);
//                resultMap.put("expire_time",nextDate);
//                return ResultGenerator.getSuccessResult(resultMap);
//            }catch(Exception e){
//                //Failed to authenticate user!
//                e.printStackTrace();
//                throw new DefinitionException(401, "Authentication failed!");
//            }
//        }

    }

    @Log("Get userinfo")
    @PostMapping(value="/getuserinfo")
    public ResultGenerator<Map<String,Object>> getUserinfo(){
        String userId= ContextHolder.getUserId();
        MisUser user = userInfoService.getUserInfo(userId);
        HashMap<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("mis_user_name", user.getMisUserName());
        resultMap.put("avtar", user.getAvatar());
        return  ResultGenerator.getSuccessResult(resultMap);
    }

    @Log("Forget password")
    @PostMapping(value="/forgotPwd")
    public ResultGenerator<Map<String,Object>> forgotPwd(@RequestBody EmailDto dto){
        return userInfoService.forgotPwd(dto.getEmail());
    }

    @Log("Reset password")
    @PostMapping(value="/reset")
    public ResultGenerator<Map<String,Object>> reset(@RequestBody ResetPasswordDto dto){
        return userInfoService.resetPwd(dto);
    }

    @Log("Get Group Default Folder By GroupId")
    @PostMapping(value="/getGroupDefaultFolderByGroupId")
    public ResultGenerator<String> getGroupDefaultFolderByGroupId(){
        return ResultGenerator.getSuccessResult(userInfoService.getGroupDefaultFolderByGroupId());
    }

    @Log("Get Password Policy")
    @RequestMapping(value="/getPasswordPolicy")
    public ResultGenerator getPasswordPolicy(){
        return userInfoService.getPasswordPolicy();
    }

}

