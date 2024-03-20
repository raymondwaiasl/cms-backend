package com.asl.prd004.dao;

import com.asl.prd004.entity.MisQueryFormColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MisQueryFormColumnDao extends JpaRepository<MisQueryFormColumn,String> {
    List<MisQueryFormColumn> getMisQueryFormColumnByMisQfId(String qfId);
    void deleteByMisQfId(String qfId);
}
