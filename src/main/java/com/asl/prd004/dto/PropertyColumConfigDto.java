package com.asl.prd004.dto;

import com.asl.prd004.entity.MisPropertyConfig;
import com.asl.prd004.entity.MisPropertyConfigDetail;
import lombok.Data;

import java.util.List;
@Data
public class PropertyColumConfigDto {

    private MisPropertyConfig propertyConfig;
    private List<PropertyColumnConfigDetailDto> propertyConfigDetails;
}
