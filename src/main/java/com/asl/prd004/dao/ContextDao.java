package com.asl.prd004.dao;

import com.asl.prd004.dto.ContextDTO;
import com.asl.prd004.entity.MisContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContextDao extends JpaRepository<MisContext, String> {

    @Query(value="select new com.asl.prd004.dto.ContextDTO(mc.misContextId,mc.misContextName,mr.misRoleName) " +
            "from MisContext mc join MisRole mr on mc.misContextRoleId = mr.misRoleId")
    Page<ContextDTO> findContextList(Pageable pageable);

    @Query(value="select max(mc.misContextId) from MisContext mc")
    String getMaxId();
}
