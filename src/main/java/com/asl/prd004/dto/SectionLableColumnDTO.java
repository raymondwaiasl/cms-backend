package com.asl.prd004.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2024/1/2 14:34
 */
@Data
@NoArgsConstructor
public class SectionLableColumnDTO {
    private String misSectionLabel;
    private String misPropertySectionId;
    private String misPropertyConfigDetailColumnId;

    public SectionLableColumnDTO(String misSectionLabel,String misPropertySectionId, String misPropertyConfigDetailColumnId) {
        this.misSectionLabel = misSectionLabel;
        this.misPropertySectionId =misPropertySectionId;
        this.misPropertyConfigDetailColumnId = misPropertyConfigDetailColumnId;
    }
}


