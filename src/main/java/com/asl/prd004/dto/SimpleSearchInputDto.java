package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSearchInputDto {

    private String misSimpleSearchId;
    private String misSimpleSearchName;
    private String misSimpleSearchSql;
    private List<SimpleSearchItemDto> items;

}
