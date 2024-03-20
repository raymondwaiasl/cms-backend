package com.asl.prd004.dao;

import com.asl.prd004.dto.CategoryDto;
import com.asl.prd004.dto.IndicatorDto;
import com.asl.prd004.dto.TypeRefPropertyDto;
import com.asl.prd004.entity.IndicatorsS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IndicatorDao extends JpaRepository<IndicatorsS, String> {

//    @Query(value = "select new com.asl.prd004.dto.IndicatorDto(m.id, m.indCode, m.subcategoryCode, m.indNameEn, m.indNameTc, m.dataType, m.currency, m.active) " +
//            "from IndicatorsS m ")
    Page<IndicatorsS> findAll(Specification specification, Pageable pageable);

    @Query(value = "select new com.asl.prd004.dto.IndicatorDto(m.id, m.indCode, m.subcategoryCode, m.indNameEn, m.indNameTc, m.dataType, m.currency, m.active) " +
            "from IndicatorsS m  where m.id = ?1 ")
    IndicatorDto findIndicatorsById(String id);

    List<IndicatorsS> findIndicatorsByIndCode(String indCode);

    @Query(value = "select new com.asl.prd004.dto.IndicatorDto(Ins.id, Ins.indCode, Ins.subcategoryCode, Ins.indNameEn, Ins.indNameTc, Ins.dataType, Ins.currency, Ins.active) " +
            "from IndicatorsS Ins\n" +
            "WHERE Ins.subcategoryCode IN(?1) ", nativeQuery = false)
    List<IndicatorDto> findIndicatorsSubCategoryCode(List<String> subcategoryCodes);

    @Query(value = "select new com.asl.prd004.dto.IndicatorDto(m.id, m.indCode, m.subcategoryCode, m.indNameEn, m.indNameTc, m.dataType, m.currency, m.active) " +
            "from IndicatorsS m  where m.subcategoryCode = ?1 ")
    List<IndicatorDto> findIndicatorsBySubcategoryCode(String subcategoryCode);

    /*@Query(value = "select new com.asl.prd004.dto.CategoryDto(ss.categoryCode) " +
            "from IndicatorsS is2\n" +
            "LEFT JOIN SubcategoryS ss on is2.subcategoryCode = ss.subcategoryCode \n" +
            "WHERE is2.indCode = ?1 ")
    CategoryDto findCategoryByIndCode(String indCode);

    @Query(value = "select new com.asl.prd004.dto.TypeRefPropertyDto( mcr.misCrossRefId, " +
            " pt.misTypeId, pt.misTypeLabel," +
            " ct.misTypeId, ct.misTypeLabel )" +
            " from MisCrossRef mcr " +
            " left join MisType pt on pt.misTypeId = mcr.misCrossRefParentTable\n" +
            " left join MisColumn pc on pc.misColumnId = mcr.misCrossRefParentKey\n" +
            " left join MisType ct on ct.misTypeId = mcr.misCrossRefChildTable\n" +
            " left join MisColumn cc on cc.misColumnId = mcr.misCrossRefChildKey " +
            " where pt.misTypeId in (:typeIds) or ct.misTypeId in (:typeIds) "
    )
    List<TypeRefPropertyDto> findAllTypeRef(@Param("typeIds") List<String> typeIds);*/

}