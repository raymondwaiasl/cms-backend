package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/6/5 16:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiToolConfigDTO {
    private String misBiConfigId;
    private String misBiConfigName;
    private String misBiConfigTypeId;
    private String misBiConfigTypeName;
    private String misBiConfigGraphicType;
    private String misBiConfigColumnHor;
    private String misBiConfigColumnVet;
    private String misBiConfigDate;
    private String misBiConfigDefView;
}


