package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.WorkspaceListPageDto;
import com.asl.prd004.entity.MisContext;
import com.asl.prd004.entity.MisContextDetail;

import java.util.List;

public interface IContextService {

    PageDataDto findContextList(PageableDto pageable);

    Boolean deleteContext(String contextId);

    Boolean addContext(String contextName,String roleId,String wsId);

    Boolean updateContext(String contextId,String contextName,String roleId,String wsId);

    MisContext findById(String id);

    List<MisContextDetail> findDetailByContextId(String contextId);

    List<WorkspaceListPageDto> findWorkspaceList();

    WorkspaceListPageDto findWorkspaceById(String id);
}
