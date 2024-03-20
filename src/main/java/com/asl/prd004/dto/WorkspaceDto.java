package com.asl.prd004.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
public class WorkspaceDto {

    private String misWorkspaceId;
    private String misWorkspaceName;
    private int misSortNum;
    private  String misWorkspaceParentId;
    private List<WorkspaceWidget> widgets;
    private Object widgetDetail;

}
