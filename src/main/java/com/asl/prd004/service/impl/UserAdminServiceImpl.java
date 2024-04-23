package com.asl.prd004.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisPasswordHistory;
import com.asl.prd004.entity.MisSysConfig;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.enums.ResultCodeEnum;
import com.asl.prd004.service.IUserAdminService;
import com.asl.prd004.utils.AESUtil;
import com.asl.prd004.vo.AccountSettingVO;
import com.github.wenhao.jpa.Specifications;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserAdminServiceImpl implements IUserAdminService {
    @Autowired
    private MisUserDao misUserDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private MisPermissionDetailDao misPermissionDetailDao;

    @Autowired
    SysConfigDao sysConfigDao;

    @Autowired
    PasswordHistoryDao passwordHistoryDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public String updateUserInfo(MisUser misUser) {
        MisUser misUserEntity = misUserDao.save(misUser);
        return misUserEntity.getMisUserId();
    }

    @Override
    @Transactional
    public void delUser(String misUserId) {
        misUserDao.delUser(misUserId);
    }

    @Override
    @Transactional
    public void deleteMemberByUserId(String misUserId) {
        memberDao.deleteMemberByUserId(misUserId);
    }


    @Override
    public PageDataDto queryUserAll(String misUserName, JSONObject... params) {
        PageDataDto userDto = null;
        try {
            JSONObject pageState = params[0];
            int pageNum = pageState.getInt("page")-1;
            int pageSize =  pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);

            if (params.length==2 && params[1].length()!=0) {
                JSONObject sortState = params[1];
                String sortField ="mis_user_id";
                if(sortState.getString("sort").equalsIgnoreCase("asc")){
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
                }else{
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
                }
            }
            misUserDao.searchUser(new TypeIdDto("123"),pageable);
            Page<MisUser> userPage=misUserDao.queryUserAll(misUserName,pageable);
            List<MisUser> userList=userPage.getContent();
            Session session = entityManager.unwrap(Session.class);
            //jpa默认在实体属性set之后自动提交到数据库，这里不需要提交到库，直接清缓存
            session.clear();
            if(userList!=null&&userList.size()>0) {
                for (int i = 0; i < userList.size(); i++) {
                    userList.get(i).setMisUserPassword(AESUtil.decryptAES(userList.get(i).getMisUserPassword()));
                }
            }
            userDto = new PageDataDto();
            userDto.setData(userList);
            userDto.setTotal(userPage.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDto;
    }

    @Override
    public ResultGenerator changeAccountSetting(ChangeAccountSettingDTO data) {
        String userId = ContextHolder.getUserId();
        misUserDao.changeAccountSetting(
            userId,
//            data.getLoginName(),
            data.getUserName(),
            data.getEmailAddress()
        );
        return ResultGenerator.getSuccessResult();
    }

    @Override
    public ResultGenerator changePassword(ChangePasswordDTO data) throws Exception {
        String userId = ContextHolder.getUserId();
        MisUser misUser = misUserDao.getUserInfoByUserId(userId).get(0);
        if(!StrUtil.equals(
            misUser.getMisUserPassword(),
            AESUtil.encryptAES(data.getCurrentPassword())
        )){
            return ResultGenerator.getResult(ResultCodeEnum.USER_ADMIN_CHANGE_PASSWORD_WRONG_PASSWORD);
//            return ResultGenerator.getFailResult("wrong password!");
        }
        if(StrUtil.equals(data.getCurrentPassword(),data.getNewPassword())){
            return ResultGenerator.getResult(ResultCodeEnum.USER_ADMIN_CHANGE_PASSWORD_OLD_PASSWORD);
        }
        int historyTimes = 1;
        MisSysConfig config =  sysConfigDao.getMisSysConfigByMisSysConfigKey("PasswordHistoryTimes");
        if(null != config){
            String timesStr = config.getMisSysConfigValue();
            if (timesStr.matches("\\d+")) {
                historyTimes = Integer.parseInt(timesStr);
            }
        }
        List<MisPasswordHistory> passwordHistories = passwordHistoryDao.getMisPasswordHistoriesByMisUserIdOrderByCreateTimeDesc(userId);
        if(null != passwordHistories){
            if(passwordHistories.size() > historyTimes){
                for (int i = 0; i < historyTimes; i++) {
                    if(StrUtil.equals(passwordHistories.get(i).getPassword(), AESUtil.encryptAES(data.getConfirmPassword()))){
                        return ResultGenerator.getResult(ResultCodeEnum.USER_ADMIN_CHANGE_PASSWORD_OLD_PASSWORD);
                    }
                }
            }else{
                for (int i = 0; i < passwordHistories.size(); i++) {
                    if(StrUtil.equals(passwordHistories.get(i).getPassword(), AESUtil.encryptAES(data.getConfirmPassword()))){
                        return ResultGenerator.getResult(ResultCodeEnum.USER_ADMIN_CHANGE_PASSWORD_OLD_PASSWORD);
                    }
                }
            }
        }

        MisPasswordHistory passwordHistory = new MisPasswordHistory();
        passwordHistory.setMisUserId(userId);
        passwordHistory.setPassword(AESUtil.encryptAES(data.getConfirmPassword()));
        passwordHistory.setCreateTime(new Timestamp(System.currentTimeMillis()));
        misUserDao.changePassword(userId,AESUtil.encryptAES(data.getConfirmPassword()));
        passwordHistoryDao.saveAndFlush(passwordHistory);
        return ResultGenerator.getSuccessResult();
    }

    @Override
    public AccountSettingVO getAccountSetting() {
        String userId = ContextHolder.getUserId();
        MisUser misUser = misUserDao.getUserInfoByUserId(userId).get(0);
        AccountSettingVO vo = new AccountSettingVO();
        vo.setLoginName(misUser.getMisUserLoginId());
        vo.setUserName(misUser.getMisUserName());
        vo.setEmailAddress(misUser.getMisEmail());
        vo.setGroupName("None");
        return vo;
    }

    @Override
    public PageDataDto searchUser(SearchUserDto dto){

        JSONArray jsonArray = JSON.parseArray(dto.getDistrict());
        String[] array = jsonArray.toArray(new String[0]);
        System.out.println("out================"+array);
        Specification<MisUser> specification =
                Specifications.<MisUser>and()
                        .like(StrUtil.isNotBlank(dto.getMisUserLoginId()), "misUserLoginId", "%" + dto.getMisUserLoginId() + "%")
                        .like(StrUtil.isNotBlank(dto.getMisUserName()), "misUserName", "%" + dto.getMisUserName() + "%")
                        .like(StrUtil.isNotBlank(dto.getSurnameEng()), "surnameEng", "%" + dto.getSurnameEng() + "%")
                        .like(StrUtil.isNotBlank(dto.getGivenNameEng()), "givenNameEng", "%" + dto.getGivenNameEng() + "%")
                        .like(StrUtil.isNotBlank(dto.getOtherNameEng()), "otherNameEng", "%" + dto.getOtherNameEng() + "%")
                        .like(StrUtil.isNotBlank(dto.getMisUserName()), "misUserName", "%" + dto.getMisUserName() + "%")
                        .like(StrUtil.isNotBlank(dto.getMisEmail()), "misEmail", "%" + dto.getMisEmail() + "%")
                        .like(StrUtil.isNotBlank(dto.getFax()), "fax", "%" + dto.getFax() + "%")
                        .like(StrUtil.isNotBlank(dto.getTel()), "tel", "%" + dto.getTel() + "%")
                        .like(StrUtil.isNotBlank(dto.getUserPost()), "userPost", "%" + dto.getUserPost() + "%")
                        .in(array.length > 0, "district", (Object[]) array)
                        .eq(StrUtil.isNotBlank(dto.getMisUserStatus()), "misUserStatus", dto.getMisUserStatus())
                        .eq(StrUtil.isNotBlank(dto.getMisUserType()), "misUserType", dto.getMisUserType())
                        .eq(StrUtil.isNotBlank(dto.getOffice()), "office", dto.getOffice())
                        .build();
        Pageable page;
        if (StringUtils.isNotEmpty(dto.getPageable().getSortModel().getField())) {
            String sortField = dto.getPageable().getSortModel().getField();
            switch (sortField){
                case "misUserId":
                    sortField = "misUserId";
                    break;
                case "misRoleName":
                    sortField = "mr.misRoleName";
                    break;
            }
            if (dto.getPageable().getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(dto.getPageable().getPageState().getPage() - 1, dto.getPageable().getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(dto.getPageable().getPageState().getPage() - 1, dto.getPageable().getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(dto.getPageable().getPageState().getPage() - 1, dto.getPageable().getPageState().getPageSize());
        }
        Page<MisUser> userPage = misUserDao.findAll(specification, page);
        List<MisUser> userList = userPage.getContent();
        Session session = entityManager.unwrap(Session.class);
        //jpa默认在实体属性set之后自动提交到数据库，这里不需要提交到库，直接清缓存
        session.clear();
        if(userList != null&& userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                try {
                    userList.get(i).setMisUserPassword(AESUtil.decryptAES(userList.get(i).getMisUserPassword()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        PageDataDto userDto = new PageDataDto(userList, userPage.getTotalElements());
        return userDto;
    }

}
