package com.asl.prd004.dao;

import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.vo.AccountSettingVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface MisUserDao extends JpaRepository<MisUser, Long> {

    @Query(nativeQuery = true,value="select mis_user_id as id,mis_user_name as name, '4' as type from mis_user mu where mu.mis_user_id in(\n" +
            "select distinct mis_member_child_id from mis_member mm where mis_member_parent_id=?1)")
    List<Object[]> getMemberInfoByGroup(String groupId);
    @Query(nativeQuery = true,value="select mis_user_id as id,mis_user_name as name, '4' as type from mis_user mu where mu.mis_user_id not in(\n" +
            "select distinct mis_member_child_id from mis_member mm where mis_member_parent_id=?1)")
    List<Object[]> getMemberInfoByNoGroup(String groupId);
    @Query(nativeQuery = true,value="select mis_user_id as id,mis_user_name as name, '4' as type from mis_user mu")
    List<Object[]> getUserInfoByGroup();
    @Query(value="from MisUser m where m.misUserId=?1")
    List<MisUser> getUserInfoByUserId(String id);

    List<MisUser> findByMisUserLoginId(String loginId);
    @Query(nativeQuery = true,value="select *  from mis_user mu where mu.mis_user_id in(\n" +
            "select distinct mis_member_child_id from mis_member mm where mis_member_parent_id=?1)")
    List<MisUser> queryUserInfo(String groupId);
    @Query(nativeQuery = true,value="select *  from mis_user mu where mu.mis_user_type ='01' and mu.mis_user_name = ?1 and mu.mis_user_login_id = ?2")
    List<MisUser> queryLdapUsers(String userName,String loginName);

    @Query(nativeQuery = true,value="select mis_user_id as id,mis_user_name as name, '4' as type from mis_user mu")
    List<Object[]> queryAllUser();
    @Modifying
    @Query(nativeQuery = true,value="delete from mis_user where mis_user_id=?1")
    void delUser(String misUserId);
    @Query(nativeQuery = true,value="select * from mis_user where if(?1!='',mis_user_name=?1,1=1)")
    Page<MisUser> queryUserAll(String misUserName,Pageable pageable);
    @Query(nativeQuery = true,value="select mis_user_id from mis_user where mis_user_name=?1")
    String queryUserByName(String misUserName);

    List<MisUser> findByMisUserName(String misUserName);
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value="update mis_user\n" +
                  "   set mis_user_name = ?2,\n" +
                  "       mis_email = ?3\n" +
                  " where mis_user_id = ?1")
    void changeAccountSetting(String misUserId,String userName,String emailAddress);
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value="update mis_user\n" +
                  "   set mis_user_password = ?2,\n" +
                    "       last_password_change = NOW(),\n" +
                    "       is_change = 0\n" +
                  " where mis_user_id = ?1")
    void changePassword(String misUserId,String password);

    List<MisUser> getMisUsersByMisEmailOrMisUserLoginId(String email,String loginId);

    MisUser getMisUserByMisUserId(String userId);

    @Query(nativeQuery = true,value="select * from mis_user where is_change = 0 ")
    List<MisUser> getUsersNeedChangePwd();

    @Query(nativeQuery = true,value="select * from mis_user where if(:d.getId != '',mis_user_name=?1,1=1)")
    Page<MisUser> searchUser(TypeIdDto d, Pageable pageable);

    Page<MisUser> findAll(Specification specification,Pageable pageable);


    @Query(nativeQuery = true,value="select r.mis_role_id as role_id from mis_user_role ur join mis_role r   \n" +
            "on ur.mis_role_id = r.mis_role_id where ur.mis_user_id=?1")
    List<String> getUserRoles(String userId);

}
