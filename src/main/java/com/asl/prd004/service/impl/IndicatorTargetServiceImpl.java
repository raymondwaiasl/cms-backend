package com.asl.prd004.service.impl;

import cn.hutool.core.util.StrUtil;
import com.asl.prd004.dao.CategoryDao;
import com.asl.prd004.dao.IndicatorDao;
import com.asl.prd004.dao.IndicatorsTargetDao;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.SearchIndicatorTargetDto;
import com.asl.prd004.entity.IndicatorsS;
import com.asl.prd004.entity.IndicatorsTargetS;
import com.asl.prd004.entity.MoluOfficeS;
import com.asl.prd004.service.IIndicatorTargetService;
import com.asl.prd004.utils.AESUtil;
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

@Service
public class IndicatorTargetServiceImpl implements IIndicatorTargetService {

    @Autowired
    private IndicatorsTargetDao indicatorsTargetDao;

    @Autowired
    private IndicatorDao indicatorDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public PageDataDto getIndicatorTargetList(SearchIndicatorTargetDto dto) {

        Specification<IndicatorsTargetS> specification =
                Specifications.<IndicatorsTargetS>and()
                        .like(StrUtil.isNotBlank(dto.getCategoryCode()), "categoryCode", "%" + dto.getCategoryCode() + "%")
                        .eq(StrUtil.isNotBlank(dto.getYear()), "year", dto.getYear())
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

        Page<IndicatorsTargetS> indicatorPage = indicatorsTargetDao.findAll(specification, page);
        List<IndicatorsTargetS> list = indicatorPage.getContent();
        Session session = entityManager.unwrap(Session.class);
        //jpa默认在实体属性set之后自动提交到数据库，这里不需要提交到库，直接清缓存
        session.clear();

        List<Object> resultDataList = new ArrayList<>();

        if (list != null) {
            for (IndicatorsTargetS indicatorsTargetS : list) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("year", indicatorsTargetS.getYear());
                map.put("categoryCode", indicatorsTargetDao.findCategoryByIndCode(indicatorsTargetS.getIndCode()).getCategoryCode());
                resultDataList.add(map);
            }
        }

        PageDataDto pageDataDto = new PageDataDto(resultDataList, indicatorPage.getTotalElements());

        return pageDataDto;
    }

    @Override
    public IndicatorsTargetS getIndicatorTargetDetail(String id) {
        return indicatorsTargetDao.findById(id).get();
    }

    @Override
    public Boolean checkIndicatorTargetByIndCodeAndYear(String indCode, Integer year) {
        List<IndicatorsTargetS> indicatorTargets = indicatorsTargetDao.findIndicatorTargetsByIndCodeAndYear(indCode, year);
        return indicatorTargets != null && indicatorTargets.size() > 0;
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
    public boolean editIndicatorTarget(String id, String indCode, String moluCode, Integer year, Double target) {
        IndicatorsTargetS indicatorsTargetS = indicatorsTargetDao.findById(id).get();
        if (indicatorsTargetS != null) {
            indicatorsTargetS.setIndCode(indCode);
            indicatorsTargetS.setMoluCode(moluCode);
            indicatorsTargetS.setYear(year);
            indicatorsTargetS.setTarget(target);
            indicatorsTargetDao.saveAndFlush(indicatorsTargetS);
            return true;
        } else {
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

}
