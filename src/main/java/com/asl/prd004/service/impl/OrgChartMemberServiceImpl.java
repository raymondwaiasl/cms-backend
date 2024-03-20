package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.IOrgChartMemberService;
import com.asl.prd004.utils.AESUtil;
import com.asl.prd004.utils.CastEntity;
import com.asl.prd004.utils.ConstUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrgChartMemberServiceImpl implements IOrgChartMemberService {
    @Autowired
    private OrgChartDao orgChartDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private MisUserDao misUserDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public List<OrgChartDTO> findAllOrgChartData() {
        // 获取数据库中带有有父子关系的数据
        List<OrgChartDTO> data = orgChartDao.findAllOrgChartData();
        // 复制data数据
        List<OrgChartDTO> menuList = new ArrayList<>(data);
        // 遍历两次data来组装带有children关联性的对象，如果找到子级就删除menuList的数据
        for (OrgChartDTO entity : data) {
            for (OrgChartDTO entity2 : data) {
                //如果本级id与数据的父id相同，就说明是子父级关系
                if (entity.getId().equals(entity2.getParentId())) {
                    entity.getChildren().add(entity2);
                    menuList.remove(entity2);
                }
            }
        }
        MisUser user = misUserDao.getMisUserByMisUserId(ContextHolder.getUserId());
        List<OrgChartDTO> returnData = new ArrayList<>();
        if("Y".equals(user.getIsAdmin())){
            return menuList;
        }else if(!StringUtils.isEmpty(user.getCurrentGroup())){
            MisGroup userGroup = groupDao.getMisGroupByMisGroupId(user.getCurrentGroup());
            if(null != userGroup){
                if("Y".equals(userGroup.getMisGroupIsAdmin())){
                    OrgChartDTO child = new OrgChartDTO("", user.getCurrentGroup(), "", "");
                    for (OrgChartDTO root : menuList) {
                        OrgChartDTO temp = findRoot(root, child);
                        if(temp != null){
                            returnData.add(root);
                            break;
                        }
                    }
                    return returnData;
                }else{
                    return returnData;
                }
            }

        }else{
            List<String> groupIds = orgChartDao.getGroupIdByUserId(ContextHolder.getUserId());
            System.out.println("groupIds========================" + groupIds.get(0));
            if(groupIds != null && groupIds.size() > 0){
                MisGroup userGroup = groupDao.getMisGroupByMisGroupId(groupIds.get(0));
                if("Y".equals(userGroup.getMisGroupIsAdmin())){
                    OrgChartDTO child = new OrgChartDTO("", user.getCurrentGroup(), "", "");
                    for (OrgChartDTO root : menuList) {
                        OrgChartDTO temp = findRoot(root, child);
                        if(temp != null){
                            returnData.add(root);
                            break;
                        }
                    }
                    return returnData;
                }else{
                    return returnData;
                }
            }
        }
        return menuList;
    }

    private OrgChartDTO findRoot(OrgChartDTO node, OrgChartDTO child) {
        if (node == null || child == null) {
            return null;
        }

        if (node.getId().equals(child.getId())) {
            // 找到了根节点
            return node;
        }

        for (OrgChartDTO childNode : (List<OrgChartDTO>) node.getChildren()) {
            OrgChartDTO foundNode = findRoot(childNode, child);
            if (foundNode != null) {
                // 在子节点的子树中找到了根节点
                return foundNode;
            }
        }
        // 在整个树中未找到根节点
        return null;
    }

    @Override
    public List getMember(String level) {
        return null;
    }

    @Override
    public PageDataDto getMemberByRole(String nodeId, JSONObject... params) {
        PageDataDto roleDto = null;
        try {
            JSONObject pageState = params[0];
            int pageNum = pageState.getInt("page") - 1;
            int pageSize = pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);

            if (params.length == 2 && params[1].length() != 0) {
                JSONObject sortState = params[1];
                String sortField = "mis_role_id";
                if (sortState.getString("sort").equalsIgnoreCase("asc")) {
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
                } else {
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
                }
            }
            Page<Object[]> rolePage = roleDao.queryMemberByRole(pageable, nodeId);
            List<UserGroupInfoDTO> roleList = CastEntity.castEntity(rolePage.getContent(), UserGroupInfoDTO.class);
            roleDto = new PageDataDto();
            roleDto.setData(roleList);
            roleDto.setTotal(rolePage.getTotalElements());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return roleDto;
    }

    @Override
    public PageDataDto getMemberByGroupAndUser(String nodeId, JSONObject... params) {

        PageDataDto orgDto = null;
        try {
            JSONObject pageState = params[0];
            int pageNum = pageState.getInt("page") - 1;
            int pageSize = pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);

            if (params.length == 2 && params[1].length() != 0) {
                JSONObject sortState = params[1];
                String sortField = "mis_member_child_id";
                if (sortState.getString("sort").equalsIgnoreCase("asc")) {
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
                } else {
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
                }
            }
            Page<Object[]> groupUserLs = groupDao.getMemberByGroupAndUser(nodeId, pageable);
            List<UserGroupInfoDTO> groupUserDTO = CastEntity.castEntity(groupUserLs.getContent(), UserGroupInfoDTO.class);
            orgDto = new PageDataDto();
            orgDto.setData(groupUserDTO);
            orgDto.setTotal(groupUserLs.getTotalElements());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orgDto;
    }

    @Override
    public List<OrgChartDTO> getMemberInfoByRole(String roleId) {
        List<OrgChartDTO> memberByRole = orgChartDao.getMemberInfoByRole(roleId);
        return memberByRole;
    }

    @Override
    public List<Object[]> getGroupInfoById(String roleId) {
        List<Object[]> groupByRole = orgChartDao.getGroupInfoById(roleId);
        return groupByRole;
    }

    @Override
    @Transactional
    public int updateRole(String roleId, String roleName) {
        MisRole misRole = new MisRole();
        misRole.setMisRoleId(roleId);
        misRole.setMisRoleName(roleName);
        roleDao.save(misRole);
        return 1;
    }

    @Override
    @Transactional
    public int insertMember(String memberId, String roleId, String memberChildId, String childType) {
        MisMember misMember = new MisMember();
        misMember.setMisMemberId(memberId);
        misMember.setMisMemberParentId(roleId);
        if (childType == ConstUtils.ROLETYPE) {
            misMember.setMisMemberParentType(ConstUtils.RootTYPE);
        } else if (childType == ConstUtils.GORUPTYPE) {
            misMember.setMisMemberParentType(ConstUtils.ROLETYPE);
        } else if (childType == ConstUtils.USERTYPE) {
            misMember.setMisMemberParentType(ConstUtils.GORUPTYPE);
        }
        misMember.setMisMemberChildId(memberChildId);
        misMember.setMisMemberChildType(childType);
        memberDao.save(misMember);
        return 1;
    }


    @Override
    public String queryMemberId(String roleId, String memberChildId) {
        return memberDao.queryMemberId(roleId, memberChildId);
    }

    @Override
    public String queryOldMemberIds(String roleId) {
        return memberDao.queryOldMemberIds(roleId);
    }

    @Override
    @Transactional
    public void deleteMember(String parentId, String childId) {
        memberDao.deleteMember(parentId, childId);
    }

    @Override
    public String queryParentMemberId(String s) {
        return memberDao.queryParentMemberId(s);
    }

    @Override
    @Transactional
    public void insertRole(String roleId, String roleName) {
        MisRole misRole = new MisRole();
        misRole.setMisRoleId(roleId);
        misRole.setMisRoleName(roleName);
        roleDao.save(misRole);
    }

    @Override
    public List<Object[]> getMemberInfoByGroup(String groupId) {
        List<Object[]> misUsers = misUserDao.getMemberInfoByGroup(groupId);
        return misUsers;
    }

    @Override
    public List<Object[]> getMemberInfoByNoGroup(String groupId) {
        List<Object[]> misUsers = misUserDao.getMemberInfoByNoGroup(groupId);
        return misUsers;
    }

    @Override
    @Transactional
    public int updateGroup(String groupId, String groupName, String isAdmin, String defaultFolderId) {
        MisGroup misGroup = new MisGroup();
        misGroup.setMisGroupId(groupId);
        misGroup.setMisGroupName(groupName);
        misGroup.setMisGroupIsAdmin(isAdmin);
        misGroup.setMisGroupDefaultFolder(defaultFolderId);
        groupDao.save(misGroup);
        return 1;
    }

    @Override
    public String queryOldMemberIdsByGroup(String groupId) {
        return memberDao.queryOldMemberIdsByGroup(groupId);
    }

    @Override
    @Transactional
    public void deleteMemberByGroup(String groupId, String s) {
        memberDao.deleteMemberByGroup(groupId, s);
    }

    @Override
    public String queryMemberIdByGroup(String groupId, String memberChildId) {
        return memberDao.queryMemberIdByGroup(groupId, memberChildId);
    }

    @Override
    public List<Object[]> getUserInfoByGroup() {
        return misUserDao.getUserInfoByGroup();
    }

    @Override
    @Transactional
    public int insertGroup(String groupId, String nodeId, String groupName, String isAdmin, String defaultFolderId) {
        MisGroup misGroup = new MisGroup();
        misGroup.setMisGroupId(groupId);
        misGroup.setMisGroupName(groupName);
        misGroup.setMisParentGroupId(nodeId);
        misGroup.setMisGroupIsAdmin(isAdmin);
        misGroup.setMisGroupDefaultFolder(defaultFolderId);
        groupDao.save(misGroup);
        return 1;
    }

    @Override
    @Transactional
    public int insertMemberByGroup(String memberId, String nodeId, String roletype, String groupId, String goruptype) {
        MisMember misMember = new MisMember();
        misMember.setMisMemberId(memberId);
        misMember.setMisMemberParentId(nodeId);
        misMember.setMisMemberParentType(roletype);
        misMember.setMisMemberChildId(groupId);
        misMember.setMisMemberChildType(goruptype);
        memberDao.save(misMember);
        return 1;
    }

    @Override
    public String queryChildNodeIds(String id) {
        return memberDao.queryChildNodeIds(id);
    }

    @Override
    @Transactional
    public int delGroupUser(String id, String childNodeAllId) {
        memberDao.deleteMemberByGroup(id, childNodeAllId);
        return 1;
    }

    @Override
    public List<MisUser> getUserInfoByUserId(String id) {
        return misUserDao.getUserInfoByUserId(id);
    }


    @Override
    @Transactional
    public int delRoleById(String roleId) {
        roleDao.delRoleById(roleId);
        return 1;
    }


    @Override
    public String queryChildType(String memberId) {
        return groupDao.queryChildType(memberId);
    }


    @Override
    public List<MisUser> queryUserInfo(String groupId) {
        List<MisUser> userList = misUserDao.queryUserInfo(groupId);
        return userList;
    }

    @Override
    public List<Object[]> queryAllGroup() {
        List<Object[]> ls = groupDao.queryAllGroup();
        return ls;
    }

    @Override
    public List<Object[]> queryAllUser() {
        List<Object[]> ls = misUserDao.queryAllUser();
        return ls;
    }


    @Override
    public List<String> getRolesByUserId(String userId) {
        List<OrgChartDTO> nodes = orgChartDao.findAllOrgChartData();
        List<OrgChartDTO> roleNodes = new ArrayList<>();
        List<String> roleIdList = new ArrayList<>();
        for (OrgChartDTO o : nodes) {
            if (o.getLevel().equals("2")) {
                roleNodes.add(o);
            }
        }
        for (OrgChartDTO o : roleNodes) {
            List<OrgChartDTO> temp = new ArrayList<>();
            temp = getUsersByRoleId(o.getId());
            if (isContains(temp, userId)) {
                roleIdList.add(o.getId());
            }

        }
        return roleIdList;
    }

    private boolean isContains(List<OrgChartDTO> list, String id) {
        List<String> strList = new ArrayList<>();
        for (OrgChartDTO o : list) {
            strList.add(o.getId());
        }
        return strList.contains(id);
    }


    private List<OrgChartDTO> getUsersByRoleId(String roleId) {
        List<OrgChartDTO> nodes = orgChartDao.findAllOrgChartData();
        treeList(nodes, roleId);

        List<OrgChartDTO> users = new ArrayList<>();
        for (OrgChartDTO o : childNode) {
            if (o.getLevel().equals("4")) {
                users.add(o);
            }
        }
        childNode = new ArrayList<>();
        lastChildNode = new ArrayList<>();
        return users;
    }

    //用于存放所有的子节点和终结点
    //执行函数treeMenuList后childNode和lastChildNode都会存在重复值，因此需要去重处理
    List<OrgChartDTO> childNode = new ArrayList<>();
    List<OrgChartDTO> lastChildNode = new ArrayList<>();

    private List<OrgChartDTO> treeList(List<OrgChartDTO> treeNodes, String pId) {
        List<OrgChartDTO> tempTreeNode = new ArrayList<>();
        List<OrgChartDTO> tempTreeNode1 = new ArrayList<>();
        for (OrgChartDTO node : treeNodes) {
            if (node.getParentId().equals(pId)) {
                //说明存在子节点
                tempTreeNode1 = treeList(treeNodes, node.getId());
                if (tempTreeNode1.isEmpty()) {
                    //不存在子节点
                    lastChildNode.add(node);
                }
                childNode.add(node);
                //用于让上一级判断是否存在子节点
                //因为存在子节点则tempTreeNode不为空
                //函数结束后返回tempTreeNode给上一级以供判断
                tempTreeNode.add(node);
            }
        }
        return tempTreeNode;
    }


    @Override
    public List<MisUser> queryAllUserInfo() {
        return null;
    }


    @Override
    public int queryGroupLevel(String nodeId) {
        return groupDao.queryGroupLevel(nodeId);
    }

    @Override
    public int queryChildGroupData(String nodeId) {
        return groupDao.queryChildGroupData(nodeId);
    }


    @Override
    public List<DicDto> getGroupsByUserId(String userId) {
        return groupDao.getGroupsByUserId(userId);
    }

    @Override
    public MisUser getMyProfileByUserId(String userId) {
        try {
            MisUser user = misUserDao.getMisUserByMisUserId(userId);
            if (user != null) {
                user.setMisUserPassword(AESUtil.decryptAES(user.getMisUserPassword()));
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public PageDataDto queryAllGroupData( JSONObject pageState, JSONObject sortState) {
        PageDataDto orgDto = null;
        try {
            int pageNum = pageState.getInt("page")-1;
            int pageSize =  pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            String sortField ="mis_group_id";
            if(sortState.getString("sort").equalsIgnoreCase("asc")){
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
            }else{
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
            }

            Page<Object[]> groupUserLs = groupDao.queryAllGroupData(pageable);
            List<UserGroupDataDTO> groupUserDTO= CastEntity.castEntity(groupUserLs.getContent(),UserGroupDataDTO.class);
            orgDto = new PageDataDto();
            orgDto.setData(groupUserDTO);
            orgDto.setTotal(groupUserLs.getTotalElements());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orgDto;
    }

}
