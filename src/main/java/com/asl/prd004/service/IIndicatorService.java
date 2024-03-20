package com.asl.prd004.service;

import com.asl.prd004.dto.IndicatorDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.SearchIndicatorDto;
import com.asl.prd004.dto.SearchMoluOfficeDto;
import com.asl.prd004.entity.IndicatorsS;
import com.asl.prd004.entity.MoluOfficeS;

import java.util.List;

public interface IIndicatorService {

    PageDataDto getIndicatorList(SearchIndicatorDto dto);

    IndicatorDto getIndicatorDetail(String id);

    boolean addIndicator(String categoryCode, String subCategoryCode, String indicatorCode, String indicatorNameEn, String indicatorNameTc, String dataType, String currency, Integer active);

    boolean editIndicator(String id, String categoryCode, String subCategoryCode, String indicatorCode, String indicatorNameEn, String indicatorNameTc, String dataType, String currency, Integer active);

    boolean deleteIndicator(String id);

    Object getIndicatorByCategoryCode(String categoryCode, String lang);

    List<IndicatorDto> getIndicatorBySubcategoryCode(String subcategoryCode, String lang);
}
