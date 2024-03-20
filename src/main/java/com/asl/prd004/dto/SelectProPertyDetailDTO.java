package com.asl.prd004.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2024/1/8 14:33
 */
@Data
@NoArgsConstructor
public class SelectProPertyDetailDTO {

        private String name;
    private List<PropertyColumnConfigDetailDto> propertyConfigDetails;



}


