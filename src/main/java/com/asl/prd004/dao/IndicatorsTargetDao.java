package com.asl.prd004.dao;

import com.asl.prd004.dto.*;
import com.asl.prd004.entity.IndicatorsTargetS;
import com.asl.prd004.entity.MoluOfficeS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface IndicatorsTargetDao extends JpaRepository<IndicatorsTargetS, String> {

    @Query(nativeQuery = true, value = "SELECT\n" +
            " t.*,\n" +
            " IF (?3 = 'EN',\n" +
            " cs.category_name_en,\n" +
            " cs.category_name_tc) as category\n" +
            " FROM\n" +
            " (\n" +
                " SELECT\n" +
                "  its.`year`,\n" +
                "  ss.category_code\n" +
                " from\n" +
                " indicators_target_s its\n" +
                " left join indicators_s is2 on\n" +
                " its.ind_code = is2.ind_code\n" +
                " LEFT JOIN subcategory_s ss on\n" +
                " is2.subcategory_code = ss.subcategory_code\n" +
                " where 1=1 \n" +
                " and if(?1 != '', ss.category_code = ?1, 1=1) \n" +
                " and if(?2 != '', its.year = ?2, 1=1) \n" +
                " GROUP by\n" +
                "  its.`year`,\n" +
                "  ss.category_code\n" +
            " )t\n" +
            " LEFT JOIN category_s cs on\n" +
            " t.category_code = cs.category_code",
            countQuery = "SELECT\n" +
                    " COUNT(1) \n" +
                    " FROM\n" +
                    " (\n" +
                        " SELECT\n" +
                        "  its.`year`,\n" +
                        "  ss.category_code\n" +
                        " from\n" +
                        "  indicators_target_s its\n" +
                        " left join indicators_s is2 on\n" +
                        "  its.ind_code = is2.ind_code\n" +
                        " LEFT JOIN subcategory_s ss on\n" +
                        "  is2.subcategory_code = ss.subcategory_code\n" +
                        " where 1=1 \n" +
                        " and if(?1 != '', ss.category_code = ?1, 1=1) \n" +
                        " and if(?2 != '', its.year = ?2, 1=1) \n" +
                        " GROUP by\n" +
                        "  its.`year`,\n" +
                        "  ss.category_code\n" +
                    " ) t")
    Page<Map<String, Object>> findAll(String categoryCode, String year, String lang, Pageable pageable);

    @Query(value = "select new com.asl.prd004.dto.IndicatorTargetDto(ss.categoryCode, its.year) " +
            "from IndicatorsTargetS its\n" +
            "LEFT JOIN IndicatorsS is2 on its.indCode = is2.indCode \n" +
            "LEFT JOIN SubcategoryS ss on is2.subcategoryCode = ss.subcategoryCode \n" +
            "WHERE is2.indCode = ?1 ")
    IndicatorTargetDto findCategoryByIndCode(String indCode);

    @Query(nativeQuery = false, value = "select new com.asl.prd004.dto.IndicatorTargetDetailDto(indCode, moluCode, target) " +
            " from IndicatorsTargetS where indCode in(?1) and year = ?2")
    List<IndicatorTargetDetailDto> findAllByIndCodeSAndYear(List<String> indCodeList, Integer year);

    IndicatorsTargetS findIndicatorTargetsByIndCodeAndYearAndMoluCode(String indCode, Integer year, String moluCode);

    @Query(nativeQuery = false, value = "select new com.asl.prd004.dto.FormInputTargetDto( indCode as indicatorCode, sum(target) as totalTarget) " +
            " from IndicatorsTargetS where indCode in(?1) and year = ?2 and moluCode in(?3) group by indCode")
    List<FormInputTargetDto> findByIndCodeAndYearAndMoluCodes(List<String> indCodeList, Integer year, List<String> moluCodesList);

    @Query(nativeQuery = true, value = "select ind_code as indicatorCode, 'TARGET' as type, sum(target) as value \n" +
            " from indicators_target_s \n" +
            " where year = ?1 \n" +
            " and ind_code in(?2) \n" +
            " and if(?3 = 1, molu_code in(?4), 1=1) \n" +
            " group by ind_code ")
    List<Map<String, Object>> findTargetGroupByIndCode(Integer year, List<String> indCodeList, Integer moluCodeBoolean, List<String> moluCodesList);


}