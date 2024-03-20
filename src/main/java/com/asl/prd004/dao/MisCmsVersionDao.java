package com.asl.prd004.dao;


import com.asl.prd004.entity.MisCmsVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * @description: TODO
 * @author: billy
 * @date: 2022/12/1 10:03
 * @version: 1.0
 */
public interface MisCmsVersionDao extends JpaRepository<MisCmsVersion, String> {
    @Query(nativeQuery = true,value="select * from mis_cms_version  where if(?1!='',mis_record_id=?1,1=1)")
    Page<MisCmsVersion> findAllVersion(String recordId,Pageable pageable);
   @Modifying
   @Query(nativeQuery = true,value="update mis_cms_version set version_status=?2 ,cms_creator_user_id=?3  where cms_version_id=?1 ")
    void updateVersion(String versionId, String locked, String userId);
}
