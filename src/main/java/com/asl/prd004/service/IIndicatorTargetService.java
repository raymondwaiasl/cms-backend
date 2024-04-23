package com.asl.prd004.service;

import com.asl.prd004.dto.IndicatorTargetDetailDto;
import com.asl.prd004.dto.SearchIndicatorTargetDto;
import com.asl.prd004.entity.IndicatorsTargetS;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface IIndicatorTargetService {

    List<IndicatorTargetDetailDto> getIndicatorTargetDetail(String categoryCode, Integer year);

    Boolean checkIndicatorTargetByIndCodeAndYear(String indCode, Integer year, String moluCode);

    boolean addIndicatorTarget(String indCode, String moluCode, Integer yearInt, Double target);

    boolean bitchAddIndicatorTarget(List<IndicatorsTargetS> indicatorsTargetSList);

    boolean editIndicatorTarget(JSONArray jsonArray);

    boolean deleteIndicator(String id);

    Object getIndicatorTargetList(String categoryCode, String year, String lang, JSONObject pageState, JSONObject sort);
}
