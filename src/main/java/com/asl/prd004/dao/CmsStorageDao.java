package com.asl.prd004.dao;

import com.asl.prd004.entity.CmsStorage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CmsStorageDao extends JpaRepository<CmsStorage, String> {

    @Query(value = "select s from CmsStorage s")
    Page<CmsStorage> findAll(Pageable pageable);
}
