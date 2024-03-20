package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WidgetInputDto {
    public WidgetInputDto(WidgetDto w) {
        this.misWidgetId = w.getMisWidgetId();
        this.misWidgetName = w.getMisWidgetName();
        this.misBasicWidget = w.getMisBasicWidget();
        this.misWidgetConfig = w.getMisWidgetConfig();
        this.misWidgetType = w.getMisWidgetType();
        this.misDefaultTable = w.getMisDefaultTable();
        this.misSimpleSearchId = w.getMisSimpleSearchId();
        this.misDisplayHeader = w.getMisDisplayHeader();
        this.misHeaderTitle = w.getMisHeaderTitle();
    }

    private String misWidgetId;
    private String misWidgetName;
    private String misBasicWidget;
    private String misWidgetConfig;
    private String misWidgetType;
    private String misDefaultTable;
    private String misSimpleSearchId;
    private String misDisplayHeader;
    private String misHeaderTitle;
    private WidgetColListDto list;

}
