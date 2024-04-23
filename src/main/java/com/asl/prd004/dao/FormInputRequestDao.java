package com.asl.prd004.dao;

import com.asl.prd004.dto.ConsolidateDataListDto;
import com.asl.prd004.entity.FormInputRequestS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface FormInputRequestDao extends JpaRepository<FormInputRequestS, String> {

//    @Query( value = "select new com.asl.prd004.dto.FormInputRequestDto(firs.id, firs.refNum,cs.categoryNameEn," +
//            " firs.formInputRequestTitle,cs.categoryNameTc,firs.formInputRequestStatus,firs.inputStartDate, firs.inputEndDate) " +
//            " from FormInputRequestS firs \n" +
//            " left join CategoryS cs on cs.categoryCode = firs.categoryCode")
    Page<FormInputRequestS> findAll(Specification<FormInputRequestS> specification, Pageable page);

    @Query(nativeQuery = true, value = "SELECT\n" +
            " distinct firs.id,\n" +
            " firs.ref_num as refNum,\n" +
            " firs.form_input_request_title as formInputRequestTitle,\n" +
            " firs.form_input_request_status as formInputRequestStatus,\n" +
            " DATE_FORMAT(firs.input_start_date, '%Y-%m-%d') as inputStartDate,\n" +
            " DATE_FORMAT(firs.input_end_date, '%Y-%m-%d') as inputEndDate,\n" +
            " if(?6 = 'EN', cs.category_name_en, cs.category_name_tc) as category,\n" +
            " mu.mis_user_name as requester\n" +
            " from\n" +
            " form_input_request_s firs\n" +
            " left join category_s cs on\n" +
            " firs.category_code = cs.category_code\n" +
            " LEFT JOIN form_input_request_period_s firps on\n" +
            " firs.id = firps.form_input_request_id\n" +
            " left JOIN mis_user mu on\n" +
            " firs.create_by = mu.mis_user_id\n" +
            " where 1 = 1 \n" +
            " and if(?1 != '', firs.form_input_request_title like concat('%', ?1, '%'), 1=1)\n" +
            " and if(?2 != '', ?2 <= firs.input_end_date, 1=1)\n" +
            " and if(?3 != '', ?3 >= firs.input_start_date, 1=1)\n" +
            " and if(?4 != '', firs.category_code = ?4, 1=1) \n" +
            " and if(?5 != '', firps.data_period_type = ?5, 1=1) \n" +
            " and if(?7 != '', firs.ref_num = ?7, 1=1) ",
            countQuery = "select count(1) from " +
                    " ( " +
                        " SELECT\n" +
                        " distinct firs.id " +
                        " from\n" +
                        " form_input_request_s firs\n" +
                        " LEFT JOIN form_input_request_period_s firps on\n" +
                        " firs.id = firps.form_input_request_id\n" +
                        " left JOIN mis_user mu on\n" +
                        " firs.create_by = mu.mis_user_id\n" +
                        " where 1 = 1 \n" +
                        " and if(?1 != '', firs.form_input_request_title like concat('%', ?1, '%'), 1=1)\n" +
                        " and if(?2 != '', ?2 <= firs.input_end_date, 1=1)\n" +
                        " and if(?3 != '', ?3 >= firs.input_start_date, 1=1)\n" +
                        " and if(?4 != '', firs.category_code = ?4, 1=1) \n" +
                        " and if(?5 != '', firps.data_period_type = ?5, 1=1) \n" +
                        " and if(?7 != '', firs.ref_num = ?7, 1=1) " +
                    " ) t")
    Page<Map<String, Object>> findAll(String formInputRequestTitle, String inputStartDate, String inputEndDate, String categoryCode,
                                      String dataPeriodType, String lang, String refNum, Pageable pageable);

    @Query(nativeQuery = true, value="SELECT * \n" +
            " from form_input_request_s firs \n" +
            " where \n" +
            " NOW() BETWEEN input_start_date AND input_end_date \n" +
            " and form_input_request_status = ?1 \n" +
            " AND id not in ( SELECT form_input_request_id from form_input_entry_s ) ")
    List<FormInputRequestS> getNSList(String formInputRequestStatus);

    @Query(nativeQuery = true, value="SELECT ref_num \n" +
            " FROM \n" +
            " form_input_request_s firs \n" +
            " where \n" +
            " ref_num LIKE concat(?1, '-', ?2, '-', ?3, '%') \n" +
            " order by ref_num desc limit 1 ")
    String findLastRefNum(String categoryCode, Integer year, String type);

    @Query(nativeQuery = true, value = "SELECT b.*, if(?4 = 'EN', cs.category_name_en, cs.category_name_tc) as category from category_s cs \n" +
            "INNER join \n" +
            " ( \n" +
            " select firs.category_code, firps.`year` \n" +
            " from form_input_request_s firs \n" +
            " left join form_input_request_period_s firps on firps.form_input_request_id = firs.id \n" +
            " where firps.`year` BETWEEN ?1 and ?2 \n" +
            " and if(?3 != '', firs.category_code = ?3, 1=1) \n" +
            " GROUP BY firs.category_code, firps.`year` \n" +
            ") b\n" +
            " on cs.category_code = b.category_code", countQuery = "SELECT count(1) FROM \n" +
            "( " +
            " select firs.category_code, firps.`year` from form_input_request_s firs\n" +
            " left join form_input_request_period_s firps on firps.form_input_request_id = firs.id\n" +
            " where \n" +
            " firps.`year` BETWEEN ?1 and ?2 \n" +
            " and if(?3 != '', firs.category_code = ?3, 1=1) \n" +
            " GROUP BY firs.category_code, firps.`year` \n" +
            ") t")
    Page<Map<String,Object>> searchConsolidateDataList(Integer yearStart, Integer yearEnd, String categoryCode, String lang, Pageable pageable);
}