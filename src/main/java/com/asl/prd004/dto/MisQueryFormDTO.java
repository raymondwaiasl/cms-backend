package com.asl.prd004.dto;

import com.asl.prd004.entity.MisQueryFormColumn;
import com.asl.prd004.entity.MisQueryFormCondition;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MisQueryFormDTO {
    private String misQfId;
    private String misQfName;
    private String misQfTableId;
    private String misQfPublic;
    private String misQfGroupId;
    private String misQfParentId;
    private List<MisQueryFormColumn> qfColumns;
    private List<MisQueryFormCondition> qfConditions;
    private List<MisQueryFormDTO> crossRef;
    public MisQueryFormDTO(String misQfId,String misQfName,String misQfTableId,String misQfPublic,String misQfGroupId){
        this.misQfId = misQfId;
        this.misQfName = misQfName;
        this.misQfTableId = misQfTableId;
        this.misQfPublic = misQfPublic;
        this.misQfGroupId = misQfGroupId;
    }
}
