package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryFormInfoDto {

    private  String misQfId;
    private  String misQfName;
    private  String misQfTableId;
    private  String misQfPublic;
    private  String misQfGroupId;

}
