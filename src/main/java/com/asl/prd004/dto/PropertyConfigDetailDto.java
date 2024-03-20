package com.asl.prd004.dto;

import com.asl.prd004.entity.MisPropertyConfigDetail;
import lombok.Data;

import java.util.List;

@Data
public class PropertyConfigDetailDto {
    private String name;
    private String misPropertyTableId;
    private List<MisPropertyConfigDetail> columns;
}
