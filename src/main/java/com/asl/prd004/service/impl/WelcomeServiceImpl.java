package com.asl.prd004.service.impl;

import com.asl.prd004.dao.WelcomeDao;
import com.asl.prd004.entity.MisWelcome;
import com.asl.prd004.service.IWelcomeService;
import com.asl.prd004.utils.SerialNumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/10/10 16:07
 */
@Service
public class WelcomeServiceImpl implements IWelcomeService {
    @Autowired
    WelcomeDao welcomeDao;
    @Transactional
    @Override
    public boolean addWelcome(String content) {
        try {
            List<MisWelcome> misWelcomeList=welcomeDao.findAll();
            if(misWelcomeList!=null&&misWelcomeList.size()>0){
                String welcomeId=misWelcomeList.get(0).getMisWelcomeId();
                welcomeDao.updateWelcome(welcomeId,content);
            }else {
                MisWelcome misWelcome = new MisWelcome();
                misWelcome.setMisWelcomeId(SerialNumberUtils.getTableSequence("mis_welcome"));
                misWelcome.setWelcomeContent(content);
                welcomeDao.save(misWelcome);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
            //return false;
        }
        return true;
    }

    @Override
    public String getWelcome() {
        List<MisWelcome> misWelcome=welcomeDao.findAll();
        if(misWelcome!=null&&misWelcome.size()>0) {
            String welContent=misWelcome.get(0).getWelcomeContent();
            ApplicationHome applicationHome = new ApplicationHome(this.getClass());
            String upladPath = applicationHome.getDir().getParentFile().getParentFile().getAbsolutePath() + "\\src\\main\\resources\\static\\";
            File directory = new File(upladPath);
            for (File file: Objects.requireNonNull(directory.listFiles())) {
                String filePath=file.getPath();
                boolean containsImage=welContent.contains(filePath.substring(filePath.lastIndexOf("\\")+1));
                if (!containsImage) {
                    file.delete();
                }
            }
        return  welContent;
        }
        return null;
    }
}


