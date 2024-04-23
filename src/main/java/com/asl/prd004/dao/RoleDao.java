package com.asl.prd004.dao;

import com.asl.prd004.entity.MisRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleDao extends JpaRepository<MisRole, Long> {
    @Query(nativeQuery = true,value=" select  mr.mis_role_id as id, mr.mis_role_name as name, 'root' as  type, '' as isAdmin, '' as defaultFolderId from mis_role mr\n" +
            "join mis_member mm on mm.mis_member_child_id = mr.mis_role_id \n" +
            "where mm.mis_member_parent_id = :memberId ")
    Page<Object[]> queryMemberByRole(Pageable pageable,String memberId);
    @Modifying
    @Query(nativeQuery = true,value="delete from  mis_role   where mis_role_id =?1")
    void delRoleById(String roleId);

    @Query(nativeQuery = true,value="select mt.mis_role_id from mis_role mt ")
    List<String> getAllRoleId();

    @Query(value="select mt.misRoleName from MisRole mt where mt.misRoleId in (:roleIds)")
    List<String> getRoleNameByRoleIds(List<String> roleIds);
}
