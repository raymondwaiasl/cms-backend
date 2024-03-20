package com.asl.prd004.dao;

import com.asl.prd004.dto.MySearchDto;
import com.asl.prd004.entity.MisSavedSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MisSavedSearchDao extends JpaRepository<MisSavedSearch, String> {
    List<MisSavedSearch> findMisSavedSearchByMisSavedSearchUserId(String userId);

    @Query(value = "select new com.asl.prd004.dto.MySearchDto(m.misSavedSearchId,m.misSavedSearchName,m.misQueryFormId,m.misSavedSearchUserId,m.misSavedSearchDate,t.misTypeName) " +
            "from MisSavedSearch m,MisQueryForm qf,MisType t where m.misQueryFormId = qf.misQfId and qf.misQfTableId = t.misTypeId and m.misSavedSearchUserId = :userId")
    Page<MySearchDto> getMySearchListPageable(@Param("userId") String userId, Pageable pageable);
}
