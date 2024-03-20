package com.asl.prd004.dao;

import com.asl.prd004.dto.OrgChartDTO;
import com.asl.prd004.entity.MisMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrgChartDao extends JpaRepository<MisMember, String> {

    @Query(value="select new com.asl.prd004.dto.OrgChartDTO( mm.misMemberParentId as parentId,mm.misMemberChildId as id,mm.misMemberChildType as level,(case\n" +
            "when mm.misMemberChildType='root' then (select  misOrganizationName from MisOrganization mr where mr.misOrganizationId =mm.misMemberChildId)\n" +
            "when mm.misMemberChildType='1' then (select  misRoleName from MisRole mr where mr.misRoleId=mm.misMemberChildId )\n" +
            "when mm.misMemberChildType='2' then (select  misRoleName from MisRole mr where mr.misRoleId=mm.misMemberChildId )\n" +
            "when mm.misMemberChildType='3' then  (select  misGroupName from MisGroup mg where mg.misGroupId=mm.misMemberChildId )\n" +
            "when mm.misMemberChildType='4' then (select  misUserName from MisUser mu where mu.misUserId=mm.misMemberChildId )\n" +
            "else  '其它'\n" +
            "end )as name)  from MisMember mm where mm.misMemberParentId<>'0005700000000001' or mm.misMemberChildId<>'0005700000000001' or mm.misMemberChildId<>'8888000000000001' ")
    List<OrgChartDTO> findAllOrgChartData();
    @Query(value="select new com.asl.prd004.dto.OrgChartDTO( mm.misMemberParentId as parentId,mm.misMemberChildId as id,mm.misMemberChildType as level,(case\n" +
            "when mm.misMemberChildType='root' then (select  misOrganizationName from MisOrganization mr where mr.misOrganizationId =mm.misMemberChildId)\n" +
            "when mm.misMemberChildType='1' then (select  misRoleName from MisRole mr where mr.misRoleId=mm.misMemberChildId )\n" +
            "when mm.misMemberChildType='2' then (select  misRoleName from MisRole mr where mr.misRoleId=mm.misMemberChildId )\n" +
            "when mm.misMemberChildType='3' then  (select  misGroupName from MisGroup mg where mg.misGroupId=mm.misMemberChildId )\n" +
            "when mm.misMemberChildType='4' then (select  misUserName from MisUser mu where mu.misUserId=mm.misMemberChildId )\n" +
            "else  '其它'\n" +
            "end )as name)  from MisMember mm where mm.misMemberParentId= :misMemberParentId "
            )
    List<OrgChartDTO> getMemberInfoByRole(@Param("misMemberParentId")String misMemberParentId);
    @Query(nativeQuery = true,value="select id ,name, type from (\n" +
            "            select mg.mis_group_id  id ,mg.mis_group_name name,'3' as type from mis_group mg where mg.mis_group_id not in(\n" +
            "            select mm.mis_member_child_id  from mis_member mm where mm.mis_member_parent_id=:roleId)\n" +
            "            union\n" +
            "            select mis_user_id as id,mis_user_name as name,'4' as type from mis_user mu where mu.mis_user_id not in(\n" +
            "            select mm.mis_member_child_id  from mis_member mm where mm.mis_member_parent_id in(\n" +
            "            select mm.mis_member_child_id  from mis_member mm where mm.mis_member_parent_id=:roleId and mm.mis_member_child_type =3)\n" +
            "            ))meb"
    )
    List<Object[]> getGroupInfoById(@Param("roleId")String roleId);

    @Query(nativeQuery = true,value="WITH RECURSIVE cte (mis_member_child_id, mis_member_parent_id, mis_member_child_type, mis_member_parent_type, level) AS (\n" +
            "  SELECT mis_member_child_id, mis_member_parent_id, mis_member_child_type, mis_member_parent_type, 0\n" +
            "  FROM mis_member\n" +
            "  WHERE mis_member_child_id = :userId\n" +
            "  UNION ALL\n" +
            "  SELECT t.mis_member_child_id, t.mis_member_parent_id, t.mis_member_child_type, t.mis_member_parent_type, cte.level + 1\n" +
            "  FROM mis_member t\n" +
            "  JOIN cte ON cte.mis_member_parent_id = t.mis_member_child_id\n" +
            ")\n" +
            "SELECT mis_member_child_id\n" +
            "FROM cte\n" +
            "WHERE mis_member_child_type = '3' and mis_member_parent_type = '2'"
    )
    List<String> getGroupIdByUserId(@Param("userId")String userId);

}
