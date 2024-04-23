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

    @Query( value = "select * " +
            "from molu_office_s where active = 1 and molu_type = ?1 ", nativeQuery = true)
    List<MoluOfficeS> findByMoluType(String moluType);

    @Query( value = "select * " +
            "from molu_office_s where active = 1 and mo_code = ?1 ", nativeQuery = true)
    List<MoluOfficeS> findByMoCode(String moCode);

    List<MoluOfficeS> findByMoluCode(String moluCode);

    List<MoluOfficeS> findByMoluCodeAndActive(String moluCode, Integer active);

    @Query( value = "SELECT mo_code from molu_office_s mos where active = 1 GROUP BY mo_code ", nativeQuery = true)
    List<String> findAllActiveMoCode();
}