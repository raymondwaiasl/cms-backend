package com.asl.prd004.controller;


import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.SubscriptionMsgListDto;
import com.asl.prd004.service.ISubscriptionService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {


    @Resource
    private ISubscriptionService subscriptionService;


    @Log("Save subscription.")
    @RequestMapping(value="/saveSubscription", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator saveSubscription(@RequestBody String data) throws Exception {
        //解析接受到的json数据
        JSONObject json =new JSONObject(data);
        String id = json.getString("id");
        String typeId = json.getString("typeId");
        if(typeId == ""){
            typeId = "0000000000000000";
        }
        Boolean checkedN = Boolean.valueOf(json.getString("checkedN"));
        Boolean checkedM = Boolean.valueOf(json.getString("checkedM"));
        Boolean checkedD = Boolean.valueOf(json.getString("checkedD"));
        Boolean checkedB = Boolean.valueOf(json.getString("checkedB"));
        String repeat = json.getString("repeat");
        String buDate = json.getString("buDate");
        boolean flag = subscriptionService.saveSubscription(id,typeId,checkedN,checkedM,checkedD,checkedB,repeat,buDate);
        return ResultGenerator.getSuccessResult(flag);
    }

    @Log("Cancel subscription.")
    @RequestMapping(value="/unSubscription", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator unSubscription(@RequestBody String data) throws Exception {
        //解析接受到的json数据
        JSONObject json =new JSONObject(data);
        String objId = json.getString("objId");
        boolean flag = subscriptionService.unSubscription(objId);
        return ResultGenerator.getSuccessResult(flag);
    }

    @Log("Judge folder if is subscripted")
    @RequestMapping(value="/folderIsSubscribe", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator folderIsSubscribe(@RequestBody String data) throws Exception {
        JSONObject json = new JSONObject(data);
        String folderId = json.getString("folderId");
        boolean flag = subscriptionService.folderIsSubscribe(folderId);
        return ResultGenerator.getSuccessResult(flag);
    }


    @Log("Get subscription message by user id.")
    @RequestMapping(value="/getSubscriptionMsgByUserId", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getSubscriptionMsgListByUserId(@RequestBody PageableDto pageable) throws Exception{
        return ResultGenerator.getSuccessResult( subscriptionService.getSubscriptionMsgListByUserId(pageable));
    }

    @Log("Delete subscription message by id.")
    @RequestMapping(value="/deleteSubscriptionMsgById", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteSubscriptionMsgById(@RequestBody String param) throws JSONException {
        JSONObject json =new JSONObject(param);
        String msgId = json.getString("msgId");
        return ResultGenerator.getSuccessResult(subscriptionService.deleteMsg(msgId));
    }

    @Log("Update subscription message to read status.")
    @RequestMapping(value="/updateSubscriptionMsgRead", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateSubscriptionMsgRead(@RequestBody String param) throws JSONException {
        JSONObject json =new JSONObject(param);
        String msgId = json.getString("msgId");
        return ResultGenerator.getSuccessResult(subscriptionService.updateMsgStatus(msgId));
    }

}
