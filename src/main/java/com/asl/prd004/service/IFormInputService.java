package com.asl.prd004.service;

import com.asl.prd004.dto.SearchFormInputDto;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author Owen
 * @date 2024/3/18 11:07
 */
public interface IFormInputService {

    List<Object> getActionList(String lang);

    Object getFormData(String formInputEntryId, String lang);

    boolean saveFormData(String formInputEntryId, JSONArray datasArray);

    boolean submitFormData(String formInputEntryId, JSONArray datasArray);

    boolean verifyFormData(String formInputEntryId, JSONArray datasArray);

    boolean withdrawFormData(String formInputEntryId, List<String> userRoleNameList);

    Object getConsolideFormData(String formInputRequestId, String moCode, String lang);

    Map<String, Object> getConsolideFormSummary(String formInputRequestId, String moCode, String lang);

    boolean acceptMOFormData(String formInputEntryId);

    boolean endorseMOFormData(String formInputRequestId);

    boolean rejectFormData(String formInputEntryId);

    boolean rejectMOFormData(String formInputEntryId);

    boolean rejectCMABFormData(String formInputRequestId, String moCode);

    boolean approveCMABFormData(String formInputRequestId, String moCode);

    boolean saveMORemark(String formInputRequestId, String moCode, String indicatorCode, String remark);

    Map<String, Object> getInputterDownloadTemplate(String formInputEntryId, String lang);

    Map<String, Object> getConsolideFormDataExport(String formInputRequestId, String moCode, String lang);

    List<Map<String, Object>> getDataExport(String categoryCode, JSONArray subCategoryCodeArray, JSONArray indicatorCodeArray, String dataPeriodType,
                                                      JSONObject exportPeriodStart, JSONObject exportPeriodEnd, JSONArray moluArray, String lang);

    Object searchFormRecord(String molu, String refNum, String formInputRequestTitle, String categoryCode, String inputStartDate,
                            String inputEndDate, String status, String lang, JSONObject pageState, JSONObject sort);
}
