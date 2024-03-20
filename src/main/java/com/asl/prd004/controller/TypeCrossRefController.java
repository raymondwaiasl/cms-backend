package com.asl.prd004.controller;


import cn.hutool.core.util.StrUtil;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.MisCrossRefDao;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.dto.TypeRefPageDto;
import com.asl.prd004.dto.TypeRefPropertyDto;
import com.asl.prd004.entity.MisCrossRef;
import com.asl.prd004.service.ITypeCrossRefService;
import com.asl.prd004.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeRef")
public class TypeCrossRefController {

    @Autowired
    ITypeCrossRefService typeCrossRefService;

    @Autowired
    MisCrossRefDao misCrossRefDao;
    @Log("Get all typeRef")
    @PostMapping(value="/getAllTypeRef")
    public ResultGenerator<PageDataDto> getAllTypeRef(@RequestBody TypeRefPageDto typeRefPageDto){
        return ResultGenerator.getSuccessResult(typeCrossRefService.getAllTypeRef(typeRefPageDto));
    }
    @Log("Add typeRef")
    @PostMapping(value="/addTypeRef")
    public ResultGenerator addTypeRef(@RequestBody MisCrossRef dto){
        return ResultGenerator.getSuccessResult(typeCrossRefService.addTypeRef(dto));
    }
    @Log("Update typeRef")
    @PostMapping(value="/updateTypeRef")
    public ResultGenerator editTypeRef(@RequestBody MisCrossRef dto){
        return ResultGenerator.getSuccessResult(typeCrossRefService.editTypeRef(dto));
    }
    @Log("Delete typeRef")
    @PostMapping(value="/deleteTypeRef")
    public ResultGenerator deleteTypeRef(@RequestBody TypeIdDto dto){
        return ResultGenerator.getSuccessResult(typeCrossRefService.deleteTypeRef(dto));
    }
    @Log("Query typeById")
    @PostMapping(value="/getById")
    public ResultGenerator getById(@RequestBody TypeIdDto dto){
        List<String> ids = StrUtil.split(dto.getId(), ',');
        List<TypeRefPropertyDto> typeRefByTypeId = misCrossRefDao.findAllTypeRef(ids);

        Map<String,DicDto> id2dto = new HashMap<>();
        typeRefByTypeId.forEach(t -> {
            id2dto.put(
                t.getMisCrossRefParentTableID(),
                new DicDto(t.getMisCrossRefParentTableID(),t.getMisCrossRefParentTableLabel())
            );
            id2dto.put(
                t.getMisCrossRefChildTableID(),
                new DicDto(t.getMisCrossRefChildTableID(),t.getMisCrossRefChildTableLabel())
            );
        });
        ids.forEach(id -> id2dto.remove(id));//排除进来的tableid
        return
            ResultGenerator.getSuccessResult(
                id2dto.values().stream().sorted(Comparator.comparing(DicDto::getKey))
            );
    }

}
