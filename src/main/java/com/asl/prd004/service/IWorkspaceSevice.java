package com.asl.prd004.service;

import com.asl.prd004.dto.MenuItemDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.WorkspaceDto;

import java.util.List;

public interface IWorkspaceSevice {
    public PageDataDto getWorkspaceListPageable(PageableDto pageable);
    WorkspaceDto selectWorkspaceWidgetById(String id);
    public boolean addWorkspace(WorkspaceDto dto);
    boolean editWorkspace(WorkspaceDto dto);
    public List<MenuItemDto> getContextByUserId(String userId);
    void deleteWorkspaceById(String id);
    int validateWorkspaceById(String id);
    List<MenuItemDto> findParentCandidateById(String id);
    void setParent(MenuItemDto dto);
}
