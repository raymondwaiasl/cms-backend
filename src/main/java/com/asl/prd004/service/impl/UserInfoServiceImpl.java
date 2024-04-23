package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.DefinitionException;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.OrgChartDTO;
import com.asl.prd004.dto.ResetPasswordDto;
import com.asl.prd004.dto.UserDto;
import com.asl.prd004.entity.MisGroup;
import com.asl.prd004.entity.MisSysConfig;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.service.IUserInfoService;
import com.asl.prd004.utils.AESUtil;
import com.asl.prd004.utils.ActiveDirectory;
import com.asl.prd004.utils.JwtUtil;
import com.asl.prd004.utils.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.ldap.LdapContext;
import java.sql.Timestamp;
import java.util.*;

@Transactional
@Service
public class UserInfoServiceImpl implements IUserInfoService {

    @Autowired
    MisUserDao userDao;

    @Autowired
    GroupDao groupDao;

    @Autowired
    OrgChartDao orgChartDao;

    @Autowired
    SysConfigDao sysConfigDao;

    @Autowired
    RoleDao roleDao;

    @Override
    public ResultGenerator verifyLogin(UserDto dto) {
        List<MisUser> users = userDao.findByMisUserLoginId(dto.getLoginName());
        if (users.isEmpty()) {
            return new ResultGenerator(401, "Login Failed! Account doesn't exit.");
        } else {
            MisUser user = users.get(0);
            Date date = new Date();
            Date nextDate = new Date(date.getTime() + JwtUtil.EXPIRE_SECONDS);
            int loginAttempts = user.getLoginAttempts();
            if (user.isLocked()) {
                System.out.println("Account is locked. Please contact the administrator.");
                return new ResultGenerator(401, "Account is locked. Please contact the administrator.");
            }
            boolean isOk;
            try {
                isOk = users.get(0).getMisUserPassword().equals(AESUtil.encryptAES(dto.getPwd()));
            } catch (Exception e) {
                return new ResultGenerator(401, e.getMessage());
            }
            if (isOk) {
                user.setLoginAttempts(0);
                userDao.saveAndFlush(user);

                String userRole = "";
                List<String> userRoleIdList = userDao.getUserRoles(user.getMisUserId());
                if (!userRoleIdList.isEmpty()) {

                    List<String> roleNames = roleDao.getRoleNameByRoleIds(userRoleIdList);

                    userRole = String.join(",", roleNames);
                }

                HashMap<String, Object> map = new HashMap<>(2);
                map.put("userId", user.getMisUserId());
                map.put("userLoginId", user.getMisUserLoginId());
                map.put("office", StringUtils.defaultIfEmpty(user.getOffice(), ""));
                map.put("userRole", StringUtils.defaultIfEmpty(userRole, ""));
                String token = JwtUtil.generateToken(map);
                HashMap<String, Object> resultMap = new HashMap<>(2);
                resultMap.put("token", token);
                resultMap.put("expire_time", nextDate);
                resultMap.put("user_name", user.getMisUserName());
                resultMap.put("is_change", user.isChange());
                resultMap.put("is_admin", user.getIsAdmin());
                return ResultGenerator.getSuccessResult(resultMap);
            } else {
               /*Comment for skip LDAP login
                try{
                    LdapContext ctx = ActiveDirectory.getConnection(dto.getLoginName(), dto.getPwd(),"d2.test","192.168.50.144:389/CN=Users,DC=d2,DC=test");
                    ctx.close();
                    HashMap<String, Object> map = new HashMap<>();
                    //question login name == user id ?
                    map.put("userId",dto.getLoginName());
                    String token = JwtUtil.generateToken(map);
                    HashMap<String, Object> resultMap = new HashMap<>(2);
                    resultMap.put("token",token);
                    resultMap.put("expire_time",nextDate);
                    resultMap.put("user_name",user.getMisUserName());
                    resultMap.put("is_change",user.isChange());
                    resultMap.put("is_admin",user.getIsAdmin());
                    return ResultGenerator.getSuccessResult(resultMap);
                }catch(Exception e){
                    //Failed to authenticate user!
                    e.printStackTrace();
                    loginAttempts++;
                    int attemptTimes = 5;
                    MisSysConfig config =  sysConfigDao.getMisSysConfigByMisSysConfigKey("attemptTimes");
                    if(null != config){
                        String timesStr = config.getMisSysConfigValue();
                        if (timesStr.matches("\\d+")) {
                            attemptTimes = Integer.parseInt(timesStr);
                        }
                    }

                    if (loginAttempts >= attemptTimes) {
                        user.setLocked(true);
                        userDao.saveAndFlush(user);
                        System.out.println("Account locked. Please contact the administrator.");
                        return new ResultGenerator(401,"Account locked. Please contact the administrator.");
                    } else {
                        user.setLoginAttempts(loginAttempts);
                        userDao.saveAndFlush(user);
                        System.out.println("Invalid password. Attempt " + loginAttempts + " of 5.");
                        return new ResultGenerator(401,"Invalid password. Attempt " + loginAttempts + " of "+ attemptTimes +".");
                    }
                }
                */
                loginAttempts++;
                int attemptTimes = 5;
                MisSysConfig config = sysConfigDao.getMisSysConfigByMisSysConfigKey("attemptTimes");
                if (null != config) {
                    String timesStr = config.getMisSysConfigValue();
                    if (timesStr.matches("\\d+")) {
                        attemptTimes = Integer.parseInt(timesStr);
                    }
                }

                if (loginAttempts >= attemptTimes) {
                    user.setLocked(true);
                    userDao.saveAndFlush(user);
                    System.out.println("Account locked. Please contact the administrator.");
                    return new ResultGenerator(401, "Account locked. Please contact the administrator.");
                } else {
                    user.setLoginAttempts(loginAttempts);
                    userDao.saveAndFlush(user);
                    System.out.println("Invalid password. Attempt " + loginAttempts + " of 5.");
                    return new ResultGenerator(401, "Invalid password. Attempt " + loginAttempts + " of " + attemptTimes + ".");
                }
            }

        }
    }

    @Override
    public MisUser getUserInfo(String userLoginId) {
        List<MisUser> users = userDao.findByMisUserLoginId(userLoginId);
        if (users.size() > 0) {
            return users.get(0);
        } else {
            throw new RuntimeException("Cannot find the user.");
        }
    }

    @Override
    public ResultGenerator forgotPwd(String email) {
        //验证邮箱
        List<MisUser> users = userDao.getMisUsersByMisEmailOrMisUserLoginId(email, email);
        //生成token
        if (users != null && users.size() > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", users.get(0).getMisUserId());
            map.put("email", users.get(0).getMisEmail());
            map.put("loginId", users.get(0).getMisUserLoginId());
            String token = JwtUtil.generateToken(map);
            String link = PropertiesUtil.getProperty("authentication.reset.link");
            String resetLink = link + token;
            //发送邮件
            String emailContent = "Dear user,\n\nPlease click <a href=\"" + resetLink + "\">here</a> to reset your password.";
            try {
                sendMail(users.get(0).getMisEmail(), "Password Reset", emailContent);
                System.out.println("Email sent successfully!");
                return ResultGenerator.getSuccessResult();
            } catch (Exception e) {
                e.printStackTrace();
                return new ResultGenerator(401, "Email sent failed!");
            }

        } else {
            return ResultGenerator.getFailResult("The email address is incorrect.");
        }
    }

    @Override
    public ResultGenerator resetPwd(ResetPasswordDto dto) {
        // 解析令牌
        Map<String, Object> map = JwtUtil.resolveToken(dto.getToken());
        String userId = map.get("userId").toString();
        MisUser user = userDao.getMisUserByMisUserId(userId);
        if (user != null) {
            try {
                user.setMisUserPassword(AESUtil.encryptAES(dto.getPassword()));
                user.setLastPasswordChange(new Timestamp(System.currentTimeMillis()));
                userDao.saveAndFlush(user);
                return ResultGenerator.getSuccessResult("Password reset successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                throw new DefinitionException(401, "Password reset failed!");
            }

        }
        return ResultGenerator.getFailResult("Password reset failed!");
    }

    @Override
    public String getGroupDefaultFolderByGroupId() {
        MisUser user = userDao.getMisUserByMisUserId(ContextHolder.getUserId());
        if (!StringUtils.isEmpty(user.getCurrentGroup())) {
            MisGroup userGroup = groupDao.getMisGroupByMisGroupId(user.getCurrentGroup());
            return userGroup.getMisGroupDefaultFolder();
        } else {
            List<String> groupIds = orgChartDao.getGroupIdByUserId(ContextHolder.getUserId());
            //System.out.println("groupIds========================" + groupIds.get(0));
            if (groupIds != null && groupIds.size() > 0) {
                MisGroup userGroup = groupDao.getMisGroupByMisGroupId(groupIds.get(0));
                return userGroup.getMisGroupDefaultFolder();
            }
        }
        return "";
    }

    @Override
    public ResultGenerator getPasswordPolicy() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = addPasswordPolicyConfig("hasDigit", resultMap);
        resultMap = addPasswordPolicyConfig("hasLetters", resultMap);
        resultMap = addPasswordPolicyConfig("hasUpperLowerCase", resultMap);
        resultMap = addPasswordPolicyConfig("hasSpecialChar", resultMap);
        resultMap = addPasswordPolicyConfig("passwordLength", resultMap);
        return ResultGenerator.getSuccessResult(resultMap);
    }

    private Map<String, Object> addPasswordPolicyConfig(String key, Map<String, Object> map) {
        MisSysConfig config = sysConfigDao.getMisSysConfigByMisSysConfigKey(key);
        if (null != config) {
            map.put(key, config.getMisSysConfigValue());
        }
        return map;
    }

    private void sendMail(String receive, String subject, String content) throws Exception {
        String MyEmail = "807661792@163.com";// 开启授权码的邮箱
        String AuthorizationCode = "NBNCTMPSLIXTMOOW";// 授权码
        String SMTPEmail = "smtp.163.com";// 网易163邮箱的 SMTP 服务器地址
        //创建连接邮件服务器的参数配置
        Properties props = new Properties();// 参数配置
        props.setProperty("mail.smtp.host", SMTPEmail);// 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");// 需要请求认证
        props.setProperty("mail.transport.protocol", "smtp");
        //根据配置创建会话对象和邮件服务器交互
        Session session = Session.getInstance(props);
        //session.setDebug(true);
        //创建邮件信息
        MimeMessage message = createEmail(session, MyEmail, receive, subject, content);
        //使用Session获取邮件传输对象
        Transport transport = session.getTransport();
        //使用邮箱账号和密码连接邮件服务器
        transport.connect(MyEmail, AuthorizationCode);
        //发送邮件
        transport.sendMessage(message, message.getAllRecipients());
        //关闭连接
        transport.close();

    }

    public static MimeMessage createEmail(Session session, String sendMail, String receiveMail, String subject, String content) throws Exception {
        //创建一封邮件
        MimeMessage message = new MimeMessage(session);
        //发件人
        message.setFrom(new InternetAddress(sendMail, "asl send user", "UTF-8"));

        Address[] internetAddressTo = new InternetAddress().parse(receiveMail);
        //收件人
        message.setRecipients(MimeMessage.RecipientType.TO, internetAddressTo);
        //邮件主题
        message.setSubject(subject, "UTF-8");
        //邮件正文
        message.setContent(content, "text/html;charset=UTF-8");
        //设置发件时间
        message.setSentDate(new Date());
        //保存设置
        message.saveChanges();
        return message;
    }


}
