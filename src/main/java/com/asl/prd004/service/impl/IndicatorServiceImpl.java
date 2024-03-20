package com.asl.prd004.service.impl;

import cn.hutool.core.util.StrUtil;
import com.asl.prd004.config.DefinitionException;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.CategoryDao;
import com.asl.prd004.dao.FormInputRequestIncidatorDao;
import com.asl.prd004.dao.IndicatorDao;
import com.asl.prd004.dao.SubcategoryDao;
import com.asl.prd004.dto.IndicatorDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.SearchIndicatorDto;
import com.asl.prd004.entity.CategoryS;
import com.asl.prd004.entity.FormInputRequestindicatorS;
import com.asl.prd004.entity.IndicatorsS;
import com.asl.prd004.entity.SubcategoryS;
import com.asl.prd004.service.IIndicatorService;
import com.github.wenhao.jpa.Specifications;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    FormInputRequestIncidatorDao formInputRequestIncidatorDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public PageDataDto getIndicatorList(SearchIndicatorDto dto) {

        Specification<IndicatorsS> specification =
                Specifications.<IndicatorsS>and()
                        .like(StrUtil.isNotBlank(dto.getSubCategoryCode()), "subCategoryCode", "%" + dto.getSubCategoryCode() + "%")
                        .like(StrUtil.isNotBlank(dto.getIndCode()), "indCode", "%" + dto.getIndCode() + "%")
                        .eq(StrUtil.isNotBlank(dto.getActive()), "active", "true".equalsIgnoreCase(dto.getActive()) ? 1 : 0)
                        .build();

        Pageable page;
        if (StringUtils.isNotEmpty(dto.getSortModel().getField())) {
            String sortField = dto.getSortModel().getField();
            switch (sortField) {
                case "indicatorCode":
                    sortField = "indicatorCode";
                    break;
            }
            if (dto.getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(dto.getPageState().getPage() - 1, dto.getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(dto.getPageState().getPage() - 1, dto.getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(dto.getPageState().getPage() - 1, dto.getPageState().getPageSize());
        }

        Page<IndicatorsS> indicatorPage = indicatorDao.findAll(specification, page);
        List<IndicatorsS> list = indicatorPage.getContent();
        Session session = entityManager.unwrap(Session.class);
        //jpa默认在实体属性set之后自动提交到数据库，这里不需要提交到库，直接清缓存
        session.clear();

        PageDataDto pageDataDto = new PageDataDto(list, indicatorPage.getTotalElements());

        return pageDataDto;
    }

    @Override
    public IndicatorDto getIndicatorDetail(String id) {
        IndicatorDto indicatorDto = indicatorDao.findIndicatorsById(id);
        return indicatorDto;
    }

    @Override
    public boolean addIndicator(String categoryCode, String subCategoryCode, String indicatorCode, String indicatorNameEn,
                                String indicatorNameTc, String dataType, String currency, Integer active) {
        try {
            //1. categoryCode exists in category_s
            List<CategoryS> categorySList = categoryDao.findCategoryByCategoryCode(categoryCode);
            if (categorySList == null || categorySList.isEmpty()) {
//                throw new DefinitionException(300, "The categoryCode not exist in category.");
                return false;
            }

            // 2. subcategoryCode exists in subcategory_s
            List<SubcategoryS> subcategoryBySubcategoryCode = subcategoryDao.findSubcategoryBySubcategoryCode(subCategoryCode);
            if (subcategoryBySubcategoryCode == null || subcategoryBySubcategoryCode.isEmpty()) {
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
            indicatorsS.setActive(active);
            indicatorDao.saveAndFlush(indicatorsS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean editIndicator(String id, String categoryCode, String subCategoryCode, String indicatorCode, String indicatorNameEn,
                                 String indicatorNameTc, String dataType, String currency, Integer active) {
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
            indicatorsS.setActive(active);
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
                List<FormInputRequestindicatorS> formInputRequestIncidators = formInputRequestIncidatorDao.findFormInputRequestIncidatorByIndCode(indicatorsS.getIndCode());
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

        List<String> subCategoryCodes = subcategoryList.stream()
                .map(SubcategoryS::getSubcategoryCode)
                .collect(Collectors.toList());

        List<IndicatorDto> indicatorDtos = indicatorDao.findIndicatorsSubCategoryCode(subCategoryCodes);

        resMap.put("category", categoryMap);
        resMap.put("subCategory", subcategoryList);
        resMap.put("indicator", indicatorDtos);


        return resMap;
    }

    @Override
    public List<IndicatorDto> getIndicatorBySubcategoryCode(String subcategoryCode, String lang) {
        List<IndicatorDto> indicatorDtos = indicatorDao.findIndicatorsBySubcategoryCode(subcategoryCode);
        return indicatorDtos;
    }


}
