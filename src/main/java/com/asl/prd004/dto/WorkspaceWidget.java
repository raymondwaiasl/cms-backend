package com.asl.prd004.dto;


import lombok.Data;


@Data
public class WorkspaceWidget {
    private String misWwId;
    private String misWorkspaceId;
    private String misWwAlias;
    private String misWwTitle;
    private String misBiConfigId;
    private String misWidgetId;
    private WorkspaceLayout layout;

    //private WidgetDto widgetDetail;
}
