package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import org.json.JSONObject;

/**
 * @author andyli
 * @date 2024/3/18 11:07
 */
public interface IConsolidateDataService {

    PageDataDto searchConsolidateDataList(String categoryCode, Integer yearStart, Integer yearEnd, String lang, JSONObject pageState, JSONObject sort);

    Object getMOFormDataByMonth(String categoryCode, Integer year);

    Object getMOFormDataSummary(String categoryCode, Integer year);

    Object getMOFormDataTotal(String categoryCode, Integer year);
}
