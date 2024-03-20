package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.SearchIndicatorTargetDto;
import com.asl.prd004.entity.IndicatorsTargetS;

import java.util.List;

public interface IIndicatorTargetService {

    Object getIndicatorTargetList(SearchIndicatorTargetDto data);

    IndicatorsTargetS getIndicatorTargetDetail(String id);

    Boolean checkIndicatorTargetByIndCodeAndYear(String indCode, Integer year);

    boolean addIndicatorTarget(String indCode, String moluCode, Integer yearInt, Double target);

    boolean bitchAddIndicatorTarget(List<IndicatorsTargetS> indicatorsTargetSList);

    boolean editIndicatorTarget(String id, String indCode, String moluCode, Integer yearInt, Double target);

    boolean deleteIndicator(String id);
}
