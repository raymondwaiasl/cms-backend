package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2024/1/8 15:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionNameColumnDetailDTO {
    private String name;
    private List<ColumnNestDTO> columns;
}


