package com.asl.prd004.dao;

import com.asl.prd004.dto.MenuItemDto;
import com.asl.prd004.dto.WorkspaceListPageDto;
import com.asl.prd004.entity.MisWorkspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface WorkspaceDao extends JpaRepository<MisWorkspace, String> {

    @Query(value = "select new com.asl.prd004.dto.WorkspaceListPageDto(mw.misWorkspaceId,mw.misWorkspaceName,pmw.misWorkspaceName,mw.misSortNum) FROM MisWorkspace mw " +
            " left join MisWorkspace pmw on mw.misWorkspaceParentId = pmw.misWorkspaceId")
    Page<WorkspaceListPageDto> findAllWorkspacePageable(Pageable pageable);

    @Query(value = "select new com.asl.prd004.dto.WorkspaceListPageDto(mw.misWorkspaceId,mw.misWorkspaceName,mw.misSortNum) FROM MisWorkspace mw ")
    List<WorkspaceListPageDto> findAllWorkspaceList();

    @Query(value = "select new com.asl.prd004.dto.WorkspaceListPageDto(mw.misWorkspaceId,mw.misWorkspaceName,mw.misSortNum) FROM MisWorkspace mw where mw.misWorkspaceId = ?1")
    List<WorkspaceListPageDto> findWorkspaceListById(String workspaceId);

//    @Query(value = "select new com.asl.prd004.dto.MenuItemDto(mw.misWorkspaceId,mw.misWorkspaceName,mw.misSortNum)  FROM MisContext mc \n" +
//            "    left join MisContextDetail mcd on mcd.misContextId = mc.misContextId \n" +
//            "    left join MisWorkspace mw on mw.misWorkspaceId = mcd.misContextWsId \n" +
//            "    left join MisRole mr on mr.misRoleId = mc.misContextRoleId\n" +
//            "    left join MisUserRole mgr on mgr.misRoleId = mc.misContextRoleId\n" +
//            "    where mgr.misUserId = ?1")
//    List<MenuItemDto> getContextByUserId(String userId);

    @Query(value = "select DISTINCT new com.asl.prd004.dto.MenuItemDto(mw.misWorkspaceId,mw.misWorkspaceName,mw.misSortNum,mw.misWorkspaceParentId)  FROM MisContext mc \n" +
            "    left join MisContextDetail mcd on mcd.misContextId = mc.misContextId \n" +
            "    left join MisWorkspace mw on mw.misWorkspaceId = mcd.misContextWsId \n" +
            "    where mc.misContextRoleId in (:roleIds)")
    Page<MenuItemDto> getContextByUserIdPageable(@Param("roleIds") List<String> roleIds, Pageable pageable);

    @Query(value = "SELECT  mis_workspace_id,mis_workspace_name from (\n" +
            "SELECT mw.mis_workspace_id ,mw.mis_workspace_name FROM mis_context mc \n" +
            "left join mis_context_detail mcd on mcd.mis_context_id = mc.mis_context_id \n" +
            "left join mis_workspace mw on mw.mis_workspace_id = mcd.mis_context_ws_id \n" +
            "left join mis_role mr on mr.mis_role_id = mc.mis_context_role_id \n" +
            "left join mis_group_role mgr on mgr.role_id = mc.mis_context_role_id \n" +
            "where mgr.user_group_id = :userId) t",
    countQuery = "SELECT  count(*) from (\n" +
            "SELECT mw.mis_workspace_id ,mw.mis_workspace_name FROM mis_context mc \n" +
            "left join mis_context_detail mcd on mcd.mis_context_id = mc.mis_context_id \n" +
            "left join mis_workspace mw on mw.mis_workspace_id = mcd.mis_context_ws_id \n" +
            "left join mis_role mr on mr.mis_role_id = mc.mis_context_role_id \n" +
            "left join mis_group_role mgr on mgr.role_id = mc.mis_context_role_id \n" +
            "where mgr.user_group_id = :userId) t",
    nativeQuery = true)
    Page<Map<String,Object>> findContextByUserIdPageable(@Param("userId") String userId, Pageable pageable);


    void deleteByMisWorkspaceId(String id);

    MisWorkspace getMisWorkspaceByMisWorkspaceId(String id);

    @Modifying
    @Query(value="update MisWorkspace set misWorkspaceParentId =?2  where misWorkspaceId =?1")
    void setParent(String id,String parentId);

}

