package com.asl.prd004.service.impl;

import com.asl.prd004.dao.CategoryDao;
import com.asl.prd004.dao.FormInputRequestIndicatorDao;
import com.asl.prd004.dao.IndicatorDao;
import com.asl.prd004.dao.SubcategoryDao;
import com.asl.prd004.dto.IndicatorDetailDto;
import com.asl.prd004.dto.IndicatorDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.entity.CategoryS;
import com.asl.prd004.entity.FormInputRequestindicatorS;
import com.asl.prd004.entity.IndicatorsS;
import com.asl.prd004.entity.SubcategoryS;
import com.asl.prd004.service.IIndicatorService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndicatorServiceImpl implements IIndicatorService {

    @Autowired
    private IndicatorDao indicatorDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private SubcategoryDao subcategoryDao;

    @Autowired
    FormInputRequestIndicatorDao formInputRequestIndicatorDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public PageDataDto<Map<String, Object>> getIndicatorList(String categoryCode, String subCategoryCode, String indicatorCode,
                                                             String indicatorName, Integer active, String lang, JSONObject pageState, JSONObject sort) {

        PageDataDto<Map<String, Object>> indicatorsSPageDataDto = null;
        try {
            int pageNum = pageState.getInt("page") - 1;
            int pageSize = pageState.getInt("pageSize");

            Pageable pageable;

            String sortField = "indicatorId";

            if (!sort.getString("field").isEmpty()) {
                sortField = sort.getString("field");
                if (sortField.equals("categoryCode")) sortField = "ss.category_code";
            }

            if (sort.getString("sort").equalsIgnoreCase("asc")) {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
            } else {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
            }

            Page<Map<String, Object>> indicatorList = indicatorDao.findAll(categoryCode,subCategoryCode, indicatorCode, indicatorName, active, lang, pageable);
            List<Map<String, Object>> contents = indicatorList.getContent();


            indicatorsSPageDataDto = new PageDataDto<>();

            indicatorsSPageDataDto.setData(contents);

            indicatorsSPageDataDto.setTotal(indicatorList.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return indicatorsSPageDataDto;
    }

    @Override
    public IndicatorDetailDto getIndicatorDetail(String id) {
        return indicatorDao.findIndicatorsById(id);
    }

    @Override
    public boolean addIndicator(String categoryCode, String subCategoryCode, String indicatorCode, String indicatorNameEn,
                                String indicatorNameTc, String dataType, String currency, Integer active, String subIndicatorNameEn, String subIndicatorNameTc) {
        try {
            //1. categoryCode exists in category_s
            List<CategoryS> categorySList = categoryDao.findCategoryByCategoryCode(categoryCode);
            if (categorySList == null || categorySList.isEmpty()) {
//                throw new DefinitionException(300, "The categoryCode not exist in category.");
                return false;
            }

            // 2.categoryCode and subcategoryCode exists in subcategory_s
            SubcategoryS subcategory = subcategoryDao.findByCategoryCodeAndSubcategoryCode(categoryCode, subCategoryCode);
            if (subcategory == null) {
                return false;
            }

            // 3. indicatorCode check unique
            List<IndicatorsS> indicatorsByIndCode = indicatorDao.findIndicatorsByIndCode(indicatorCode);
            if (indicatorsByIndCode.size() > 0) {
                return false;
            }

            IndicatorsS indicatorsS = new IndicatorsS();
            indicatorsS.setSubcategoryCode(subCategoryCode);
            indicatorsS.setIndCode(indicatorCode);
            indicatorsS.setIndNameEn(indicatorNameEn);
            indicatorsS.setIndNameTc(indicatorNameTc);
            indicatorsS.setDataType(dataType);
            indicatorsS.setCurrency(currency);
            indicatorsS.setActive(active);
            indicatorsS.setSubIndicatorNameEn(subIndicatorNameEn);
            indicatorsS.setSubIndicatorNameTc(subIndicatorNameTc);
            indicatorDao.saveAndFlush(indicatorsS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean editIndicator(String id, String categoryCode, String subCategoryCode, String indicatorCode, String indicatorNameEn,
                                 String indicatorNameTc, String dataType, String currency, Integer active, String subIndicatorNameEn, String subIndicatorNameTc) {
        //1. categoryCode exists in category_s
        List<CategoryS> categorySList = categoryDao.findCategoryByCategoryCode(categoryCode);
        if (categorySList == null || categorySList.isEmpty()) {
            return false;
        }

        // 2. subcategoryCode exists in subcategory_s
        List<SubcategoryS> subcategoryBySubcategoryCode = subcategoryDao.findSubcategoryBySubcategoryCode(subCategoryCode);
        if (subcategoryBySubcategoryCode == null || subcategoryBySubcategoryCode.isEmpty()) {
            return false;
        }

        IndicatorsS indicatorsS = indicatorDao.findById(id).get();
        if (indicatorsS != null) {
            indicatorsS.setSubcategoryCode(subCategoryCode);
//            indicatorsS.setIndCode(indicatorCode);
            indicatorsS.setIndNameEn(indicatorNameEn);
            indicatorsS.setIndNameTc(indicatorNameTc);
            indicatorsS.setDataType(dataType);
            indicatorsS.setCurrency(currency);
            indicatorsS.setActive(active);
            indicatorsS.setSubIndicatorNameEn(subIndicatorNameEn);
            indicatorsS.setSubIndicatorNameTc(subIndicatorNameTc);
            indicatorDao.saveAndFlush(indicatorsS);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteIndicator(String id) {
        try {
            IndicatorsS indicatorsS = indicatorDao.findById(id).get();
            if (indicatorsS != null) {
                List<FormInputRequestindicatorS> formInputRequestIncidators = formInputRequestIndicatorDao.findFormInputRequestIncidatorByIndCode(indicatorsS.getIndCode());
                if (formInputRequestIncidators.size() > 0) {
                    return false;
                }
                indicatorDao.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    @Override
    public Object getIndicatorByCategoryCode(String categoryCode, String lang) {
        List<CategoryS> categorySList = categoryDao.findCategoryByCategoryCode(categoryCode);
        List<SubcategoryS> subcategoryList = subcategoryDao.findSubcategoryByCategoryCode(categoryCode);

        HashMap<String, Object> resMap = new HashMap<>();
        HashMap<String, Object> categoryMap = new HashMap<>();
        categoryMap.put("categoryCode", categorySList.get(0).getCategoryCode());
        if ("EN".equals(lang)) {
            categoryMap.put("categoryName", categorySList.get(0).getCategoryNameEn());
        } else {
            categoryMap.put("categoryName", categorySList.get(0).getCategoryNameTc());
        }

        ArrayList<Object> subCategoryRes = new ArrayList<>();
        for (SubcategoryS subcategoryS : subcategoryList) {
            subCategoryRes.add(
                    Map.of(
                            "subCategoryCode", subcategoryS.getSubcategoryCode(),
                            "subCategoryName", "EN".equals(lang) ? subcategoryS.getSubcategoryNameEn() : subcategoryS.getSubcategoryNameTc()
                    )
            );
        }

        List<String> subCategoryCodes = subcategoryList.stream()
                .map(SubcategoryS::getSubcategoryCode)
                .collect(Collectors.toList());

        List<Map<String, Object>> indicatorListBySubCategoryCodes = indicatorDao.findBySubCategoryCodes(subCategoryCodes, lang);

        resMap.put("category", categoryMap);
        resMap.put("subCategory", subCategoryRes);
        resMap.put("indicator", indicatorListBySubCategoryCodes);

        return resMap;
    }

    @Override
    public List<IndicatorDto> getIndicatorBySubcategoryCode(String subcategoryCode, String lang) {
        List<IndicatorDto> indicatorDtos = indicatorDao.findIndicatorsBySubcategoryCode(subcategoryCode);
        return indicatorDtos;
    }


}
