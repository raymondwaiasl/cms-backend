package com.asl.prd004.service;

import com.asl.prd004.dto.*;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface IIndicatorService {

    PageDataDto<Map<String, Object>> getIndicatorList(String categoryCode, String subCategoryCode, String indicatorCode,
                                                      String indicatorName, Integer active, String lang, JSONObject pageState, JSONObject sort);

    IndicatorDetailDto getIndicatorDetail(String id);

    boolean addIndicator(String categoryCode, String subCategoryCode, String indicatorCode, String indicatorNameEn,
                         String indicatorNameTc, String dataType, String currency, Integer active, String subIndicatorNameEn, String subIndicatorNameTc);

    boolean editIndicator(String id, String categoryCode, String subCategoryCode, String indicatorCode, String indicatorNameEn,
                          String indicatorNameTc, String dataType, String currency, Integer active, String subIndicatorNameEn, String subIndicatorNameTc);

    boolean deleteIndicator(String id);

    Object getIndicatorByCategoryCode(String categoryCode, String lang);

    List<IndicatorDto> getIndicatorBySubcategoryCode(String subcategoryCode, String lang);
}
