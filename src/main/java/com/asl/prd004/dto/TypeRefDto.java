package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeRefDto {

    private String misCrossRefId;
    private String misCrossRefName;

    private String misCrossRefParentTableID;
    private String misCrossRefParentTableLabel;
    private String misCrossRefParentTableName;
    private String misCrossRefParentKey;
    private String misCrossRefParentKeyLabel;
    private String misCrossRefParentKeyName;


    private String misCrossRefChildTableID;
    private String misCrossRefChildTableLabel;
    private String misCrossRefChildTableName;
    private String misCrossRefChildKey;
    private String misCrossRefChildKeyLabel;
    private String misCrossRefChildKeyName;

}
