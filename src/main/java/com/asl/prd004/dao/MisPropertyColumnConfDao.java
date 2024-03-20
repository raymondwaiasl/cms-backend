package com.asl.prd004.dao;

import com.asl.prd004.entity.MisPropertyColumnConf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MisPropertyColumnConfDao extends JpaRepository<MisPropertyColumnConf, String> {
    Optional<MisPropertyColumnConf> findByMisPropertyConfigDetailId(String columnConfigId);

}
