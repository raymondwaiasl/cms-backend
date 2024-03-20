package com.asl.prd004.controller;


import cn.hutool.core.util.StrUtil;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.entity.MisDataDictionary;
import com.asl.prd004.service.IDictionaryService;
import com.asl.prd004.utils.Log;
import com.asl.prd004.utils.SerialNumberUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/DictManage")
public class DictionaryController {

    @Autowired
    IDictionaryService dictionaryService;

    @Log("Get all dictionary list.")
    @PostMapping(value="/getAllDictName")
    public ResultGenerator getAllDictName(@RequestBody PageableDto pageable) {
        return ResultGenerator.getSuccessResult(dictionaryService.getAllDictName(pageable));
    }

    @Log("Get dictionary by dictionary id.")
    @PostMapping(value="/getDicByDicId")
    public ResultGenerator getDicByDicId(@RequestBody String data) throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id=json.getString("id");
        MisDataDictionary misDataDictionary=dictionaryService.getDicByDicId(id);
        HashMap<String, String> resultData = new HashMap<>();
        resultData.put("dicId", misDataDictionary.getMisDdId());
        resultData.put("dicName", misDataDictionary.getMisDdName());
        return ResultGenerator.getSuccessResult(resultData);
    }


    /**
     * @param data
     * @return
     * @throws Exception
     */
    @Log("Query dictionary details.")
    @RequestMapping(value="/queryDictDetails",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator queryDictDetails(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id=json.getString("id");
        MisDataDictionary misDataDictionary=dictionaryService.getDicByDicId(id);
        HashMap<String, Object> resultData = new HashMap<>();
        resultData.put("dicId", misDataDictionary.getMisDdId());
        resultData.put("dicName", misDataDictionary.getMisDdName());
        resultData.put("propType", misDataDictionary.getMisDdPropType());
        resultData.put("propSql", misDataDictionary.getMisDdPropSql());


        JSONObject  pageState = json.getJSONObject("pageState");
        JSONObject  sort = json.getJSONObject("sortModel");
        PageDataDto dicDetails = null;
        if(Objects.equals(2,misDataDictionary.getMisDdPropType())
                && StrUtil.isNotBlank(misDataDictionary.getMisDdPropSql())
            ){
            dicDetails = dictionaryService.querySqlDictDetails(misDataDictionary.getMisDdPropSql(),pageState,sort);
        }
        else{
            dicDetails = dictionaryService.queryDictDetails(id,pageState,sort);
        }
        resultData.put("pageData", dicDetails);
        return ResultGenerator.getSuccessResult(resultData);
    }

    @Log("Create dictionary.")
    @RequestMapping(value="/createDic",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator createDictionary(@RequestBody String data)throws Exception {
        JSONObject json = new JSONObject(data);
        String dicName=json.getString("dicName");
        Integer propType=json.getInt("propType");
        MisDataDictionary dataDict = new MisDataDictionary();
        dataDict.setMisDdName(dicName);
        dataDict.setMisDdPropType(propType);
        TypeIdDto typeIdDto =  dictionaryService.createDictionary(dataDict);
        return ResultGenerator.getSuccessResult(typeIdDto);
    }

    @Log("Update dictionary.")
    @RequestMapping(value="/updateDic",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateDictionary(@RequestBody String data)throws Exception {
        JSONObject json = new JSONObject(data);
        String dicId=json.getString("dicId");
        String dicName=json.optString("dicName");
        String propSql=json.optString("propSql");
        MisDataDictionary dataDict = new MisDataDictionary();
        dataDict.setMisDdId(dicId);
        if(StrUtil.isNotBlank(dicName)){dataDict.setMisDdName(dicName);}
        if(StrUtil.isNotBlank(propSql)){dataDict.setMisDdPropSql(propSql);}
        if(dictionaryService.updateDictionary(dataDict)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Delete dictionary.")
    @RequestMapping(value="/delDictionary",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator delDictionary(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id");
        if(dictionaryService.delDictionary(id)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return  ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Add dictionary item.")
    @RequestMapping(value="/addDicItem",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addDictionaryItem(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String dicId=json.getString("dicId");
        String keyId=json.getString("keyId");
        String key=json.getString("key");
        String value=json.getString("value");
        if(dictionaryService.createDictionaryItem(dicId,keyId,key,value)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return ResultGenerator.getSuccessResult("failed");
        }
    }

    @Log("Update dictionary item.")
    @RequestMapping(value="/updateDicItem",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateDictionaryItem(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String dicId=json.getString("dicId");
        String keyId=json.getString("keyId");
        String key=json.getString("key");
        String value=json.getString("value");
        if(dictionaryService.updateDictionaryItem(dicId,keyId,key,value)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return ResultGenerator.getSuccessResult("failed");
        }
    }

    @Log("Delete dictionary item.")
    @RequestMapping(value="/delDictionaryItem",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator delDictionaryItem(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String keyId = json.getString("keyId");
        if(dictionaryService.delDictionaryItem(keyId)){
            return ResultGenerator.getSuccessResult("success");
        }else{
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("Get dictionary list by id.")
    @RequestMapping(value="/getDicListById",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getDicListById(@RequestBody String data)throws Exception {
        JSONObject json = new JSONObject(data);
        String id = json.getString("id");
        List<DicDto> dicList = dictionaryService.getDicListById(id);
        return ResultGenerator.getSuccessResult(dicList);
    }

    @Log("Verify dict prop sql.")
    @RequestMapping(value="/verifyPropSql",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator verifyPropSql(@RequestBody String data) throws Exception{
        return dictionaryService.verifyPropSql(new JSONObject(data).getString("sql"));
    }
}
