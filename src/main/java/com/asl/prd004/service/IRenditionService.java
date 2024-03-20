package com.asl.prd004.service;

import com.asl.prd004.dto.RenditionDto;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.entity.CmsRendition;
import com.asl.prd004.entity.CmsStorage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRenditionService {
    List<RenditionDto> getRenditionByRecordId(String recordId);

    boolean upLoadRendition(MultipartFile[] files,String misTypeId,String misRecordId);

    boolean deleteRenditionByRenditionId(String renditionId);

    boolean saveRendition(String recordId,String typeId,String image,String format);

    List<CmsRendition> getCmsRenditionsByMisRecordId(String recordId);

}
