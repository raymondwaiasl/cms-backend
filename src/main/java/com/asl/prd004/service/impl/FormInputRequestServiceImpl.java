package com.asl.prd004.service.impl;

import cn.hutool.core.date.DateUtil;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.IFormInputRequestService;
import com.asl.prd004.utils.SerialNumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author AndyLi
 * @date 2024/3/11 11:13
 */
@Service
public class FormInputRequestServiceImpl implements IFormInputRequestService {

    @Autowired
    private FormInputRequestDao formInputRequestDao;

    @Autowired
    private FormInputRequestIndicatorDao formInputRequestIndicatorDao;

    @Autowired
    private FormInputRequestOfficeDao formInputRequestOfficeDao;

    @Autowired
    private FormInputRequestPeriodDao formInputRequestPeriodDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private SubcategoryDao subcategoryDao;

    @Autowired
    private IndicatorDao indicatorDao;

    @Autowired
    private MoluOfficeDao moluOfficeDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public PageDataDto<Map<String, Object>> getFormRequestList(String refNum, String formInputRequestTitle, String inputStartDate, String inputEndDate,
                                                               String categoryCode, String dataPeriodType, String lang, JSONObject pageState, JSONObject sort) {

        PageDataDto<Map<String, Object>> pageDataDto = null;

        try {
            int pageNum = pageState.getInt("page") - 1;
            int pageSize = pageState.getInt("pageSize");

            Pageable pageable;

            String sortField = "id";

            if (!sort.getString("field").isEmpty()) {
                sortField = sort.getString("field");
                if (sortField.equals("categoryCode")) sortField = "ss.category_code";
            }

            if (sort.getString("sort").equalsIgnoreCase("asc")) {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
            } else {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
            }
            inputStartDate  = inputStartDate.isEmpty() ? "1970-01-01" : inputStartDate;
            inputEndDate  = inputEndDate.isEmpty() ? "2888-12-31" : inputEndDate;
            Page<Map<String, Object>> inputRequestPage = formInputRequestDao.findAll(formInputRequestTitle, inputStartDate, inputEndDate, categoryCode,
                     dataPeriodType, lang, refNum, pageable);

            pageDataDto = new PageDataDto<>();

            pageDataDto.setData(inputRequestPage.getContent());

            pageDataDto.setTotal(inputRequestPage.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageDataDto;
    }

    @Override
    public FormInputRequestS getFormRequestDetail(String id) {

        FormInputRequestS formInputRequestS = formInputRequestDao.findById(id).get();

        List<String> indicators = formInputRequestIndicatorDao.findByformInputRequestId(id).stream()
                .map(FormInputRequestindicatorS::getIndCode)
                .collect(Collectors.toList());

        List<String> molus = formInputRequestOfficeDao.findByformInputRequestId(id).stream()
                .map(FormInputRequestOfficeS::getMoluCode)
                .collect(Collectors.toList());

        List<FormInputRequestPeriodDto> periodDtoList = formInputRequestPeriodDao.findByformInputRequestId(id);

        List<FormInputRequestPeriodS> periodSList = formInputRequestPeriodDao.findRequestPeriodByformInputRequestId(id);

        formInputRequestS.setIndicators(indicators);
        formInputRequestS.setMolus(molus);
        formInputRequestS.setPeriod(periodDtoList);
        formInputRequestS.setDataPeriodType(periodSList.get(0).getDataPeriodType());

        return formInputRequestS;
    }

    @Override
    public Boolean checkHasSamePeriod(List<String> indCodes, List<String> moluList, String dataPeriodType, Integer year,
                                      Integer startMonth, Integer endMonth, String id) {
        List<FormInputRequestindicatorS> indicatorsBySamePeriod = formInputRequestIndicatorDao.findIndicatorsBySamePeriod(indCodes, moluList, dataPeriodType, year, startMonth, endMonth, id);
        return indicatorsBySamePeriod.size() > 0;
    }

    @Override
    public Boolean checkIfAllIndicatorUnderCategoryCode(JSONArray indicatorsArray, String categoryCode) {

        try {
            List<SubcategoryS> subcategorys = subcategoryDao.findSubcategoryByCategoryCode(categoryCode);

            List<String> subCategoryCodeList = subcategorys.stream().map(SubcategoryS::getSubcategoryCode).collect(Collectors.toList());

            List<IndicatorDto> indicatorDtoList = indicatorDao.findIndicatorsSubCategoryCode(subCategoryCodeList);

            List<String> indCodeList = indicatorDtoList.stream().map(IndicatorDto::getIndCode).collect(Collectors.toList());

            for (int i = 0; i < indicatorsArray.length(); i++) {
                String indCode = indicatorsArray.getString(i);
                if (!indCodeList.contains(indCode)) {
                    return false;
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean checkAllMoluUnderMoluOffice(JSONArray molusArray) {
        try {
            for (int i = 0; i < molusArray.length(); i++) {
                String moluCode = molusArray.getString(i);
                List<MoluOfficeS> byMoluCode = moluOfficeDao.findByMoluCodeAndActive(moluCode, 1);
                return !byMoluCode.isEmpty();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean addFormRequest(String mode, String formInputRequestTitle, String formInputRequestDesc,
                                  String categoryCode, Date inputStartDate, Date inputEndDate, Integer deadlineAlertDay,
                                  String formInputRequestStatus, JSONArray indicatorsArray, JSONArray molusArray,
                                  String dataPeriodType, JSONArray periodArray) {

        try {

            FormInputRequestS formInputRequestS = new FormInputRequestS();
            formInputRequestS.setRefNum(getAutoRefNum(categoryCode, DateUtil.thisYear(), dataPeriodType));
            formInputRequestS.setFormInputRequestTitle(formInputRequestTitle);
            formInputRequestS.setFormInputRequestDescription(formInputRequestDesc);
            formInputRequestS.setCategoryCode(categoryCode);
            formInputRequestS.setInputStartDate(inputStartDate);
            formInputRequestS.setInputEndDate(inputEndDate);
            formInputRequestS.setDeadlineAlertDay(deadlineAlertDay);
            formInputRequestS.setFormInputRequestStatus(formInputRequestStatus);
            formInputRequestDao.save(formInputRequestS);

            saveIndicatorMolusPeriod(indicatorsArray, molusArray, dataPeriodType, periodArray, formInputRequestS);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //
    private void saveIndicatorMolusPeriod(JSONArray indicatorsArray, JSONArray molusArray, String dataPeriodType,
                                          JSONArray periodArray, FormInputRequestS formInputRequestS) throws JSONException {
        //保存indicators
        if (indicatorsArray.length() > 0) {
            for (int i = 0; i < indicatorsArray.length(); i++) {
                String indCode = indicatorsArray.getString(i);
                FormInputRequestindicatorS formInputRequestindicatorS = new FormInputRequestindicatorS();
                formInputRequestindicatorS.setId(SerialNumberUtils.getTableSequence("form_input_request_indicator_s"));
                formInputRequestindicatorS.setIndCode(indCode);
                formInputRequestindicatorS.setFormInputRequestId(formInputRequestS.getId());
                formInputRequestIndicatorDao.save(formInputRequestindicatorS);
            }
        }

        //保存molus
        if (molusArray.length() > 0) {
            for (int i = 0; i < molusArray.length(); i++) {
                String molusCode = molusArray.getString(i);
                FormInputRequestOfficeS formInputRequestOfficeS = new FormInputRequestOfficeS();
                formInputRequestOfficeS.setMoluCode(molusCode);
                formInputRequestOfficeS.setFormInputRequestId(formInputRequestS.getId());
                formInputRequestOfficeDao.save(formInputRequestOfficeS);
            }
        }

        //保存period
        if (periodArray.length() > 0) {
            for (int i = 0; i < periodArray.length(); i++) {
                JSONObject json = periodArray.getJSONObject(i);
                Integer year = json.getInt("year");
                Integer startMonth = json.getInt("startMonth");
                Integer endMonth = json.getInt("endMonth");

                FormInputRequestPeriodS formInputRequestPeriodS = new FormInputRequestPeriodS();
                formInputRequestPeriodS.setYear(year);
                formInputRequestPeriodS.setStartMonth(startMonth);
                formInputRequestPeriodS.setEndMonth(endMonth);
                formInputRequestPeriodS.setDataPeriodType(dataPeriodType);
                formInputRequestPeriodS.setFormInputRequestId(formInputRequestS.getId());
                formInputRequestPeriodDao.save(formInputRequestPeriodS);
            }
        }
    }

    @Override
    @Transactional
    public boolean editFormRequest(String id, String mode, String formInputRequestTitle, String formInputRequestDesc,
                                   String categoryCode, Date inputStartDate, Date inputEndDate, int deadlineAlertDay,
                                   String formInputRequestStatus, JSONArray indicatorsArray, JSONArray molusArray,
                                   String dataPeriodType, JSONArray periodArray) {

        try {
            FormInputRequestS formInputRequestS = formInputRequestDao.findById(id).get();

            formInputRequestS.setFormInputRequestTitle(formInputRequestTitle);
            formInputRequestS.setFormInputRequestDescription(formInputRequestDesc);
            formInputRequestS.setCategoryCode(categoryCode);
            formInputRequestS.setInputStartDate(inputStartDate);
            formInputRequestS.setInputEndDate(inputEndDate);
            formInputRequestS.setDeadlineAlertDay(deadlineAlertDay);
            formInputRequestS.setFormInputRequestStatus(formInputRequestStatus);
            formInputRequestDao.save(formInputRequestS);

            //删除相关数据
            formInputRequestIndicatorDao.deleteByFormInputRequestId(id);
            formInputRequestOfficeDao.deleteByFormInputRequestId(id);
            formInputRequestPeriodDao.deleteByFormInputRequestId(id);

            //保存indicators、molus和period数据
            saveIndicatorMolusPeriod(indicatorsArray, molusArray, dataPeriodType, periodArray, formInputRequestS);

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteFormRequest(String id) {
        try {
            formInputRequestDao.deleteById(id);
            formInputRequestIndicatorDao.deleteByFormInputRequestId(id);
            formInputRequestOfficeDao.deleteByFormInputRequestId(id);
            formInputRequestPeriodDao.deleteByFormInputRequestId(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean cancelFormRequest(String id) {
        try {
            FormInputRequestS formInputRequestS = formInputRequestDao.findById(id).get();
            formInputRequestS.setFormInputRequestStatus("CX");
            formInputRequestDao.save(formInputRequestS);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized String getAutoRefNum(String categoryCode, Integer year, String type) {

        String lastRefNum = formInputRequestDao.findLastRefNum(categoryCode, year, type);

        if (lastRefNum != null) {

            String[] split = lastRefNum.split("-");

            int i = Integer.parseInt(split[3]);

            i++;

            split[3] = String.format("%03d", i);

            return String.join("-", split);
        } else {
            StringBuilder sb = new StringBuilder();
            StringBuilder refNum = sb.append(categoryCode)
                    .append("-")
                    .append(year)
                    .append("-")
                    .append(type)
                    .append("-")
                    .append("001");
            return refNum.toString();
        }
    }
}
