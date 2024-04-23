package com.asl.prd004.dao;

import com.asl.prd004.dto.CategoryDto;
import com.asl.prd004.dto.SubcategoryDto;
import com.asl.prd004.entity.SubcategoryS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubcategoryDao extends JpaRepository<SubcategoryS, String> {

    @Query( value = "select new com.asl.prd004.dto.SubcategoryDto(c.id,c.categoryCode,c.subcategoryCode,c.subcategoryNameEn,c.subcategoryNameTc) " +
            "from SubcategoryS c ")
    Page<SubcategoryDto> findAllSubcategory(Pageable pageable);

    SubcategoryS  findSubcategoryById(String id);

    @Query( value = "select * from subcategory_s where if(?1 != '', category_code = ?1, 1=1)", nativeQuery = true)
    List<SubcategoryS>  findSubcategoryByCategoryCode(String categoryCode);

    List<SubcategoryS>  findSubcategoryBySubcategoryCode(String categoryCode);

    SubcategoryS findByCategoryCodeAndSubcategoryCode(String categoryCode, String subcategoryCode);

}

