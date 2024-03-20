package com.asl.prd004.dto;

import com.asl.prd004.entity.MisPropertyConfigDetail;
import lombok.Data;

import java.util.List;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/12/21 15:38
 */
@Data
public class propertyConfigDetailsDto {
    private String name;
    private List<MisPropertyConfigDetail> columns;
}


