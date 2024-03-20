package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutolinkDTO {

    private String cmsAutolinkId;

    private String folderName;

    private String folderLevel;

    private String columnName;

    private String autoLinkCondition;

    private String autoLinkValue;

    private String autoLinkConditionRel;

}
