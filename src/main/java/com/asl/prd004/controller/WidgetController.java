package com.asl.prd004.controller;

import cn.hutool.core.util.StrUtil;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.WidgetDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisWidget;
import com.asl.prd004.service.IWidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/widget")
public class WidgetController {

    @Autowired
    private  IWidgetService iWidgetService;


    @PostMapping(value = "/getWidgetListByPage")
    public ResultGenerator<PageDataDto> getWidgetListPageable(@RequestBody PageableDto pageable) {
        return ResultGenerator.getSuccessResult(iWidgetService.getWidgetListPageable(pageable));
    }

    @PostMapping(value = "/getWidgetList")
    public ResultGenerator<List<WidgetDto>> getWidgetListPageable() {
        return ResultGenerator.getSuccessResult(iWidgetService.getWidgetList());
    }

    @PostMapping(value = "/getWidgetById")
    public ResultGenerator<WidgetInputDto> getWidgetById(@RequestBody TypeIdDto dto) {
        if(StrUtil.isNotBlank(dto.getId())){
            return ResultGenerator.getSuccessResult(iWidgetService.getWidgetById(dto));
        }else{
            return ResultGenerator.getSuccessResult(null);
        }
    }

    @PostMapping(value = "/addWidget")
    public ResultGenerator addWidget(@RequestBody WidgetInputDto widgetDto) {
        if (iWidgetService.addWidget(widgetDto)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @PostMapping(value = "/editWidget")
    public ResultGenerator editWidget(@RequestBody WidgetInputDto widgetDto) {
        if (iWidgetService.editWidget(widgetDto)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @PostMapping(value = "/deleteWidget")
    public ResultGenerator editWidget(@RequestBody TypeIdDto dto) {
        if (iWidgetService.deleteWidget(dto)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }
}
