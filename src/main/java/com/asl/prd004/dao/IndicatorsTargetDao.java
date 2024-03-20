package com.asl.prd004.dao;

import com.asl.prd004.dto.CategoryDto;
import com.asl.prd004.dto.IndicatorTargetDto;
import com.asl.prd004.entity.IndicatorsTargetS;
import com.asl.prd004.entity.MoluOfficeS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IndicatorsTargetDao extends JpaRepository<IndicatorsTargetS, String> {

    Page<IndicatorsTargetS> findAll(Specification<IndicatorsTargetS> specification, Pageable page);

    @Query(value = "select new com.asl.prd004.dto.IndicatorTargetDto(ss.categoryCode, its.year) " +
            "from IndicatorsTargetS its\n" +
            "LEFT JOIN IndicatorsS is2 on its.indCode = is2.indCode \n" +
            "LEFT JOIN SubcategoryS ss on is2.subcategoryCode = ss.subcategoryCode \n" +
            "WHERE is2.indCode = ?1 ")
    IndicatorTargetDto findCategoryByIndCode(String indCode);

    List<IndicatorsTargetS> findIndicatorTargetsByIndCodeAndYear(String indCode, Integer year);
}