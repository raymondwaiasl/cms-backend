package com.asl.prd004.dao;

import com.asl.prd004.entity.IndicatorsS;
import com.asl.prd004.entity.SubcategoryS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndicatorsDao extends JpaRepository<IndicatorsS, String> {

    List<IndicatorsS> findIndicatorsBySubcategoryCode(String subcategoryCode);

}