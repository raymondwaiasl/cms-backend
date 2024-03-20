package com.asl.prd004.dao;

import com.asl.prd004.dto.ContextDTO;
import com.asl.prd004.entity.MisContext;
import com.asl.prd004.entity.MisMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberDao extends JpaRepository<MisMember, String> {

    @Query(nativeQuery = true,value="select distinct mm.mis_member_id from mis_member mm where mm.mis_member_parent_id =?1 and mm.mis_member_child_id =?2")
    String queryMemberId(String roleId, String memberChildId);
    /*@Query(nativeQuery = true,value="select getAllSubNodes(mis_member_child_id) as nodeId from (select mis_member_parent_id,mis_member_child_id from mis_member where mis_member_child_id=?1)a")*/
    @Query(nativeQuery = true,value="select GROUP_CONCAT(mis_member_child_id) as nodeId from mis_member mm where mm.mis_member_parent_id =?1")
    String queryOldMemberIds(String roleId);
    @Modifying
    @Query("delete from MisMember where misMemberParentId =?1 and misMemberChildId=?2")
    void deleteMember(String parentId,String childId);
    @Query(nativeQuery = true,value="SELECT GROUP_CONCAT(mis_member_parent_id)FROM mis_member WHERE mis_member_child_id=?1")
    String queryParentMemberId(String s);
    @Query(nativeQuery = true,value="SELECT GROUP_CONCAT(mis_member_child_id SEPARATOR ',' )as mis_member_child_id FROM mis_member WHERE mis_member_parent_id=?1")
    String queryOldMemberIdsByGroup(String groupId);
    @Modifying
    @Query("delete from MisMember where misMemberParentId =?1 and misMemberChildId=?2")
    void deleteMemberByGroup(String groupId, String s);
    @Query(nativeQuery = true,value="SELECT distinct mis_member_id FROM mis_member WHERE mis_member_parent_id=?1 and mis_member_child_id=?2")
    String queryMemberIdByGroup(String groupId, String memberChildId);
    @Query(nativeQuery = true,value="SELECT GROUP_CONCAT(mis_member_child_id)\n" +
            "FROM mis_member\n" +
            "WHERE mis_member_parent_id=?1")
    String queryChildNodeIds(String id);
    @Modifying
    @Query("delete from MisMember where  misMemberChildId=?1")
    void deleteMemberByUserId(String misUserId);
}
