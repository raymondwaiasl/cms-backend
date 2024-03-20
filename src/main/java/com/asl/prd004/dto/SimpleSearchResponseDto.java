package com.asl.prd004.dto;

import com.asl.prd004.entity.MisSimpleSearch;
import com.asl.prd004.entity.MisSimpleSearchItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSearchResponseDto {

    public SimpleSearchResponseDto(String misSimpleSearchId, String misSimpleSearchName, String misSimpleSearchSql) {
        this.misSimpleSearchId = misSimpleSearchId;
        this.misSimpleSearchName = misSimpleSearchName;
        this.misSimpleSearchSql = misSimpleSearchSql;
    }
    public SimpleSearchResponseDto(MisSimpleSearch simpleSearch, List<SimpleSearchItemDto> simpleSearchItems) {
        this.misSimpleSearchId = simpleSearch.getMisSimpleSearchId();
        this.misSimpleSearchName = simpleSearch.getMisSimpleSearchName();
        this.misSimpleSearchSql = simpleSearch.getMisSimpleSearchSql();
        this.items = simpleSearchItems;
    }

    private String misSimpleSearchId;
    private String misSimpleSearchName;
    private String misSimpleSearchSql;
    private List<SimpleSearchItemDto> items;

}
