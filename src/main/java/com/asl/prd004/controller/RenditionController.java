package com.asl.prd004.controller;


import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.service.IRenditionService;
import com.asl.prd004.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rendidtion")
public class RenditionController {



    @Autowired
    IRenditionService renditionService;

    @Log("Get rendition by recordId")
    @PostMapping(value="/getRenditionByRecordId")
    public ResultGenerator  getRenditionByRecordId(@RequestBody TypeIdDto idDto){
        return ResultGenerator.getSuccessResult(renditionService.getRenditionByRecordId(idDto.getId()));
    }

    @Log("UpLoad rendition")
    @PostMapping(value = "/upLoadRendition", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultGenerator  upLoadRendition(@RequestParam("files") MultipartFile[] files,
                                   @RequestParam("misTypeId") String misTypeId,
                                   @RequestParam("misRecordId") String misRecordId){
        if (renditionService.upLoadRendition(files, misTypeId, misRecordId)) {
            return ResultGenerator.getSuccessResult("rendition success");
        }else {
            return ResultGenerator.getFailResult("参数为空!");
        }

    }
    @Log("Delete rendition by renditionId")
    @PostMapping(value="/deleteRenditionByRenditionId")
    public ResultGenerator  deleteRenditionByRenditionId(@RequestBody TypeIdDto idDto){
        return ResultGenerator.getSuccessResult(renditionService.deleteRenditionByRenditionId(idDto.getId()));
    }


}
