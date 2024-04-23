package com.asl.prd004.dao;

import com.asl.prd004.dto.FormInputEntryDto;
import com.asl.prd004.dto.VFormDataActualDto;
import com.asl.prd004.entity.FormInputEntryS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface FormInputEntryDao extends JpaRepository<FormInputEntryS, String> {

    /*@Query(nativeQuery = true, value = "select e.* from form_input_entry_s e \n" +
            " left join form_input_request_s r on r.id = e.form_input_request_id \n" +
            " left join category_s c on c.category_code = r.category_code" +
            " left join mis_user u on u.mis_user_id = e.update_by" +
            " where if(?3 = 0, e.molu_code = ?1, 1=1) and e.form_input_status in (?2)")*/

    @Query(nativeQuery = true, value = "select e.* from form_input_entry_s e \n" +
                        " where if(?4 = 1, e.mo_code = ?1, 1=1) and " +
                        "  if(?3 = 0 and ?4 = 0, e.molu_code = ?1, 1=1) and e.form_input_status in (?2)")
    List<FormInputEntryS> getActionList(String office, List<String> formInputStatusList, Integer isCMABApprover, Integer isMOReviewer);

    Page<FormInputEntryS> findAll(Specification<FormInputEntryS> specification, Pageable page);

    FormInputEntryS findByIdAndMoluCode(String id, String office);

    List<FormInputEntryS> findByFormInputRequestIDAndMoCode(String formInputRequestID, String moCode);

    @Query(nativeQuery = true, value = "select a.ind_code as indicatorCode, 'LOCKED' as type, if(?6 = 'Amount', a.data_amount, if(?6 = 'Count', a.data_count, a.data_currency)) as value," +
            " a.revised as isRevised" +
            " from v_form_data_actual a " +
            " where a.year = ?1 and a.start_month = ?2 and mo_code = ?3 and molu_code = ?4 and ind_code = ?5")
    VFormDataActualDto findApprovalData(Integer year, Integer startMonth, String moCode, String moluCode, String indCode, String dataType);

    @Query(nativeQuery = true, value = "select ind_code as indicatorCode, 'BENCHMARK' as type, max(start_month) / 12 as value" +
            " from v_form_data_actual" +
            " where year = ?1 and if(?2 != '', mo_code = ?2, 1=1) " +
            " group by ind_code")
    List<Map<String, Object>> findApprovalLastMonth(Integer year, String moCode);

    @Query(nativeQuery = true, value = "SELECT va.start_month as startMonth, \n" +
            " va.ind_code as indicatorCode,\n" +
            " 'LOCKED' as type,\n" +
            " SUM( if(is2.data_type  = 'Amount',\n" +
            " va.data_amount,\n" +
            " if(is2.data_type  = 'Count',\n" +
            " va.data_count ,\n" +
            " va.data_currency))) as value, \n" +
            " MAX( va.revised ) as isRevised \n" +
            " from\n" +
            " v_form_data_actual va\n" +
            " left join indicators_s is2 on\n" +
            " is2.ind_code = va.ind_code\n" +
            " where\n" +
            " va.year = ?1 \n" +
            " and va.mo_code = ?2 \n" +
            " and if(?3 != '', va.molu_code = ?3, 1=1) \n" +
            " and va.start_month in(?4)\n" +
            " and va.ind_code in (?5)\n" +
            " GROUP BY\n" +
            " va.start_month ,\n" +
            " va.ind_code")
    List<Map<String, Object>> findApprovalDataGroupByMonthIndCode(Integer year, String moCode, String moluCode, List<Integer> monthList, List<String> indCodeList);

    @Query(nativeQuery = true, value = "select\n" +
            " e.id as formInputEntryId,\n" +
            " r.id  as formInputRequestId,\n" +
            " r.ref_num as refNum,\n" +
            " if(?9 = 'EN', c.category_name_en, c.category_name_tc) as category,\n" +
            " r.form_input_request_title as formInputRequestTitle,\n" +
            " e.molu_code as molu,\n" +
            " e.form_input_status as formInputStatus,\n" +
            " date_format(r.input_end_date, '%Y-%m-%d') as inputEndDate,\n" +
            " u.mis_user_name as updatedUser\n" +
            " from\n" +
            " form_input_entry_s e\n" +
            " left join form_input_request_s r on\n" +
            " e.form_input_request_id = r.id\n" +
            " left join category_s c on\n" +
            " r.category_code = c.category_code\n" +
            " left join mis_user u on\n" +
            " e.update_by = u.mis_user_id\n" +
            " where 1 = 1 \n" +
            " and if(?1 != '', e.molu_code = ?1, 1=1) \n" +
            " and if(?2 != '', r.ref_num = ?2, 1=1) \n" +
            " and if(?3 != '', r.form_input_request_title like concat('%', ?3, '%'), 1=1)\n" +
            " and if(?4 != '', r.category_code = ?4, 1=1)\n" +
            " and if(?5 != '', ?5 <= r.input_end_date, 1=1)\n" +
            " and if(?6 != '', ?6 >= r.input_start_date, 1=1)\n" +
            " and if(?7 != '', e.form_input_status = ?7, 1=1) \n" +
            " and if(?8 != '', e.mo_code = ?8, 1=1) ",
            countQuery = "select count(1) from " +
                    " (" +
                        " e.id as formInputEntryId \n" +
                        " from\n" +
                        " form_input_entry_s e\n" +
                        " left join form_input_request_s r on\n" +
                        " e.form_input_request_id = r.id\n" +
                        " where 1 = 1 \n" +
                        " and if(?1 != '', e.molu_code = ?1, 1=1) \n" +
                        " and if(?2 != '', r.ref_num = ?2, 1=1) \n" +
                        " and if(?3 != '', r.form_input_request_title like concat('%', ?3, '%'), 1=1)\n" +
                        " and if(?4 != '', r.category_code = ?4, 1=1)\n" +
                        " and if(?5 != '', ?5 <= r.input_end_date, 1=1)\n" +
                        " and if(?6 != '', ?6 >= r.input_start_date, 1=1)\n" +
                        " and if(?7 != '', e.form_input_status = ?7, 1=1)\n" +
                        " and if(?8 != '', e.mo_code = ?8, 1=1) " +
                    " ) t")
    Page<Map<String, Object>> findAll(String molu, String refNum, String formInputRequestTitle, String categoryCode, String inputStartDate,
                                      String inputEndDate, String status, String moCode, String lang, Pageable pageable);
}