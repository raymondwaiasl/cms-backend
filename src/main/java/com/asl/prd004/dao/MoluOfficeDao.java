package com.asl.prd004.dao;

import com.asl.prd004.dto.MoluOfficeDto;
import com.asl.prd004.entity.MoluOfficeS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MoluOfficeDao extends JpaRepository<MoluOfficeS, String> {

    @Query( value = "select new com.asl.prd004.dto.MoluOfficeDto(c.moluCode,c.moCode,c.moluType,c.moluNameEn,c.moluNameTc,c.active) " +
            "from MoluOfficeS c ")
    Page<MoluOfficeDto> getMoluList(Pageable pageable);

    Page<MoluOfficeS> findAll(Specification specification, Pageable pageable);

    MoluOfficeS findMoluOfficeById(String id);

    List<MoluOfficeS> findByMoluType(String moluType);

    List<MoluOfficeS> findByMoCode(String moCode);

}