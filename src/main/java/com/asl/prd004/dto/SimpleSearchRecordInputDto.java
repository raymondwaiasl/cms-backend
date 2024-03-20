package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSearchRecordInputDto {

    private String folderId;
    private String simpleSearchId;
    private String data;
    private PageStateDto pageState;
    private List<SortModelDto> sortModel;

}
