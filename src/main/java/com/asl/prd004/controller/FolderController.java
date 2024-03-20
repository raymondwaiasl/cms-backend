package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.SubscriptionBuDto;
import com.asl.prd004.entity.MisFolder;
import com.asl.prd004.service.IFolderService;
import com.asl.prd004.service.ISubscriptionService;
import com.asl.prd004.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/folder")
public class FolderController {

    @Resource
    private IFolderService folderService;

    @Resource
    private ISubscriptionService subscriptionService;


    @Log("Get folder list.")
    @RequestMapping(value="/getFolderList")
    public ResultGenerator getFolderList(){
        try {
            List<MisFolder> folderList = folderService.getFolderList();
            return ResultGenerator.getSuccessResult(folderList);
        } catch (Exception e) {
            return ResultGenerator.getFailResult("query fail");
        }
    }

    @Log("Get default folder.")
    @RequestMapping(value="/getDefaultFolder")
    public ResultGenerator getDefaultFolder(){
        return ResultGenerator.getSuccessResult(folderService.getDefaultFolder());
    }

    @Log("Save folder.")
    @RequestMapping(value="/saveFolder", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator saveFolder(@RequestBody String param) throws JSONException {
        JSONObject json = new JSONObject(param);
        String misFolderParentId = json.getString("misFolderParentId");
        String misFolderName = json.getString("misFolderName");
        boolean flag = folderService.saveFolder(misFolderParentId,misFolderName);
        //insert to subscription msg
        List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId( "0000000000000000",misFolderParentId);
        if(!subscriptionList.isEmpty()&&flag){
            for (SubscriptionBuDto subscription : subscriptionList) {
                if(subscription.getMisSubEventMsg().equals("New")){
                    subscriptionService.insertMsg(subscription.getMisSubscriptionId(),subscription.getMisSubscriptionEventId());
                }
            }
        }
        return ResultGenerator.getSuccessResult(flag);
    }

    @Log("Update folder.")
    @RequestMapping(value="/updateFolder", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateFolder(@RequestBody String param) throws JSONException {
        JSONObject json = new JSONObject(param);
        String id = json.getString("id");
        String name = json.getString("name");
        boolean flag = folderService.updateFolder(id,name);
        //insert to subscription msg
        List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId( "0000000000000000", id);
        if(!subscriptionList.isEmpty()&&flag){
            for (SubscriptionBuDto subscription : subscriptionList) {
                if(subscription.getMisSubEventMsg().equals("Modified")){
                    subscriptionService.insertMsg(subscription.getMisSubscriptionId(),subscription.getMisSubscriptionEventId());
                }
            }
        }
        return ResultGenerator.getSuccessResult(flag);
    }

    @Log("Delete folder.")
    @RequestMapping(value="/deleteFolder", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteFolder(@RequestBody String param) throws JSONException {
        JSONObject json = new JSONObject(param);
        String id = json.getString("id");
        MisFolder misFolder = folderService.getMisFolderById(id);
        boolean flag = folderService.deleteFolder(id);
        //insert to subscription msg
        if(misFolder.getMisFolderParentId()!=null && !misFolder.getMisFolderParentId().equals("0") && flag){
            List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId("0000000000000000", misFolder.getMisFolderParentId());
            if(!subscriptionList.isEmpty()&&flag){
                for (SubscriptionBuDto subscription : subscriptionList) {
                    if(subscription.getMisSubEventMsg().equals("Deleted")){
                        subscriptionService.insertMsg(subscription.getMisSubscriptionId(),subscription.getMisSubscriptionEventId());
                    }
                }
            }
        }
        return ResultGenerator.getSuccessResult(flag);
    }

}


