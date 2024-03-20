package com.asl.prd004.service;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.ChangeAccountSettingDTO;
import com.asl.prd004.dto.ChangePasswordDTO;
import com.asl.prd004.dto.PageDataDto;

import com.asl.prd004.dto.SearchUserDto;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.vo.AccountSettingVO;
import org.json.JSONObject;


public interface IUserAdminService {

    String updateUserInfo(MisUser misUser);

    void delUser(String misUserId);

    void deleteMemberByUserId(String misUserId);

    PageDataDto queryUserAll(String misUserName, JSONObject... params);

    ResultGenerator changeAccountSetting(ChangeAccountSettingDTO data);

    ResultGenerator changePassword(ChangePasswordDTO data) throws Exception;

    AccountSettingVO getAccountSetting();

    PageDataDto searchUser(SearchUserDto dto);
}
