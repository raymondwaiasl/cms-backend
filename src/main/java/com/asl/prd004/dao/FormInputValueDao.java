package com.asl.prd004.dao;

import com.asl.prd004.dto.FormInputValueDto;
import com.asl.prd004.entity.FormInputValueS;
import com.asl.prd004.entity.FormInputEntryS;
import org.json.JSONArray;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface FormInputValueDao extends JpaRepository<FormInputValueS, String> {


    /*@Query( value = "select new com.asl.prd004.dto.FormInputValueDto(i.id, c.categoryNameEn, c.categoryNameTc, " +
            " i.indCode, i.year, i.startMonth, i.endMonth, iss.dataType) " +
            " from FormInputValueS i \n" +
            " left join FormInputEntryS e on e.id=i.formInputEntryId" +
            " left join FormInputRequestS r on r.id = e.formInputRequestID \n" +
            " left join CategoryS c on c.categoryCode = r.categoryCode" +
            " left join IndicatorsS iss on iss.indCode = i.indCode" +
            " where i.formInputEntryId = ?1")*/
    List<FormInputValueS> findByFormInputEntryId(String formInputEntryId);

    List<FormInputValueS> findByFormInputEntryIdAndStartMonth(String formInputEntryId, Integer startMonth);

    @Query(value = "SELECT t.ind_code as indicatorCode, t.start_month as startMonth, 'CURRENT' as type, " +
            " SUM(if(is2.data_type = 'Amount', t.data_amount, if(is2.data_type = 'Count', t.data_count, t.data_text))) as value " +
            " FROM \n" +
            " (\n" +
            " SELECT DISTINCT fivs.* \n" +
            " from form_input_value_s fivs \n" +
            " left join form_input_entry_s fies on fies.id = fivs.form_input_entry_id \n" +
            " LEFT JOIN  form_input_request_period_s firps on firps.form_input_request_id = fies.form_input_request_id \n" +
            " where firps.year = ?1 and firps.data_period_type = ?2 AND fies.mo_code = ?3 and fivs.start_month in (?4) \n" +
            " ) t\n" +
            " left JOIN indicators_s is2 on t.ind_code = is2.ind_code \n" +
            " GROUP by t.ind_code, t.start_month", nativeQuery = true)
    List<Map<String, Object>> findGroupByIndCodeAndMonth(Integer year, String dataPeriodType, String moCode, List<Integer> monthList);

    @Query(value = "SELECT t.ind_code as indicatorCode, case ?2" +
            " when 'A' then 'ACTUAL' \n" +
            " when 'PY' then 'PROJECTPERIOD' \n" +
            " when 'E' then 'ESTIMATE' \n" +
            " else null \n" +
            " end AS type, \n" +
            " SUM(if(is2.data_type = 'Amount', t.data_amount, if(is2.data_type = 'Count', t.data_count, t.data_text))) as value FROM \n" +
            "(\n" +
            "SELECT DISTINCT fivs.* from form_input_value_s fivs \n" +
            "left join form_input_entry_s fies on fies.id = fivs.form_input_entry_id \n" +
            "LEFT JOIN  form_input_request_period_s firps on firps.form_input_request_id = fies.form_input_request_id \n" +
            "where firps.year = ?1 and firps.data_period_type = ?2 AND if(?3 != '', fies.mo_code = ?3, 1=1) \n" +
            ") t\n" +
            "left JOIN indicators_s is2 on t.ind_code = is2.ind_code \n" +
            "GROUP by t.ind_code", nativeQuery = true)
    List<Map<String, Object>> findGroupByIndCodeByFormInputEntryId(Integer year, String dataPeriodType, String moCode);

    @Query(value = "SELECT fivs.ind_code as indicatorCode, start_month as startMonth, 'CURRENT' as type, fivs.revised as isRevised, \n" +
            " if(is2.data_type = 'Amount', fivs.data_amount, if(is2.data_type = 'Count', fivs.data_count, fivs.data_text)) as value\n" +
            " from form_input_value_s fivs\n" +
            " left join indicators_s is2 on is2.ind_code = fivs.ind_code \n" +
            " WHERE\n" +
            " form_input_entry_id = ?1 \n" +
            " and start_month in(?2)", nativeQuery = true)
    List<Map<String, Object>> findGroupByIndCodeAndMonth1(String formInputEntryId, List<Integer> monthList);


    @Query(nativeQuery = true, value = "SELECT\n" +
            " if(?10 = 'EN', cs.category_name_en, cs.category_name_tc) as category,\n" +
            " if(?10 = 'EN', ss.subcategory_name_en , ss.subcategory_name_en) as subCategory,\n" +
            " if(?10 = 'EN', is2.ind_name_en , is2.ind_name_tc) as indicator,\n" +
            " fies.data_period_type as dataPeriodType ,\n" +
            " fies.molu_code as moluCode,\n" +
            " fivs.`year`,\n" +
            " fivs.start_month as startMonth,\n" +
            " fivs.end_month as endMonth ,\n" +
            " if(is2.data_type = 'Amount', fivs.data_amount , if(is2.data_type = 'Count', fivs.data_count, fivs.data_text) ) as Count\n" +
            " FROM\n" +
            " form_input_value_s fivs\n" +
            " left join form_input_entry_s fies on\n" +
            " fivs.form_input_entry_id = fies.id\n" +
            " left join indicators_s is2 on\n" +
            " fivs.ind_code = is2.ind_code\n" +
            " LEFT join subcategory_s ss on\n" +
            " is2.subcategory_code = ss.subcategory_code\n" +
            " LEFT join category_s cs on\n" +
            " ss.category_code = cs.category_code\n" +
            " WHERE cs.category_code = ?1 \n" +
            " and ss.subcategory_code in (?2)\n" +
            " and fivs.ind_code in (?3) \n" +
            " and fies.data_period_type = ?4\n" +
            " and if(fies.data_period_type = 'A', DATE_FORMAT(CONCAT(fivs.year, '-', fivs.start_month, '-01'), '%Y-%m-%d') >=  DATE_FORMAT(CONCAT( ?5, '-', ?6, '-01'), '%Y-%m-%d')\n" +
            " and  DATE_FORMAT(CONCAT(fivs.year, '-', fivs.end_month, '-01'), '%Y-%m-%d') <=  DATE_FORMAT(CONCAT( ?7, '-', ?8, '-01'), '%Y-%m-%d'), fivs.year between ?5 and ?7) \n" +
            " and fies.molu_code in (?9)"
    )
    List<Map<String, Object>> getDataExport(String categoryCode, List<String> subCategoryCodeList, List<String> indicatorCodeList, String dataPeriodType,
                                                      int startYear, int startMonth, int endYear, int endMonth, List<String> moluList, String lang);
}