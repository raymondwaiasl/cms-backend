package com.asl.prd004.service;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.ChangePasswordDTO;
import com.asl.prd004.dto.ResetPasswordDto;
import com.asl.prd004.dto.UserDto;
import com.asl.prd004.entity.MisUser;

public interface IUserInfoService {

    ResultGenerator verifyLogin(UserDto dto);

    MisUser getUserInfo(String userId);

    ResultGenerator forgotPwd(String email);

    ResultGenerator resetPwd(ResetPasswordDto dto);

    String getGroupDefaultFolderByGroupId();

    ResultGenerator getPasswordPolicy();





}
