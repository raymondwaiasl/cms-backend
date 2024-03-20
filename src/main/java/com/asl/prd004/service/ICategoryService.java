package com.asl.prd004.service;

import com.asl.prd004.dto.CategoryDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.entity.CategoryS;

import java.util.List;

public interface ICategoryService {


    List<Object> getCategory(String lang);
    PageDataDto getAllCategory(PageableDto pageable);

    CategoryS getCategoryDetail(String id);

    boolean createCategory(String categoryCode,String categoryNameEn,String categoryNameTc,String yearType);

    boolean updateCategory(String id,String categoryCode,String categoryNameEn,String categoryNameTc,String yearType);

    boolean delCategory(String id);


}
