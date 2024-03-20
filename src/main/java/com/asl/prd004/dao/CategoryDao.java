package com.asl.prd004.dao;

import com.asl.prd004.dto.CategoryDto;
import com.asl.prd004.entity.CategoryS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryDao extends JpaRepository<CategoryS, String> {

    @Query( value = "select new com.asl.prd004.dto.CategoryDto(c.id,c.categoryCode,c.categoryNameEn,c.categoryNameTc,c.yearType) " +
            "from CategoryS c ")
    List<CategoryDto> findAllCategory();

    @Query( value = "select new com.asl.prd004.dto.CategoryDto(c.id,c.categoryCode,c.categoryNameEn,c.categoryNameTc,c.yearType) " +
            "from CategoryS c ")
    Page<CategoryDto> findAllCategory(Pageable pageable);


    CategoryS findCategoryById(String id);

    List<CategoryS> findCategoryByCategoryCode(String categoryCode);



}