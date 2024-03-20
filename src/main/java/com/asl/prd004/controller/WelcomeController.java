package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.dto.WidgetDto;
import com.asl.prd004.entity.MisWelcome;
import com.asl.prd004.service.IWelcomeService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author billy
 * @version 1.0
 * @description: welcome page controller
 * @date 2023/10/10 16:02
 */
@RestController
@RequestMapping("/welcome")
public class WelcomeController {
    @Autowired
    IWelcomeService iWelcomeService;
    @PostMapping(value = "/getWelcome")
    public ResultGenerator<String> getWelcome() {
        return ResultGenerator.getSuccessResult(iWelcomeService.getWelcome());
    }
    @PostMapping(value = "/addWelcomeData")
    public ResultGenerator addWelcome(@RequestBody String data) {
        try {
            JSONObject json = new JSONObject(data);
            String content=json.getString("content");
            if (iWelcomeService.addWelcome(content)) {
                return ResultGenerator.getSuccessResult("success");
            } else {
                return ResultGenerator.getFailResult("failed");
            }
        } catch (JSONException e) {
            return ResultGenerator.getFailResult("failed");
        }
    }
    @RequestMapping(value="/uploadEditorImage")
    public ResultGenerator uploadEditorImage(MultipartFile file, HttpServletRequest request){
        ApplicationHome applicationHome = new ApplicationHome(this.getClass());
        String upladPath=applicationHome.getDir().getParentFile().getParentFile().getAbsolutePath() + "\\src\\main\\resources\\static\\";
        //String returnPath=request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        if (file.isEmpty()) {
            return ResultGenerator.getFailResult("文件为空!");
        }
        // 给文件重命名,加上时间缀,文件越来越多时,根据时间缀定时清理文件
        String fileName = UUID.randomUUID()+"-"+ System.currentTimeMillis()+ "." + file.getContentType()
                .substring(file.getContentType().lastIndexOf("/") + 1);
        String returnPath=request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +"/"+fileName;
        try {
            // 获取保存路径
            String path = upladPath;
            File files = new File(path, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdir();
            }
            file.transferTo(files);

        } catch (IOException e) {
            return ResultGenerator.getFailResult("程序异常!");
        }
        return ResultGenerator.getSuccessResult(returnPath);
    }
}


