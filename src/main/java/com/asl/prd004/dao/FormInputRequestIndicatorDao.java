package com.asl.prd004.dao;

import com.asl.prd004.dto.FormInputRequestIndicatorDto;
import com.asl.prd004.entity.FormInputRequestindicatorS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface FormInputRequestIndicatorDao extends JpaRepository<FormInputRequestindicatorS, String> {

    List<FormInputRequestindicatorS> findFormInputRequestIncidatorByIndCode(String indCode);

    @Modifying
    @Query("delete from FormInputRequestindicatorS where formInputRequestId =?1")
    void deleteByFormInputRequestId(String formInputRequestId);

    List<FormInputRequestindicatorS> findByformInputRequestId(String formInputRequestId);

    @Query(value = "select DISTINCT firis.* " +
            " from form_input_request_indicator_s firis \n" +
            " left join form_input_request_s firs on firis.form_input_request_id = firs.id \n" +
            " left join form_input_request_period_s firps on firps.form_input_request_id = firs.id \n" +
            " left join form_input_request_office_s firos on firos.form_input_request_id = firs.id  \n" +
            " where \n" +
            " firs.form_input_request_status in ('NS', 'IP') \n" +
            " and firps.data_period_type = ?3 \n" +
            " and firps.year = ?4 \n" +
            " and firps.start_month = ?5 \n" +
            " and firps.end_month = ?6 \n" +
            " and firis.ind_code in(?1) " +
            " and firos.molu_code in (?2) " +
            " and if(?7 != '', firs.id != ?7, 1=1)", nativeQuery = true)
    List<FormInputRequestindicatorS> findIndicatorsBySamePeriod(List<String> indCodes, List<String> moluList, String dataPeriodType,
                                                                Integer year, Integer startMonth, Integer endMonth, String id);

    @Query(nativeQuery = true, value = "SELECT firis.ind_code, if(?3 = 'EN', ind_name_en, ind_name_tc) as indicatorName " +
            " from form_input_request_indicator_s firis" +
            " left join indicators_s is2 on firis.ind_code = is2.ind_code \n" +
            " WHERE firis.form_input_request_id = ?1 " +
            " and is2.subcategory_code = ?2 ")
    List<Map<String, Object>> findByFormInputRequestIdAndSubcategoryCode(String formInputRequestId, String subcategoryCode, String lang);
}