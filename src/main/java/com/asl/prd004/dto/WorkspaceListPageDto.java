package com.asl.prd004.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class WorkspaceListPageDto {

    public WorkspaceListPageDto(String misWorkspaceId,String misWorkspaceName,Integer misSortNum){
        this.misWorkspaceId = misWorkspaceId;
        this.misWorkspaceName = misWorkspaceName;
        this.misSortNum = misSortNum;
    }

    public WorkspaceListPageDto(String misWorkspaceId,String misWorkspaceName,String misParentWorkspaceName,Integer misSortNum){
        this.misWorkspaceId = misWorkspaceId;
        this.misWorkspaceName = misWorkspaceName;
        this.misParentWorkspaceName = misParentWorkspaceName;
        this.misSortNum = misSortNum;
    }
    private String misWorkspaceId;
    private String misWorkspaceName;
    private String misParentWorkspaceName;
    private Integer misSortNum;

}
