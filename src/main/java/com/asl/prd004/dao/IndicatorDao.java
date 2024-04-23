package com.asl.prd004.dao;

import com.asl.prd004.dto.IndicatorDetailDto;
import com.asl.prd004.dto.IndicatorDto;
import com.asl.prd004.entity.IndicatorsS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface IndicatorDao extends JpaRepository<IndicatorsS, String> {

    @Query(nativeQuery = true, value = "SELECT\n" +
            " is2.id as indicatorId,\n" +
            " is2.ind_code as indicatorCode,\n" +
            " if(?6 = 'EN', ss.subcategory_name_en, ss.subcategory_name_tc) as subcategoryName,\n"+
            " if(?6 = 'EN', c.category_name_en, c.category_name_tc) as categoryName,\n"+
            " is2.ind_name_en as indicatorNameEn,\n"+
            " is2.ind_name_tc as indicatorNameTc,\n"+
            " is2.active,\n" +
            " ss.category_code\n" +
            " from\n" +
            " indicators_s is2\n" +
            " left join subcategory_s ss on\n" +
            " is2.subcategory_code = ss.subcategory_code\n" +
            " left join category_s c on\n" +
            " ss.category_code = c.category_code\n" +
            " WHERE if(?1 != '', ss.category_code = ?1, 1=1) \n" +
            " and if(?2 != '', is2.subcategory_code = ?2, 1=1) \n" +
            " and if(?3 != '', is2.ind_code = ?3, 1=1) \n" +
            " and if(?4 != '', if(?6 = 'EN', is2.ind_name_en like concat('%', ?4, '%'), is2.ind_name_tc like concat('%', ?4, '%')), 1=1) \n" +
            " and if(?5 >= 0, is2.active = ?5, 1=1)",
            countQuery = "select count(1) from " +
                    "( " +
                    " SELECT is2.id as indicatorId \n" +
                    " from\n" +
                    " indicators_s is2\n" +
                    " left join subcategory_s ss on\n" +
                    " is2.subcategory_code = ss.subcategory_code\n" +
                    " WHERE if(?1 != '', ss.category_code = ?1, 1=1) \n" +
                    " and if(?2 != '', is2.subcategory_code = ?2, 1=1) \n" +
                    " and if(?3 != '', is2.ind_code = ?3, 1=1) \n" +
                    " and if(?4 != '', if(?6 = 'EN', is2.ind_name_en like concat('%', ?4, '%'), is2.ind_name_tc like concat('%', ?4, '%')), 1=1) \n" +
                    " and if(?5 >= 0, is2.active = ?5, 1=1)" +
                    ") t")
    Page<Map<String, Object>> findAll(String categoryCode, String subCategoryCode, String indicatorCode,
                                       String indicatorName, Integer active, String lang, Pageable pageable);


    @Query(value = "select new com.asl.prd004.dto.IndicatorDetailDto(m.id, m.indCode, m.subcategoryCode, m.indNameEn," +
            " m.indNameTc, m.dataType, m.currency, m.active, m.subIndicatorNameEn, m.subIndicatorNameTc, s.categoryCode) " +
            " from IndicatorsS m" +
            " left join SubcategoryS s on s.subcategoryCode = m.subcategoryCode where m.id = ?1 ")
    IndicatorDetailDto findIndicatorsById(String id);

    List<IndicatorsS> findIndicatorsByIndCode(String indCode);

    @Query(value = "select new com.asl.prd004.dto.IndicatorDto(Ins.id, Ins.indCode, Ins.subcategoryCode, Ins.indNameEn, Ins.indNameTc, Ins.dataType, Ins.currency, Ins.active) " +
            "from IndicatorsS Ins\n" +
            "WHERE Ins.subcategoryCode IN(?1) ", nativeQuery = false)
    List<IndicatorDto> findIndicatorsSubCategoryCode(List<String> subcategoryCodes);

    @Query(value = "select Ins.ind_code as indicatorCode, Ins.subcategory_code as subcategoryCode, if(?2 = 'EN', Ins.ind_name_en, Ins.ind_name_tc) as indicatorName" +
            " from indicators_s Ins\n" +
            " WHERE Ins.subcategory_code IN(?1) ", nativeQuery = true)
    List<Map<String, Object>> findBySubCategoryCodes(List<String> subcategoryCodes, String lang);

    @Query(value = "select new com.asl.prd004.dto.IndicatorDto(m.id, m.indCode, m.subcategoryCode, m.indNameEn, m.indNameTc, m.dataType, m.currency, m.active) " +
            "from IndicatorsS m  where m.subcategoryCode = ?1 ")
    List<IndicatorDto> findIndicatorsBySubcategoryCode(String subcategoryCode);

    List<IndicatorsS> findByIndCode(String indCode);

}