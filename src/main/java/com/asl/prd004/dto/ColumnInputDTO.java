package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnInputDTO {

    private String misColumnLabel;

    private String misColumnName;

    private String misColumnType;

    private String misColumnInputType;

    private String misColumnDictionary;

    private List<DicDto> dictList;

    private Integer row_size;

    private Integer col_size;

    private String misColumnId;

    private Float misColumnWidth;

    private String misColumnLength;

    private Object value;

    private Boolean disabled;

    private List<EffectColumn> effect;

    private String misPropertySectionId;
}
