package com.asl.prd004.service.impl;

import com.asl.prd004.dao.CategoryDao;
import com.asl.prd004.dao.IndicatorsDao;
import com.asl.prd004.dao.SubcategoryDao;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.SubcategoryDto;
import com.asl.prd004.entity.CategoryS;
import com.asl.prd004.entity.IndicatorsS;
import com.asl.prd004.entity.SubcategoryS;
import com.asl.prd004.service.ISubcategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SubcategoryServiceImpl implements ISubcategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private SubcategoryDao subcategoryDao;

    @Autowired
    private IndicatorsDao indicatorsDao;


    @Override
    public PageDataDto getAllSubcategory(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "categoryCode":
                    sortField = "categoryCode";
                    break;
                case "subcategoryCode":
                    sortField = "subcategoryCode";
                    break;
                case "subcategoryNameEn":
                    sortField = "subcategoryNameEn";
                    break;
                case "subcategoryNameTc":
                    sortField = "subcategoryNameTc";
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
        Page<SubcategoryDto> subcategoryDtoPage = subcategoryDao.findAllSubcategory(page);
        List<SubcategoryDto> subcategoryDaoDtoList = subcategoryDtoPage.getContent();
        PageDataDto subcategoryDto = new PageDataDto();
        subcategoryDto.setData(subcategoryDaoDtoList);
        subcategoryDto.setTotal(subcategoryDtoPage.getTotalElements());
        return subcategoryDto;
    }

    @Override
    public List<Object> getSubcategory(String lang, String categoryCode)
    {
        List<Object> resultDataList=new ArrayList<>();
        List<SubcategoryS> subcategoryList=subcategoryDao.findSubcategoryByCategoryCode(categoryCode);
        if(subcategoryList!=null)
        {
            for (SubcategoryS subcategory:subcategoryList) {
                HashMap<String, Object> subcategoryListObj = new HashMap<>();
                subcategoryListObj.put("id", subcategory.getId());
                subcategoryListObj.put("categoryCode", subcategory.getCategoryCode());
                subcategoryListObj.put("subcategoryCode", subcategory.getSubcategoryCode());
                if(lang.equals("TC"))
                {
                    subcategoryListObj.put("subcategoryName", subcategory.getSubcategoryNameTc());
                }
                else
                {
                    subcategoryListObj.put("subcategoryName", subcategory.getSubcategoryNameEn());
                }
                resultDataList.add(subcategoryListObj);
            }
        }
        return resultDataList;
    }

    @Override
    public SubcategoryS getSubcategoryDetail(String id)
    {
        return subcategoryDao.findSubcategoryById(id);
    }

    @Override
    public boolean createSubcategory(String categoryCode,String subcategoryCode,String subcategoryNameEn,String subcategoryNameTc)
    {
        try {
            List<CategoryS> categorySList = categoryDao.findCategoryByCategoryCode(categoryCode);
            if (categorySList != null) {
                List<SubcategoryS> subcategorySList = subcategoryDao.findSubcategoryBySubcategoryCode(subcategoryCode);
                if (subcategorySList == null || subcategorySList.isEmpty()) {
                    SubcategoryS subcategory = new SubcategoryS();
                    subcategory.setCategoryCode(categoryCode);
                    subcategory.setSubcategoryCode(subcategoryCode);
                    subcategory.setSubcategoryNameEn(subcategoryNameEn);
                    subcategory.setSubcategoryNameTc(subcategoryNameTc);
                    subcategoryDao.saveAndFlush(subcategory);
                    return true;
                }
                else {

                    return false;
                }
            }
            else {
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
    public boolean updateSubcategory(String id,String categoryCode,String subcategoryCode,String subcategoryNameEn,String subcategoryNameTc)
    {
        SubcategoryS subcategory = subcategoryDao.findSubcategoryById(id);
        if(subcategory!=null) {
            subcategory.setSubcategoryNameEn(subcategoryNameEn);
            subcategory.setSubcategoryNameTc(subcategoryNameTc);
            subcategoryDao.saveAndFlush(subcategory);
            return true;
        }else{
            return  false;
        }
    }

    @Override
    public boolean delSubcategory(String id)
    {

        SubcategoryS subcategory = subcategoryDao.findSubcategoryById(id);

        try {
            List<IndicatorsS> indicatorList = indicatorsDao.findIndicatorsBySubcategoryCode(subcategory.getSubcategoryCode());
            if(indicatorList.isEmpty()) {
                subcategoryDao.deleteById(id);
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
