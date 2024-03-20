package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.entity.SubcategoryS;

import java.util.List;

public interface ISubcategoryService {


    List<Object> getSubcategory(String lang, String categoryCode);
    PageDataDto getAllSubcategory(PageableDto pageable);

    SubcategoryS getSubcategoryDetail(String id);

    boolean createSubcategory(String categoryCode,String subcategoryCode,String subcategoryNameEn,String subcategoryNameTc);

    boolean updateSubcategory(String id,String categoryCode,String subcategoryCode,String subcategoryNameEn,String subcategoryNameTc);

    boolean delSubcategory(String id);


}
