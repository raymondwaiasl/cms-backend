package com.asl.prd004.dao;

import com.asl.prd004.dto.FormInputRequestPeriodDto;
import com.asl.prd004.entity.FormInputRequestPeriodS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormInputRequestPeriodDao extends JpaRepository<FormInputRequestPeriodS, String> {

    @Modifying
    @Query("delete from FormInputRequestPeriodS where formInputRequestId =?1")
    void deleteByFormInputRequestId(String formInputRequestId);

    @Query(value = "select new com.asl.prd004.dto.FormInputRequestPeriodDto(fp.year, fp.startMonth, fp.endMonth) " +
            "from FormInputRequestPeriodS fp where fp.formInputRequestId = ?1 ")
    List<FormInputRequestPeriodDto> findByformInputRequestId(String formInputRequestId);

    List<FormInputRequestPeriodS> findRequestPeriodByformInputRequestId(String formInputRequestId);

    List<FormInputRequestPeriodS> findByYearAndDataPeriodType(Integer year, String dataPeriodType);
}