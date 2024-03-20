package com.asl.prd004.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class SaveSysConfigDTO {
    private String misSysConfigId;
    private String misSysConfigKey;
    private String misSysConfigValue;
    private MultipartFile misSysConfigImage;
    private String misSysConfigVisible;
    private String misSysConfigType;
}
