package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeRefPropertyDto {
    private String misCrossRefId;
    private String misCrossRefParentTableID;
    private String misCrossRefParentTableLabel;
    private String misCrossRefChildTableID;
    private String misCrossRefChildTableLabel;

}
