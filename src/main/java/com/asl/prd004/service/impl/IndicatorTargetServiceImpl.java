package com.asl.prd004.service.impl;

import cn.hutool.core.util.StrUtil;
import com.asl.prd004.dao.CategoryDao;
import com.asl.prd004.dao.IndicatorDao;
import com.asl.prd004.dao.IndicatorsTargetDao;
import com.asl.prd004.dao.SubcategoryDao;
import com.asl.prd004.dto.IndicatorDto;
import com.asl.prd004.dto.IndicatorTargetDetailDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.SearchIndicatorTargetDto;
import com.asl.prd004.entity.IndicatorsS;
import com.asl.prd004.entity.IndicatorsTargetS;
import com.asl.prd004.entity.SubcategoryS;
import com.asl.prd004.service.IIndicatorTargetService;
import com.github.wenhao.jpa.Specifications;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndicatorTargetServiceImpl implements IIndicatorTargetService {

    @Autowired
    private IndicatorsTargetDao indicatorsTargetDao;

    @Autowired
    private IndicatorDao indicatorDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private SubcategoryDao subcategoryDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public List<IndicatorTargetDetailDto> getIndicatorTargetDetail(String categoryCode, Integer year) {
        List<SubcategoryS> subcategorys = subcategoryDao.findSubcategoryByCategoryCode(categoryCode);
        List<String> subCategoryCodeList = subcategorys.stream().map(SubcategoryS::getSubcategoryCode).collect(Collectors.toList());
        List<IndicatorDto> indicatorDtoList = indicatorDao.findIndicatorsSubCategoryCode(subCategoryCodeList);

        List<String> indCodeList = new ArrayList<>();
        for (IndicatorDto indicatorDto : indicatorDtoList) {
            indCodeList.add(indicatorDto.getIndCode());
        }

        List<IndicatorTargetDetailDto> indicatorsTargetSList = indicatorsTargetDao.findAllByIndCodeSAndYear(indCodeList, year);

        return indicatorsTargetSList;
    }

    @Override
    public Boolean checkIndicatorTargetByIndCodeAndYear(String indCode, Integer year, String moluCode) {
        IndicatorsTargetS indicatorsTargetS = indicatorsTargetDao.findIndicatorTargetsByIndCodeAndYearAndMoluCode(indCode, year, moluCode);
        return indicatorsTargetS != null;
    }

    @Override
    public boolean addIndicatorTarget(String indCode, String moluCode, Integer year, Double target) {
        try {
            IndicatorsTargetS indicatorsTargetS = new IndicatorsTargetS();
            indicatorsTargetS.setIndCode(indCode);
            indicatorsTargetS.setMoluCode(moluCode);
            indicatorsTargetS.setYear(year);
            indicatorsTargetS.setTarget(target);
            indicatorsTargetDao.saveAndFlush(indicatorsTargetS);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean bitchAddIndicatorTarget(List<IndicatorsTargetS> indicatorsTargetSList) {
        try {
            indicatorsTargetDao.saveAllAndFlush(indicatorsTargetSList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean editIndicatorTarget(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String indCode = json.getString("indCode").isEmpty() ? "" : json.getString("indCode").trim();
                String moluCode = json.getString("moluCode").isEmpty() ? "" : json.getString("moluCode").trim();
                Integer year = json.getInt("year");
                double target = json.getDouble("target");

                IndicatorsTargetS indicatorTarget = indicatorsTargetDao.findIndicatorTargetsByIndCodeAndYearAndMoluCode(indCode, year, moluCode);

                if (indicatorTarget != null) {
                    indicatorTarget.setTarget(target);
                    indicatorsTargetDao.save(indicatorTarget);
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteIndicator(String id) {
        try {
            indicatorsTargetDao.deleteById(id);
            return true;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    @Override
    public Object getIndicatorTargetList(String categoryCode, String year, String lang, JSONObject pageState, JSONObject sort) {

        PageDataDto<Map<String, Object>> pageDataDto = null;
        try {
            int pageNum = pageState.getInt("page") - 1;
            int pageSize = pageState.getInt("pageSize");

            Pageable pageable;

            String sortField = "t.category_code";

            if (!sort.getString("field").isEmpty()) {
                sortField = sort.getString("field");
                if (sortField.equals("categoryCode")) sortField = "t.category_code";
            }

            if (sort.getString("sort").equalsIgnoreCase("asc")) {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
            } else {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
            }

            Page<Map<String, Object>> pageList = indicatorsTargetDao.findAll(categoryCode, year, lang, pageable);

            pageDataDto = new PageDataDto<>();

            pageDataDto.setData(pageList.getContent());

            pageDataDto.setTotal(pageList.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageDataDto;
    }

}
