package com.asl.prd004.dao;

import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.PropertyListDto;
import com.asl.prd004.entity.MisGroup;
import com.asl.prd004.entity.MisRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupDao extends JpaRepository<MisGroup, Long> {
    @Query(nativeQuery = true,value="select mis_group_id from mis_group mg where mg.mis_group_id= :memberId")
    String queryChildType(@Param("memberId") String memberId);
    @Query(nativeQuery = true,value="select mis_member_child_id id,(case\n" +
            "            when mm.mis_member_child_type='root' then (select  mis_organization_name from mis_organization mr where mr.mis_organization_id =mm.mis_member_child_id)\n" +
            "            when mm.mis_member_child_type='2' then (select  mis_role_name from mis_role mr where mr.mis_role_id=mm.mis_member_child_id )\n" +
            "            when mm.mis_member_child_type='3' then  (select  mis_group_name from mis_group mg where mg.mis_group_id=mm.mis_member_child_id )\n" +
            "            when mm.mis_member_child_type='4' then (select  mis_user_name from mis_user mu where mu.mis_user_id=mm.mis_member_child_id )\n" +
            "            else  '其它'\n" +
            "            end )as name," +
            "(case when mis_member_child_type='root' then 'root'" +
            " when mis_member_child_type='2' then 'role' when mis_member_child_type='3' then 'group' else 'user' end)as  type, " +
            "(case when mis_member_child_type='3' then (select  IFNULL(mg.mis_group_is_admin,'N') from mis_group mg where mg.mis_group_id=mm.mis_member_child_id ) else '' end)as isAdmin, " +
            "(case when mis_member_child_type='3' then (select  IFNULL(mg.mis_group_default_folder,'') from mis_group mg where mg.mis_group_id=mm.mis_member_child_id ) else '' end)as defaultFolderId " +
            "from mis_member mm where  find_in_set(mm.mis_member_child_id,(\n" +
            "\t\t\tSELECT GROUP_CONCAT(mis_member_child_id) as mis_member_child_id\n" +
            "\t\t\tFROM mis_member\n" +
            "\t\t\tWHERE mis_member_parent_id=?1\n" +
            "            )) and mis_member_parent_id=?1")
    Page<Object[]> getMemberByGroupAndUser(String nodeId, Pageable pageable);
    @Query(nativeQuery = true,value="select mis_group_id as id,mis_group_name as name, '3' as type from mis_group mu")
    List<Object[]> queryAllGroup();

    @Query(nativeQuery = true,value="select getParentList(?1)")
    int queryGroupLevel(String nodeId);
    @Query(nativeQuery = true,value="select count(1) from mis_group mg where mg.mis_parent_group_id =?1")
    int queryChildGroupData(String nodeId);

    List<MisGroup> findByMisGroupName(String misGroupName);

    @Query(value = "select new com.asl.prd004.dto.DicDto(g.misGroupId,g.misGroupName) " +
            "from MisMember m join MisGroup g on m.misMemberParentId = g.misGroupId  " +
            "where m.misMemberChildId = :userId and m.misMemberParentType = '3'")
    List<DicDto> getGroupsByUserId(String userId);

    MisGroup getMisGroupByMisGroupId(String groupId);

    @Query(nativeQuery = true,value="select mis_group_id as id,mis_group_name as name, '3' as type from mis_group")
    Page<Object[]> queryAllGroupData( Pageable pageable);
}
