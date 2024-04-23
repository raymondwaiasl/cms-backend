package com.asl.prd004.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.SearchFormInputRequestDto;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.IFormInputRequestService;
import com.asl.prd004.utils.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/forminputrequest")
public class FormInputRequestController {

    @Autowired
    IFormInputRequestService formInputRequestService;

    @Log("Get all Form Input request list.")
    @RequestMapping(value = "/getFormRequestList", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getFormRequestList(@RequestBody String data) throws Exception {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("参数为空!");
        }

        JSONObject json = new JSONObject(data);

        String refNum = json.getString("refNum");
        String formInputRequestTitle = json.getString("formInputRequestTitle");
        String categoryCode = json.getString("categoryCode");
        String inputStartDate = json.getString("inputStartDate");
        String inputEndDate = json.getString("inputEndDate");
        String dataPeriodType = json.getString("dataPeriodType");
        String lang = json.getString("lang");

        JSONObject pageState = json.getJSONObject("pageState");

        JSONObject sort = json.getJSONObject("sortModel");

        return ResultGenerator.getSuccessResult(formInputRequestService.getFormRequestList(refNum, formInputRequestTitle, inputStartDate,
                inputEndDate, categoryCode, dataPeriodType, lang, pageState, sort));
    }

    @Log("Get Form Input request details.")
    @RequestMapping(value = "/getFormRequestDetail", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getFormRequestDetail(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();

        FormInputRequestS formRequestDetail = formInputRequestService.getFormRequestDetail(id);

        return ResultGenerator.getSuccessResult(formRequestDetail);
    }

    @Log("Add Form Input request.")
    @RequestMapping(value = "/addFormRequest", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator addFormRequest(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String mode = json.getString("mode").isEmpty() ? "" : json.getString("mode").trim();
        String formInputRequestTitle = json.getString("formInputRequestTitle").isEmpty() ? "" : json.getString("formInputRequestTitle").trim();
        String formInputRequestDesc = json.getString("form_input_request_description").isEmpty() ? "" : json.getString("form_input_request_description").trim();
        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        String inputStartDate = json.getString("inputStartDate").isEmpty() ? "" : json.getString("inputStartDate").trim();
        String inputEndDate = json.getString("inputEndDate").isEmpty() ? "" : json.getString("inputEndDate").trim();
        String deadlineAlertDay = json.getString("deadlineAlertDay").isEmpty() ? "" : json.getString("deadlineAlertDay").trim();
        String dataPeriodType = json.getString("dataPeriodType").isEmpty() ? "" : json.getString("dataPeriodType").trim();
        int deadlineAlertDayInt = Integer.parseInt(deadlineAlertDay);

        String indicators = json.getString("indicators").isEmpty() ? "" : json.getString("indicators").trim();
        String molus = json.getString("molus").isEmpty() ? "" : json.getString("molus").trim();
        String period = json.getString("period").isEmpty() ? "" : json.getString("period").trim();

        JSONArray indicatorsArray = new JSONArray(indicators);
        JSONArray molusArray = new JSONArray(molus);
        JSONArray periodArray = new JSONArray(period);

        DateTime dateTimeStart = DateUtil.parse(inputStartDate);
        DateTime dateTimeEnd = DateUtil.parse(inputEndDate);
        String today = DateUtil.today();
        DateTime dateTimeToday = DateUtil.parse(today);

        //common Validation rules
        ResultGenerator<String> failResult = checkValidationRules(dataPeriodType, indicatorsArray, periodArray,
                molusArray, dateTimeStart, dateTimeEnd, dateTimeToday, categoryCode, null);
        if (failResult != null) return failResult;

        String formInputRequestStatus;
        if ("save".equals(mode)) {
            formInputRequestStatus = "D";
        } else if ("submit".equals(mode)) {
            formInputRequestStatus = "NS";
        } else {
            formInputRequestStatus = null;
        }

        if (formInputRequestService.addFormRequest(mode, formInputRequestTitle, formInputRequestDesc, categoryCode,
                dateTimeStart, dateTimeEnd, deadlineAlertDayInt, formInputRequestStatus, indicatorsArray, molusArray, dataPeriodType, periodArray)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    private ResultGenerator<String> checkValidationRules(String dataPeriodType, JSONArray indicatorsArray, JSONArray periodArray,
                                                         JSONArray molusArray, DateTime dateTimeStart, DateTime dateTimeEnd,
                                                         DateTime dateTimeToday, String categoryCode, String id) throws JSONException {

        // 2.When addFormRequest or editFormRequest: current date < start data
        if (dateTimeStart.isBeforeOrEquals(dateTimeToday)) {
            return ResultGenerator.getFailResult("inputStartDate must after current date\n!");
        }

        // check input_start_date <= input_end_date
        if (dateTimeEnd.isBefore(dateTimeStart)) {
            return ResultGenerator.getFailResult("input end Date must after equals input start date\n!");
        }

        //check all indicator need under categoryCode
        if (indicatorsArray.length() > 0 && !formInputRequestService.checkIfAllIndicatorUnderCategoryCode(indicatorsArray, categoryCode)) {
            return ResultGenerator.getFailResult("all indicator need under categoryCode\n!");
        }

        //check all molu under molu_office_s.molu_code and active =1
        if (molusArray.length() > 0 && !formInputRequestService.checkAllMoluUnderMoluOffice(molusArray)) {
            return ResultGenerator.getFailResult("all molu need under molu_office_s.molu_code\n!");
        }

        //4.If data period is Projection for a period, start_month =1
        if ("PP".equals(dataPeriodType) && periodArray.length() > 0) {
            for (int i = 0; i < periodArray.length(); i++) {
                JSONObject periodJson = periodArray.getJSONObject(i);
                int startMonth = periodJson.getInt("startMonth");
                if (startMonth != 1) {
                    return ResultGenerator.getFailResult("If data period is Projection for a period, start_month must be 1\n!");
                }
            }
        }

        //5.When addFormRequest or  editFormRequest, Same “Data Period”,  start month, end month and indicator need unique
        if (indicatorsArray.length() > 0 && periodArray.length() > 0 && molusArray.length() > 0) {
            List<String> indicatorsList = new ArrayList<>();
            for (int i = 0; i < indicatorsArray.length(); i++) {
                String indCode = indicatorsArray.getString(i);
                indicatorsList.add(indCode);
            }
            List<String> moluList = new ArrayList<>();
            for (int i = 0; i < molusArray.length(); i++) {
                moluList.add(molusArray.getString(i));
            }
            for (int i = 0; i < periodArray.length(); i++) {
                JSONObject jsonPeriod = periodArray.getJSONObject(i);
                Integer year = jsonPeriod.getInt("year");
                Integer startMonth = jsonPeriod.getInt("startMonth");
                Integer endMonth = jsonPeriod.getInt("endMonth");
                if (formInputRequestService.checkHasSamePeriod(indicatorsList, moluList, dataPeriodType, year, startMonth, endMonth, id)) {
                    return ResultGenerator.getFailResult("Same “Data Period” has exist\n");
                }
            }
        }
        return null;
    }

    @Log("Update Form Input request.")
    @RequestMapping(value = "/editFormRequest", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator editFormRequest(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();
        String mode = json.getString("mode").isEmpty() ? "" : json.getString("mode").trim();
        String formInputRequestTitle = json.getString("formInputRequestTitle").isEmpty() ? "" : json.getString("formInputRequestTitle").trim();
        String formInputRequestDesc = json.getString("form_input_request_description").isEmpty() ? "" : json.getString("form_input_request_description").trim();
        String categoryCode = json.getString("categoryCode").isEmpty() ? "" : json.getString("categoryCode").trim();
        String inputStartDate = json.getString("inputStartDate").isEmpty() ? "" : json.getString("inputStartDate").trim();
        String inputEndDate = json.getString("inputEndDate").isEmpty() ? "" : json.getString("inputEndDate").trim();
        String deadlineAlertDay = json.getString("deadlineAlertDay").isEmpty() ? "" : json.getString("deadlineAlertDay").trim();
        String dataPeriodType = json.getString("dataPeriodType").isEmpty() ? "" : json.getString("dataPeriodType").trim();
        int deadlineAlertDayInt = Integer.parseInt(deadlineAlertDay);

        String indicators = json.getString("indicators").isEmpty() ? "" : json.getString("indicators").trim();
        String molus = json.getString("molus").isEmpty() ? "" : json.getString("molus").trim();
        String period = json.getString("period").isEmpty() ? "" : json.getString("period").trim();

        JSONArray indicatorsArray = new JSONArray(indicators);
        JSONArray molusArray = new JSONArray(molus);
        JSONArray periodArray = new JSONArray(period);

        DateTime dateTimeStart = DateUtil.parse(inputStartDate);
        DateTime dateTimeEnd = DateUtil.parse(inputEndDate);
        String today = DateUtil.today();
        DateTime dateTimeToday = DateUtil.parse(today);

        // common Validation rules
        ResultGenerator<String> failResult = checkValidationRules(dataPeriodType, indicatorsArray, periodArray, molusArray,
                dateTimeStart, dateTimeEnd, dateTimeToday, categoryCode, id);
        if (failResult != null) return failResult;

        FormInputRequestS formRequestDetail = formInputRequestService.getFormRequestDetail(id);

        // Allow modify when form_input_request_status='D' or form_input_request_status='NS'
        if (!formRequestDetail.getFormInputRequestStatus().equals("D") && !formRequestDetail.getFormInputRequestStatus().equals("NS")) {
            return ResultGenerator.getFailResult("current status cannot edit\n!");
        }

        String formInputRequestStatus;
        if ("save".equals(mode)) {
            formInputRequestStatus = "D";
        } else if ("submit".equals(mode)) {
            formInputRequestStatus = "NS";
        } else {
            formInputRequestStatus = null;
        }

        if (formInputRequestService.editFormRequest(id, mode, formInputRequestTitle, formInputRequestDesc, categoryCode,
                dateTimeStart, dateTimeEnd, deadlineAlertDayInt, formInputRequestStatus, indicatorsArray, molusArray, dataPeriodType, periodArray)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }

    }

    @Log("Delete Form Input request item.")
    @RequestMapping(value = "/deleteFormRequest", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteFormRequest(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();

        FormInputRequestS formRequestDetail = formInputRequestService.getFormRequestDetail(id);

        /*Date inputStartDate = formRequestDetail.getInputStartDate();

        DateTime inputStartDateTime = DateUtil.date(inputStartDate);

        DateTime currentDateTime = DateUtil.date();*/

        //can be deleted when status =D or NS
        if (formRequestDetail.getFormInputRequestStatus().equals("D") || formRequestDetail.getFormInputRequestStatus().equals("NS")) {
            if (formInputRequestService.deleteFormRequest(id)) {
                return ResultGenerator.getSuccessResult("success");
            } else {
                return ResultGenerator.getFailResult("failed");
            }
        } else {
            return ResultGenerator.getFailResult("Can be deleted only when current date < start date and status =D or NS\n!");
        }
    }

    @Log("cancel Form Input request.")
    @RequestMapping(value = "/cancelFormRequest", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator cancelFormRequest(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }
        JSONObject json = new JSONObject(data);
        String id = json.getString("id").isEmpty() ? "" : json.getString("id").trim();

        FormInputRequestS formRequestDetail = formInputRequestService.getFormRequestDetail(id);

        if (!formRequestDetail.getFormInputRequestStatus().equals("NS")) {
            return ResultGenerator.getFailResult("Only allow when status is NS");
        }

        DateTime dateTimeStart = DateUtil.parse(formRequestDetail.getInputStartDate().toString());
        String today = DateUtil.today();
        DateTime dateTimeToday = DateUtil.parse(today);

        if (dateTimeToday.isAfterOrEquals(dateTimeStart)) {
            return ResultGenerator.getFailResult("Can not cancel when currentDate after input start date.");
        }

        if (formInputRequestService.cancelFormRequest(id)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

}
