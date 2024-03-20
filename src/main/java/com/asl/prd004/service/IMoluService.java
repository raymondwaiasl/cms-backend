package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.SearchMoluOfficeDto;
import com.asl.prd004.entity.MoluOfficeS;

import java.util.List;

public interface IMoluService {

    PageDataDto getMoluList(SearchMoluOfficeDto dto);

    MoluOfficeS getMoluDetail(String id);

    List<Object> getMoluByType(String moluType,String lang);

    List<Object> getMoluByMOCode(String molu, String lang);

    boolean addMolu(String moluCode, String moCode, String moluType, String moluNameEn, String moluNameTc, int activeIntValue);

    boolean editMolu(String id, String moluCode, String moCode, String moluType, String moluNameEn, String moluNameTc, int activeIntValue);

    boolean deleteMolu(String id);

}
