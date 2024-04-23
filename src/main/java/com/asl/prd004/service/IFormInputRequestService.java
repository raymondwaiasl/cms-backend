package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.entity.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IFormInputRequestService {

    PageDataDto<Map<String, Object>> getFormRequestList(String refNum, String formInputRequestTitle, String inputStartDate, String inputEndDate,
                                                        String categoryCode, String dataPeriodType, String lang, JSONObject pageState, JSONObject sort);

    FormInputRequestS getFormRequestDetail(String id);

    Boolean checkHasSamePeriod(List<String> indCodes, List<String> moluList, String dataPeriodType, Integer year,
                               Integer startMonth, Integer endMonth, String id);

    Boolean checkIfAllIndicatorUnderCategoryCode(JSONArray indicatorsArray, String categoryCode);

    boolean addFormRequest(String mode, String formInputRequestTitle, String formInputRequestDesc,
                           String categoryCode, Date inputStartDate, Date inputEndDate, Integer deadlineAlertDay,
                           String formInputRequestStatus, JSONArray indicatorsArray, JSONArray molusArray,
                           String dataPeriodType, JSONArray periodArray);

    boolean editFormRequest(String id, String mode, String formInputRequestTitle, String formInputRequestDesc,
                            String categoryCode, Date inputStartDate, Date inputEndDate, int deadlineAlertDay,
                            String formInputRequestStatus, JSONArray indicatorsArray, JSONArray molusArray,
                            String dataPeriodType, JSONArray periodArray);

    boolean deleteFormRequest(String id);

    boolean checkAllMoluUnderMoluOffice(JSONArray molusArray);

    boolean cancelFormRequest(String id);
}
