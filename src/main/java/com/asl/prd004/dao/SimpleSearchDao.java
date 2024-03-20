package com.asl.prd004.dao;

import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.SimpleSearchResponseDto;
import com.asl.prd004.dto.WidgetDto;
import com.asl.prd004.entity.MisSimpleSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SimpleSearchDao extends JpaRepository<MisSimpleSearch, String> {

    MisSimpleSearch getMisSimpleSearchByMisSimpleSearchId(String id);

    @Query(value = "select new com.asl.prd004.dto.SimpleSearchResponseDto(ss.misSimpleSearchId, ss.misSimpleSearchName, ss.misSimpleSearchSql) FROM MisSimpleSearch ss ")
    Page<SimpleSearchResponseDto> findAllSimpleSearchPageable(Pageable pageable);

    @Query(value = "select new com.asl.prd004.dto.DicDto(m.misSimpleSearchId,m.misSimpleSearchName) " +
            "from MisSimpleSearch m   ")
    List<DicDto> getSimpleSearchDic();
}
