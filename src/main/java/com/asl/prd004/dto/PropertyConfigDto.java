package com.asl.prd004.dto;

import com.asl.prd004.entity.MisPropertyConfig;
import lombok.Data;

import java.util.List;

@Data
public class PropertyConfigDto {
    private MisPropertyConfig propertyConfig;
    private List<PropertyConfigDetailDto> propertyConfigDetails;
}
