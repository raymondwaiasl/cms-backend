package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.service.IMySearchService;
import com.asl.prd004.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mySearch")
public class MySearchController {

    @Autowired
    IMySearchService searchService;


    @Log("Get my search list.")
    @PostMapping(value="/getMySearchListPageable")
    public ResultGenerator getMySearchListPageable(@RequestBody PageableDto pageable){

        return ResultGenerator.getSuccessResult(searchService.getMySearchListPageable(pageable));
    }
}
