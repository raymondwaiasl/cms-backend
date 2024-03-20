package com.asl.prd004;

import com.asl.prd004.entity.MisSysConfig;
import com.asl.prd004.service.ISysConfigService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SysConfigLoader implements InitializingBean, ServletContextAware {

    public static Map<String,String> initDate = new ConcurrentHashMap<>();

    public static Map<String,String> folderId = new ConcurrentHashMap<>();

    @Autowired
    private ISysConfigService sysConfigService;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        System.out.println("start loading sysConfigList.........");
        List<MisSysConfig> sysConfigList =  sysConfigService.findByVisible("0");
        if (null != sysConfigList && !sysConfigList.isEmpty()) {
            for (MisSysConfig sysConfig : sysConfigList){
                initDate.put(sysConfig.getMisSysConfigKey(), sysConfig.getMisSysConfigValue());
            }
        } else {
            System.out.println("sysConfigList no data");
        }
    }
}
