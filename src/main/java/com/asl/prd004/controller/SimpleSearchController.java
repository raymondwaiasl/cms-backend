package com.asl.prd004.controller;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.MisColumnDao;
import com.asl.prd004.dao.MisCrossRefDao;
import com.asl.prd004.dao.MisPropertyConfigDetailDao;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisImportHist;
import com.asl.prd004.service.IRecordService;
import com.asl.prd004.service.ISimpleSearchService;
import com.asl.prd004.service.ISubscriptionService;
import com.asl.prd004.service.ITypeCrossRefService;
import com.asl.prd004.utils.Log;
import com.asl.prd004.utils.SerialNumberUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;


@RestController
@RequestMapping("/simpleSearch")
public class SimpleSearchController {

    @Resource
    private ISimpleSearchService simpleSearchService;


    @Log("getSimpleSearchListPageable")
    @RequestMapping(value="/getSimpleSearchListPageable", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getSimpleSearchListPageable(@RequestBody PageableDto data) {
        return ResultGenerator.getSuccessResult(simpleSearchService.getSimpleSearchListPageable(data));
    }


    @Log("Get Simple Search By Id.")
    @RequestMapping(value="/getSimpleSearchById", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getSimpleSearchById(@RequestBody TypeIdDto data) {
        return ResultGenerator.getSuccessResult(simpleSearchService.getSimpleSearchById(data));
    }

    @Log("addSimpleSearch.")
    @RequestMapping(value="/addSimpleSearch", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addSimpleSearch(@RequestBody SimpleSearchInputDto data) {
        System.out.println(data);
        return ResultGenerator.getSuccessResult(simpleSearchService.addSimpleSearch(data));
    }

    @Log("editSimpleSearch.")
    @RequestMapping(value="/editSimpleSearch", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator editSimpleSearch(@RequestBody SimpleSearchInputDto data) {
        System.out.println(data);
        return ResultGenerator.getSuccessResult(simpleSearchService.editSimpleSearch(data));
    }

    @Log("deleteSimpleSearch")
    @RequestMapping(value="/deleteSimpleSearch", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteSimpleSearch(@RequestBody TypeIdDto data) {
        return ResultGenerator.getSuccessResult(simpleSearchService.deleteSimpleSearch(data));
    }

    @Log("Simple Search.")
    @RequestMapping(value="/simpleSearchRecord", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator simpleSearchRecord(@RequestBody SimpleSearchRecordInputDto data) {
        System.out.println(data);
        return simpleSearchService.simpleSearchRecord(data);
    }

    @Log("getSimpleSearchDic")
    @RequestMapping(value="/getSimpleSearchDic", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getSimpleSearchDic() {
        return ResultGenerator.getSuccessResult(simpleSearchService.getSimpleSearchDic());
    }

}
