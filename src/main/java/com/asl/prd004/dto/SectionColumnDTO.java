package com.asl.prd004.dto;

import lombok.Data;

import java.util.List;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2024/1/2 14:29
 */
@Data
public class SectionColumnDTO {
    private String name;
    private List<ColumnInputDTO> columns;
    

}