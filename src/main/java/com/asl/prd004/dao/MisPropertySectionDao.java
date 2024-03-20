package com.asl.prd004.dao;
import com.asl.prd004.entity.MisPropertySection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MisPropertySectionDao extends JpaRepository<MisPropertySection,String> {


    List<MisPropertySection> findByMisPropertyId(String misPropertyId);
}
