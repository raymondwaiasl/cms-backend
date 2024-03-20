package com.asl.prd004.dao;

import com.asl.prd004.entity.MisQueryFormCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MisQueryFormConditionDao extends JpaRepository<MisQueryFormCondition,String > {
    List<MisQueryFormCondition> getMisQueryFormConditionByMisQfId(String qfId);

    void deleteByMisQfId(String qfId);
}
