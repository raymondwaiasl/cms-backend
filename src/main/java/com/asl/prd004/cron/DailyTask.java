package com.asl.prd004.cron;

import com.asl.prd004.dao.MisUserDao;
import com.asl.prd004.dao.SysConfigDao;
import com.asl.prd004.entity.MisSysConfig;
import com.asl.prd004.entity.MisUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Component
public class DailyTask {

    @Autowired
    MisUserDao userDao;

    @Autowired
    SysConfigDao sysConfigDao;

    @Scheduled(cron = "0 0 0 * * *") // 每天凌晨执行
    public void executeDailyTask() {
        // 每天定时检查哪些账户需要修改密码
        List<MisUser> users = userDao.getUsersNeedChangePwd();
        int passwordAgeDays = 30;
        MisSysConfig config =  sysConfigDao.getMisSysConfigByMisSysConfigKey("PasswordAgeDays");
        if(null != config){
            String daysStr = config.getMisSysConfigValue();
            if (daysStr.matches("\\d+")) {
                passwordAgeDays = Integer.parseInt(daysStr);
            }
        }
        for(MisUser u:users){
            int days = getDaysBetweenDates(u.getLastPasswordChange(), new Timestamp(System.currentTimeMillis()));
            if(days > passwordAgeDays){
                u.setChange(true);
                userDao.saveAndFlush(u);
            }
        }
        System.out.println("执行每天定时任务");
    }

    private int getDaysBetweenDates(Timestamp startDate, Timestamp endDate) {
        //以毫秒为单位获取两个日期之间的差值。
        long differenceInMilliseconds = endDate.getTime() - startDate.getTime();

        //将差值以毫秒为单位转换为天。
        int daysBetweenDates = (int) (differenceInMilliseconds / (1000 * 60 * 60 * 24));

        //返回两个日期之间的天数。
        return daysBetweenDates;
    }
}
