package com.asl.prd004.dao;

import com.asl.prd004.entity.FormInputRequestOfficeS;
import com.asl.prd004.entity.FormInputRequestindicatorS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormInputRequestOfficeDao extends JpaRepository<FormInputRequestOfficeS, String> {

    @Modifying
    @Query("delete from FormInputRequestOfficeS where formInputRequestId =?1")
    void deleteByFormInputRequestId(String formInputRequestId);

    List<FormInputRequestOfficeS> findByformInputRequestId(String formInputRequestId);

    List<FormInputRequestOfficeS> findByMoluCode(String moluCode);
}