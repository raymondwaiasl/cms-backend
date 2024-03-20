package com.asl.prd004.dto;



import com.asl.prd004.entity.CmsFormat;
import com.asl.prd004.entity.MisUser;
import lombok.Data;

import java.sql.Timestamp;


@Data
public class RenditionDto {

    private String cmsRenditionId;
    private String misTypeId;
    private String misRecordId;
    private String cmsIsPrimary;
    private String cmsFormatId;
    private String cmsRenditionFile;
    private Timestamp cmsRenditionDate;
    private String cmsCreatorUserId;
    private String cmsFileLocation;
    private MisUser user;
    private CmsFormat format;
}
