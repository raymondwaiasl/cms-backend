package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordListPageableDto {
    private String typeId;
    private String folderId;
    private String widgetId;
    private PageStateDto pageState;
    private List<SortModelDto> sortModel;
}
