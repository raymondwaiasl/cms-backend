package com.asl.prd004.dao;

import com.asl.prd004.dto.FormInputRemarkDto;
import com.asl.prd004.entity.FormInputRemarkS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FormInputRemarkDao extends JpaRepository<FormInputRemarkS, String> {

    @Query(nativeQuery = false, value = "select new com.asl.prd004.dto.FormInputRemarkDto( indCode, remark) " +
            " from FormInputRemarkS where formInputEntryId = ?1 and indCode = ?2 ")
    FormInputRemarkDto findByFormInputEntryIdAndIndCode(String formInputEntryId, String indCode);
}