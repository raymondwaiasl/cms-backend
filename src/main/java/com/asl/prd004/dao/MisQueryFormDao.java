package com.asl.prd004.dao;

import com.asl.prd004.dto.MisQueryFormDTO;
import com.asl.prd004.dto.QueryFormInfoDto;
import com.asl.prd004.dto.QueryListDto;
import com.asl.prd004.entity.MisQueryForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MisQueryFormDao extends JpaRepository<MisQueryForm, String> {

    @Query(value = "select new com.asl.prd004.dto.QueryListDto(m.misQfId,m.misQfName,t.misTypeLabel) " +
            "from MisQueryForm m,MisType t where m.misQfTableId = t.misTypeId and m.misQfParentId is null")
    List<QueryListDto> getQueryList();

    @Query(value = "select new com.asl.prd004.dto.QueryListDto(m.misQfId,m.misQfName,t.misTypeLabel) " +
            "from MisQueryForm m join MisType t on m.misQfTableId = t.misTypeId and m.misQfParentId is null")
    Page<QueryListDto> getQueryListPageable(Pageable pageable);

    @Query(value = "select new com.asl.prd004.dto.MisQueryFormDTO(m.misQfId, m.misQfName, m.misQfTableId, m.misQfPublic, m.misQfGroupId ) " +
            "from MisQueryForm m where m.misQfId = ?1 ")
    List<MisQueryFormDTO> getQueryFormInfoById(String id);

    @Query(value = "select new com.asl.prd004.dto.MisQueryFormDTO(m.misQfId, m.misQfName, m.misQfTableId, m.misQfPublic, m.misQfGroupId ) " +
            "from MisQueryForm m where m.misQfParentId = ?1 ")
    List<MisQueryFormDTO> getQueryFormCrossRefInfoById(String id);

    @Modifying
    @Query("DELETE FROM MisQueryForm mqf WHERE mqf.misQfParentId = ?1")
    void deleteByParentId(String parentId);
}
