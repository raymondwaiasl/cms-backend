package com.asl.prd004.service;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.OrgChartDTO;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.entity.MisUser;
import org.json.JSONObject;

import java.util.List;

public interface IOrgChartMemberService {
    List<OrgChartDTO> findAllOrgChartData();

    List getMember(String level);

    PageDataDto getMemberByRole(String nodeId, JSONObject... params);

    PageDataDto<Object[]> getMemberByGroupAndUser(String nodeId, JSONObject... params);

    List<OrgChartDTO> getMemberInfoByRole(String roleId);

    List<Object[]> getGroupInfoById(String roleId);

    int updateRole(String roleId, String roleName);

    int insertMember(String memberId, String roleId, String memberChildId, String childType);

    String queryChildType(String memberId);

    String queryMemberId(String roleId, String memberChildId);

    String queryOldMemberIds(String roleId);

    void deleteMember(String parentId, String childId);

    String queryParentMemberId(String s);

    void insertRole(String roleId, String roleName);

    List<Object[]> getMemberInfoByGroup(String groupId);

    List<Object[]> getMemberInfoByNoGroup(String groupId);

    int updateGroup(String groupId, String groupName, String isAdmin, String defaultFolderId);

    String queryOldMemberIdsByGroup(String groupId);

    void deleteMemberByGroup(String groupId, String s);

    String queryMemberIdByGroup(String groupId, String memberChildId);

    List<Object[]> getUserInfoByGroup();

    int insertGroup(String groupId,String nodeId, String groupName, String isAdmin, String defaultFolderId);

    int insertMemberByGroup(String memberId, String nodeId, String roletype, String groupId, String goruptype);

    String queryChildNodeIds(String id);

    int delGroupUser(String id, String childNodeAllId);

    List<MisUser> getUserInfoByUserId(String id);

    int delRoleById(String roleId);

    List<MisUser> queryUserInfo(String groupId);

    List<String> getRolesByUserId(String userId);

    List<Object[]> queryAllGroup();

    List<Object[]> queryAllUser();


    List<MisUser> queryAllUserInfo();


    int queryGroupLevel(String nodeId);

    int queryChildGroupData(String nodeId);

    List<DicDto> getGroupsByUserId(String userId);

    MisUser getMyProfileByUserId(String userId);

    PageDataDto queryAllGroupData(JSONObject pageState, JSONObject sort);

}
