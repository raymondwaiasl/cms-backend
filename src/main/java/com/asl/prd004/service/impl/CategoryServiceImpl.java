package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.CategoryDao;
import com.asl.prd004.dao.SubcategoryDao;
import com.asl.prd004.dto.CategoryDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.entity.CategoryS;
import com.asl.prd004.entity.MisDataDictionary;
import com.asl.prd004.entity.MisDataDictionaryValue;
import com.asl.prd004.entity.SubcategoryS;
import com.asl.prd004.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private SubcategoryDao subcategoryDao;


    @Override
    public PageDataDto getAllCategory(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "categoryCode":
                    sortField = "categoryCode";
                    break;
                case "categoryNameEn":
                    sortField = "categoryNameEn";
                    break;
                case "categoryNameTc":
                    sortField = "categoryNameTc";
                    break;
            }
            if (pageable.getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize());
        }
        Page<CategoryDto> categoryDtoPage = categoryDao.findAllCategory(page);
        List<CategoryDto> categoryDaoDtoList = categoryDtoPage.getContent();
        PageDataDto categoryDto = new PageDataDto();
        categoryDto.setData(categoryDaoDtoList);
        categoryDto.setTotal(categoryDtoPage.getTotalElements());
        return categoryDto;
    }

    @Override
    public List<Object> getCategory(String lang)
    {
        List<Object> resultDataList=new ArrayList<>();
        List<CategoryDto> categoryDataList=categoryDao.findAllCategory();
        if(categoryDataList!=null)
        {
            for (CategoryDto categoryDto:categoryDataList) {
                HashMap<String, Object> categoryListObj = new HashMap<>();
                categoryListObj.put("id", categoryDto.getId());
                categoryListObj.put("categoryCode", categoryDto.getCategoryCode());
                if(lang.equals("TC"))
                {
                    categoryListObj.put("categoryName", categoryDto.getCategoryNameTc());
                }
                else
                {
                    categoryListObj.put("categoryName", categoryDto.getCategoryNameEn());
                }
                categoryListObj.put("yearType", categoryDto.getYearType());
                resultDataList.add(categoryListObj);
            }
        }
        return resultDataList;
    }

    @Override
    public CategoryS getCategoryDetail(String id)
    {
        return categoryDao.findCategoryById(id);
    }

    @Override
    public boolean createCategory(String categoryCode,String categoryNameEn,String categoryNameTc,String yearType)
    {
        try {
            List<CategoryS> categorySList = categoryDao.findCategoryByCategoryCode(categoryCode);
            if (categorySList == null || categorySList.isEmpty()) {
                CategoryS category = new CategoryS();
                category.setCategoryCode(categoryCode);
                category.setCategoryNameEn(categoryNameEn);
                category.setCategoryNameTc(categoryNameTc);
                category.setYearType(yearType);
                categoryDao.saveAndFlush(category);
                return true;
            } else {

                return false;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCategory(String id,String categoryCode,String categoryNameEn,String categoryNameTc,String yearType)
    {
        CategoryS category = categoryDao.findCategoryById(id);
        if(category!=null) {
           // category.setCategoryCode(categoryCode);
            category.setCategoryNameEn(categoryNameEn);
            category.setCategoryNameTc(categoryNameTc);
            category.setYearType(yearType);
             categoryDao.saveAndFlush(category);
            return true;
        }else{
            return  false;
        }
    }

    @Override
    public boolean delCategory(String id)
    {

        CategoryS category = categoryDao.findCategoryById(id);

        try {
            List<SubcategoryS> subcategoryList = subcategoryDao.findSubcategoryByCategoryCode(category.getCategoryCode());
            if(subcategoryList.isEmpty()) {
                categoryDao.deleteById(id);
                return  true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }

    }


}
