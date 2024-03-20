package com.asl.prd004.service.impl;

import cn.hutool.core.util.StrUtil;
import com.asl.prd004.dao.MoluOfficeDao;
import com.asl.prd004.dto.MoluOfficeDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.SearchMoluOfficeDto;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.entity.MoluOfficeS;
import com.asl.prd004.service.IMoluService;
import com.asl.prd004.utils.AESUtil;
import com.asl.prd004.utils.SerialNumberUtils;
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
public class MoluServiceImpl implements IMoluService {

    @Autowired
    private MoluOfficeDao moluOfficeDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public PageDataDto getMoluList(SearchMoluOfficeDto dto) {

        Specification<MoluOfficeS> specification =
                Specifications.<MoluOfficeS>and()
                        .like(StrUtil.isNotBlank(dto.getMoluCode()), "moluCode", "%" + dto.getMoluCode() + "%")
                        .like(StrUtil.isNotBlank(dto.getMoCode()), "moCode", "%" + dto.getMoCode() + "%")
                        .like(StrUtil.isNotBlank(dto.getMoluType()), "moluType", "%" + dto.getMoluType() + "%")
                        .eq(StrUtil.isNotBlank(dto.getActive()), "active", "true".equalsIgnoreCase(dto.getActive()) ? 1 : 0)
                        .build();

        Pageable page;
        if (StringUtils.isNotEmpty(dto.getSortModel().getField())) {
            String sortField = dto.getSortModel().getField();
            switch (sortField) {
                case "moluCode":
                    sortField = "moluCode";
                    break;
                case "moCode":
                    sortField = "moCode";
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

        /*Page<MoluOfficeDto> moluDtoPage = moluOfficeDao.getMoluList(page);

        List<MoluOfficeDto> moluOfficeDtoList = moluDtoPage.getContent();

        PageDataDto moluDto = new PageDataDto();

        moluDto.setData(moluOfficeDtoList);

        moluDto.setTotal(moluDtoPage.getTotalElements());

        return moluDto;*/

        Page<MoluOfficeS> moluPage = moluOfficeDao.findAll(specification, page);
        List<MoluOfficeS> moluList = moluPage.getContent();
        Session session = entityManager.unwrap(Session.class);
        //jpa默认在实体属性set之后自动提交到数据库，这里不需要提交到库，直接清缓存
        session.clear();

        PageDataDto userDto = new PageDataDto(moluList, moluPage.getTotalElements());

        return userDto;
    }

    @Override
    public MoluOfficeS getMoluDetail(String id) {
        MoluOfficeS moluOfficeS = moluOfficeDao.findMoluOfficeById(id);
        return moluOfficeS;
    }

    @Override
    public List<Object> getMoluByType(String moluType, String lang) {

        List<Object> resultDataList = new ArrayList<>();

        List<MoluOfficeS> moluList = moluOfficeDao.findByMoluType(moluType);

        if (moluList != null) {
            for (MoluOfficeS moluOfficeS : moluList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("moluCode", moluOfficeS.getMoluCode());
                map.put("moluType", moluOfficeS.getMoluType());
                if (lang.equals("TC")) {
                    map.put("moluName", moluOfficeS.getMoluNameTc());
                } else {
                    map.put("moluName", moluOfficeS.getMoluNameEn());
                }
                resultDataList.add(map);
            }
        }
        return resultDataList;
    }

    @Override
    public List<Object> getMoluByMOCode(String molu, String lang) {

        List<Object> resultDataList = new ArrayList<>();

        List<MoluOfficeS> moluList = moluOfficeDao.findByMoCode(molu);

        if (moluList != null) {
            for (MoluOfficeS moluOfficeS : moluList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("moluCode", moluOfficeS.getMoluCode());
                map.put("moluType", moluOfficeS.getMoluType());
                if (lang.equals("TC")) {
                    map.put("moluName", moluOfficeS.getMoluNameTc());
                } else {
                    map.put("moluName", moluOfficeS.getMoluNameEn());
                }
                resultDataList.add(map);
            }
        }
        return resultDataList;
    }

    @Override
    public boolean addMolu(String moluCode, String moCode, String moluType,
                           String moluNameEn, String moluNameTc, int activeIntValue) {
        try {
            MoluOfficeS moluOfficeS = new MoluOfficeS();
            moluOfficeS.setId(SerialNumberUtils.getTableSequence("molu_office_s"));
            moluOfficeS.setMoluCode(moluCode);
            moluOfficeS.setMoCode(moCode);
            moluOfficeS.setMoluType(moluType);
            moluOfficeS.setMoluNameEn(moluNameEn);
            moluOfficeS.setMoluNameTc(moluNameTc);
            moluOfficeS.setActive(activeIntValue);
            moluOfficeDao.saveAndFlush(moluOfficeS);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean editMolu(String id, String moluCode, String moCode, String moluType,
                            String moluNameEn, String moluNameTc, int activeIntValue) {
        MoluOfficeS moluOfficeS = moluOfficeDao.findById(id).get();
        if (moluOfficeS != null) {
            moluOfficeS.setMoluCode(moluCode);
            moluOfficeS.setMoCode(moCode);
            moluOfficeS.setMoluType(moluType);
            moluOfficeS.setMoluNameEn(moluNameEn);
            moluOfficeS.setMoluNameTc(moluNameTc);
            moluOfficeS.setActive(activeIntValue);
            moluOfficeDao.saveAndFlush(moluOfficeS);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteMolu(String id) {
        try {
            moluOfficeDao.deleteById(id);
            return true;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }
    }


}
