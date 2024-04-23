package com.asl.prd004.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.IFormInputService;
import com.github.wenhao.jpa.Specifications;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Andy
 * @date 2024/3/18 11:12
 */
@Service
public class FormInputServiceImpl implements IFormInputService {

    @Autowired
    private FormInputRequestDao formInputRequestDao;

    @Autowired
    private FormInputEntryDao formInputEntryDao;

    @Autowired
    private FormInputValueDao formInputValueDao;

    @Autowired
    private MisUserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private IndicatorDao indicatorDao;

    @Autowired
    private IndicatorsTargetDao indicatorsTargetDao;

    @Autowired
    private FormInputRequestPeriodDao formInputRequestPeriodDao;

    @Autowired
    private FormInputRequestIndicatorDao formInputRequestIndicatorDao;

    @Autowired
    private FormInputRemarkDao formInputRemarkDao;

    @Autowired
    SubcategoryDao subcategoryDao;

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Object> getActionList(String lang) {

        List<Object> resultDataList = new ArrayList<>();

        String userOffice = ContextHolder.getOffice();

        List<String> formInputStatusList = getAcceptStatus();

        String userRole = ContextHolder.getUserRole();
        String[] userRoleArray = userRole.split(",");

        List<String> userRoleNameList = Arrays.stream(userRoleArray).collect(Collectors.toList());

        Integer isMOReviewer = 0;
        if (!userRoleNameList.isEmpty()) {
            if (userRoleNameList.contains("MO Reviewer")) {
                isMOReviewer = 1;
            }
        }

        List<FormInputEntryS> actionList = formInputEntryDao.getActionList(userOffice, formInputStatusList, isCMABApprover() ? 1 : 0, isMOReviewer);

        if (actionList != null) {
            for (FormInputEntryS formInputEntryS : actionList) {
                HashMap<String, Object> entryListObj = new HashMap<>();
                FormInputRequestS formInputRequestS = formInputRequestDao.findById(formInputEntryS.getFormInputRequestID()).get();
                entryListObj.put("formInputRequestId", formInputEntryS.getFormInputRequestID());
                entryListObj.put("formInputEntryId", formInputEntryS.getId());
                entryListObj.put("refNum", formInputRequestS.getRefNum());
                entryListObj.put("formInputRequestTitle", formInputRequestS.getFormInputRequestTitle());
                entryListObj.put("molu", formInputEntryS.getMoluCode());
                entryListObj.put("formInputRequestStatus", formInputEntryS.getFormInputStatus());
                entryListObj.put("inputEndDate", formInputRequestS.getInputEndDate());
                List<MisUser> userInfo = userDao.getUserInfoByUserId(formInputEntryS.getUpdateBy());
                if (userInfo.size() > 0)
                    entryListObj.put("updatedUser", userInfo.get(0).getMisUserName());
                else
                    entryListObj.put("updatedUser", "");

                List<CategoryS> categories = categoryDao.findCategoryByCategoryCode(formInputRequestS.getCategoryCode());
                if (categories.size() > 0) {
                    if (lang.equals("TC")) {
                        entryListObj.put("category", categories.get(0).getCategoryNameTc());
                    } else {
                        entryListObj.put("category", categories.get(0).getCategoryNameEn());
                    }
                } else {
                    entryListObj.put("category", "");
                }
                resultDataList.add(entryListObj);
            }
        }
        return resultDataList;
    }

    @Override
    public Object getFormData(String formInputEntryId, String lang) {

        String userOffice = ContextHolder.getOffice();

        FormInputEntryS formInputEntryS;

        if (isCMABApprover() || isMOReviewer()) {
            formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();
        } else {
            formInputEntryS = formInputEntryDao.findByIdAndMoluCode(formInputEntryId, userOffice);
        }

        if (formInputEntryS == null) {
            return false;
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("molu", formInputEntryS.getMoluCode());

        FormInputRequestS formInputRequestS = formInputRequestDao.findById(formInputEntryS.getFormInputRequestID()).get();

        List<FormInputRequestPeriodS> formInputRequestPeriods = formInputRequestPeriodDao.findRequestPeriodByformInputRequestId(formInputRequestS.getId());

        if (formInputRequestPeriods.size() > 0) {
            resMap.put("periodType", formInputRequestPeriods.get(0).getDataPeriodType());
        } else {
            resMap.put("periodType", "");
        }

        List<CategoryS> categories = categoryDao.findCategoryByCategoryCode(formInputRequestS.getCategoryCode());

        if (categories.size() > 0) {
            if (lang.equals("TC")) {
                resMap.put("category", categories.get(0).getCategoryNameTc());
            } else {
                resMap.put("category", categories.get(0).getCategoryNameEn());
            }
        } else {
            resMap.put("category", "");
        }

        List<FormInputValueS> formInputs = formInputValueDao.findByFormInputEntryId(formInputEntryId);

        List<Object> valuesList = new ArrayList<>();

        formInputs.forEach(formInput -> {
            HashMap<String, Object> map = new HashMap<>();
            List<IndicatorsS> indicators = indicatorDao.findIndicatorsByIndCode(formInput.getIndCode());
            map.put("formInputValueId", formInput.getId());
            map.put("categoryCode", resMap.get("category"));
            map.put("Indicator", formInput.getIndCode());
            map.put("year", formInput.getYear());
            map.put("startMonth", formInput.getStartMonth());
            map.put("endMonth", formInput.getEndMonth());
            if (indicators.size() > 0) {
                map.put("subCategoryCode", indicators.get(0).getSubcategoryCode());
                map.put("dataType", indicators.get(0).getDataType());
                if (indicators.get(0).getDataType().equals("Amount")) {
                    map.put("value", formInput.getData_amount());
                } else if (indicators.get(0).getDataType().equals("Count")) {
                    map.put("value", formInput.getDataCount());
                } else {
                    map.put("value", formInput.getDataText());
                }
            } else {
                map.put("dataType", "");
                map.put("value", "");
            }
            valuesList.add(map);
        });

        resMap.put("values", valuesList);

        return resMap;

    }

    @Override
    @Transactional
    public boolean saveFormData(String formInputEntryId, JSONArray datasArray) {
        try {

            FormInputEntryS formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();

            String userId = ContextHolder.getUserId();
            String userRole = ContextHolder.getUserRole();
            String[] userRoleArray = userRole.split(",");

            List<String> userRoleNameList = Arrays.stream(userRoleArray).collect(Collectors.toList());

            if (!userRoleNameList.isEmpty()) {
                if (userRoleNameList.contains("MO/LU Inputter")) {
                    formInputEntryS.setFormInputStatus("D");
                } else if (userRoleNameList.contains("MO/LU Verifier")) {
                    formInputEntryS.setFormInputStatus("V");
                } else if (userRoleNameList.contains("MO Reviewer") || userRoleNameList.contains("CMAB Approver")) {
                    formInputEntryS.setFormInputStatus("unchange");
                }

                formInputEntryDao.save(formInputEntryS);
            }

            if (datasArray.length() > 0) {
                for (int i = 0; i < datasArray.length(); i++) {
                    JSONObject json = datasArray.getJSONObject(i);
                    String formInputValueId = json.getString("formInputValueId");
                    String value = json.getString("value");
                    String dataType = json.getString("dataType");

                    FormInputValueS formInputValueS = formInputValueDao.findById(formInputValueId).get();

                    if (dataType.equals("Amount")) {
//                        if (!value.equals(String.valueOf(formInputValueS.getData_amount()))) {
//                            formInputValueS.setRevised(1);
//                            formInputValueS.setRevisedUser(userId);
//                        }
                        formInputValueS.setData_amount(Double.parseDouble(value));
                    } else if (dataType.equals("Count")) {
//                        if (!value.equals(String.valueOf(formInputValueS.getDataCount()))) {
//                            formInputValueS.setRevised(1);
//                            formInputValueS.setRevisedUser(userId);
//                        }
                        formInputValueS.setDataCount(Integer.parseInt(value));
                    } else {
//                        if (!value.equals(formInputValueS.getDataText())) {
//                            formInputValueS.setRevised(1);
//                            formInputValueS.setRevisedUser(userId);
//                        }
                        formInputValueS.setDataText(value);
                    }
                    formInputValueDao.save(formInputValueS);
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean submitFormData(String formInputEntryId, JSONArray datasArray) {
        try {

            FormInputEntryS formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();
            String userId = ContextHolder.getUserId();

            if (isCMABApprover()) {
                formInputEntryS.setFormInputStatus("E");
            } else if (isMOReviewer()) {
                formInputEntryS.setFormInputStatus("V");
            } else if (formInputEntryS.getWorkflowType() == 1) {
                formInputEntryS.setFormInputStatus("V");
            } else if (formInputEntryS.getWorkflowType() == 2) {
                formInputEntryS.setFormInputStatus("S");
            }

            formInputEntryDao.save(formInputEntryS);

            if (datasArray.length() > 0) {
                for (int i = 0; i < datasArray.length(); i++) {
                    JSONObject json = datasArray.getJSONObject(i);
                    String formInputValueId = json.getString("formInputValueId");
                    String value = json.getString("value");
                    String dataType = json.getString("dataType");

                    FormInputValueS formInputValueS = formInputValueDao.findById(formInputValueId).get();

                    if (dataType.equals("Amount")) {
                        if (formInputValueS.getData_amount() != null && !value.equals(String.valueOf(formInputValueS.getData_amount()))) {
                            formInputValueS.setRevised(1);
                            formInputValueS.setRevisedUser(userId);
                        }
                        formInputValueS.setData_amount(Double.parseDouble(value));
                    } else if (dataType.equals("Count")) {
                        if (formInputValueS.getDataCount() != null && !value.equals(String.valueOf(formInputValueS.getDataCount()))) {
                            formInputValueS.setRevised(1);
                            formInputValueS.setRevisedUser(userId);
                        }
                        formInputValueS.setDataCount(Integer.parseInt(value));
                    } else {
                        if (formInputValueS.getDataText() != null && !value.equals(String.valueOf(formInputValueS.getDataText()))) {
                            formInputValueS.setRevised(1);
                            formInputValueS.setRevisedUser(userId);
                        }
                        formInputValueS.setDataText(value);
                    }
                    formInputValueDao.save(formInputValueS);
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean verifyFormData(String formInputEntryId, JSONArray datasArray) {
        try {
            FormInputEntryS formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();
            formInputEntryS.setFormInputStatus("V");
            formInputEntryDao.save(formInputEntryS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean withdrawFormData(String formInputEntryId, List<String> userRoleNameList) {
        try {
            FormInputEntryS formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();

            if (userRoleNameList.contains("MO/LU Inputter")) {  //MO/LU Inputter

                List<String> acceptStatus = Arrays.asList("S", "V");

                if (!acceptStatus.contains(formInputEntryS.getFormInputStatus())) {
                    return false;
                }
                if (formInputEntryS.getFormInputStatus().equals("S") && formInputEntryS.getWorkflowType() == 1) {
                    return false;
                }
                if (formInputEntryS.getFormInputStatus().equals("V") && formInputEntryS.getWorkflowType() == 2) {
                    return false;
                }
                formInputEntryS.setFormInputStatus("W1");
            } else { //MO/LU Verifier
                if (!formInputEntryS.getFormInputStatus().equals("V")) {
                    return false;
                }
                formInputEntryS.setFormInputStatus("W2");
            }

            formInputEntryDao.save(formInputEntryS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Object getConsolideFormData(String formInputRequestId, String moCode, String lang) {

        List<FormInputEntryS> entrySList = formInputEntryDao.findByFormInputRequestIDAndMoCode(formInputRequestId, moCode);
        if (entrySList.isEmpty()) {
            return false;
        }

        List<FormInputRequestPeriodS> periods = formInputRequestPeriodDao.findRequestPeriodByformInputRequestId(formInputRequestId);
        List<String> periodTypes = periods.stream().map(period -> period.getDataPeriodType()).collect(Collectors.toList());
        String periodType = periodTypes.get(0);

        List<FormInputRequestPeriodDto> periodDtos = formInputRequestPeriodDao.findByformInputRequestId(formInputRequestId);
        List<Integer> periodYearList = periodDtos.stream().map(FormInputRequestPeriodDto::getYear).collect(Collectors.toList());
        List<Integer> periodStartMonthList = periodDtos.stream().map(FormInputRequestPeriodDto::getStartMonth).collect(Collectors.toList());

        List<FormInputRequestindicatorS> requestIndicatorSList = formInputRequestIndicatorDao.findByformInputRequestId(formInputRequestId);
        List<String> indicatorCodeList = requestIndicatorSList.stream().map(FormInputRequestindicatorS::getIndCode).collect(Collectors.toList());

        ArrayList<Integer> historicalMonthList = new ArrayList<>();
        if (!periodType.equals("A")) {
            for (int m = 1; m <= 12; m++) {
                historicalMonthList.add(m);
            }
        } else {
            for (int m = 1; m < Collections.min(periodStartMonthList); m++) {
                historicalMonthList.add(m);
            }
        }


        ArrayList<Object> dataList = new ArrayList<>();

        for (FormInputEntryS formInputEntryS : entrySList) {

            HashMap<String, Double> totalMap = new HashMap<>();

            HashMap<String, Object> entryMap = new HashMap<>();

            String moluCode = formInputEntryS.getMoluCode();

            HashMap<String, Object> moluDataMap = new HashMap<>();

            //获取历史数据
            //get Approval data (通过 Use " v_form_data_actual" to get current year historical data)\
            // form request only allow period with in same year.
            Integer searchHistoryYear = periodType.equals("E") ? periodYearList.get(0) - 1 : periodYearList.get(0);
            List<Map<String, Object>> historicalList = formInputEntryDao.findApprovalDataGroupByMonthIndCode(searchHistoryYear,
                    moCode, moluCode, historicalMonthList, indicatorCodeList);

            //获取current数据
            List<Map<String, Object>> currentList = formInputValueDao.findGroupByIndCodeAndMonth1(formInputEntryS.getId(), periodStartMonthList);

            //声明一个合并list
            ArrayList<Map<String, Object>> newList = new ArrayList<>(historicalList);

            //合并 currentList
            if (periodType.equals("A")) {
                newList.addAll(currentList);
            }

            //累加totalMap
            for (Map<String, Object> c : newList) {
                String indCode = (String) c.get("indicatorCode");
                Double addValue = c.get("value") == null ? 0 : Double.valueOf(c.get("value").toString());
                if (!totalMap.containsKey(indCode)) {
                    totalMap.put(indCode, addValue);
                } else {
                    totalMap.put(indCode, totalMap.get(indCode) + addValue);
                }
            }

            HashMap<String, List<Map<String, Object>>> monthMap = new HashMap<>();
            for (Map<String, Object> newMap : newList) {
                String startMonth = String.valueOf(newMap.get("startMonth"));
                Map<String, Object> nn = new HashMap<>(newMap);
                nn.remove("startMonth");
                if (!monthMap.containsKey(startMonth)) {
                    monthMap.put(startMonth, new ArrayList<>(Arrays.asList(nn)));
                } else {
                    List<Map<String, Object>> mapList = monthMap.get(startMonth);
                    mapList.add(nn);
                    monthMap.put(startMonth, mapList);
                }
            }

            // Estimate / PY / PP
            if (!periodType.equals("A")) {
                String key = "";
                if (periodType.equals("E")) {
                    key = "ESTIMATE";
                } else if (periodType.equals("PY")) {
                    key = "PROJECTYEAR";
                } else {
                    key = "PROJECTPERIOD";
                }
                HashMap<String, List<Map<String, Object>>> estimateMap = new HashMap<>();
                for (Map<String, Object> newMap : currentList) {
                    Map<String, Object> nn = new HashMap<>(newMap);
                    nn.remove("startMonth");

                    if (!estimateMap.containsKey(key)) {
                        estimateMap.put(key, new ArrayList<>(Arrays.asList(nn)));
                    } else {
                        List<Map<String, Object>> mapList = estimateMap.get(key);
                        mapList.add(nn);
                        estimateMap.put(key, mapList);
                    }
                }
                moluDataMap.putAll(estimateMap);
            }

            moluDataMap.putAll(monthMap);

            //组装TOTAL数据
            ArrayList<Map<String, Object>> totalList = new ArrayList<>();
            totalMap.forEach((key, value) -> {
                HashMap<String, Object> e = new HashMap<>();
                e.put("indicatorCode", key);
                e.put("type", "TOTAL");
                e.put("value", value);
                totalList.add(e);
            });
            moluDataMap.put("TOTAL", totalList);

            //包一成数组
            ArrayList<Object> moluData = new ArrayList<>();
            moluData.add(moluDataMap);

            entryMap.put("moluCode", moluCode);
            entryMap.put("data", moluData);

            dataList.add(entryMap);
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("currentYear", periodYearList.get(0));
        resMap.put("formentryid", entrySList.stream().map(FormInputEntryS::getId).collect(Collectors.toList()));
        resMap.put("dataperiodtype", entrySList.get(0).getDataPeriodType());
        resMap.put("inputyear", periodYearList);
        resMap.put("inputmonth", periodStartMonthList);
        resMap.put("indicatorCode", indicatorCodeList);
        resMap.put("entrySList", entrySList);
        resMap.put("data", dataList);

        /*ArrayList<Map<String, Object>> indicatorList = new ArrayList<>();
        indicatorCodeList.forEach((indCode -> {
            List<IndicatorsS> indicatorsByIndCode = indicatorDao.findIndicatorsByIndCode(indCode);
            List<SubcategoryS> subcategory = subcategoryDao.findSubcategoryBySubcategoryCode(indicatorsByIndCode.get(0).getSubcategoryCode());
            indicatorList.add(Map.of(
                    "subcategory", lang.equals("EN") ? subcategory.get(0).getSubcategoryNameEn() : subcategory.get(0).getSubcategoryNameTc(),
                    "indicatorCode", indCode,
                    "indicator", lang.equals("EN") ? indicatorsByIndCode.get(0).getIndNameEn() : indicatorsByIndCode.get(0).getIndNameTc()
            ));
        }));

        resMap.put("indicatorList", indicatorList);*/

        return resMap;
    }

    @Override
    public Map<String, Object> getConsolideFormSummary(String formInputRequestId, String moCode, String lang) {

        List<FormInputEntryS> entrySList = formInputEntryDao.findByFormInputRequestIDAndMoCode(formInputRequestId, moCode);
        if (entrySList.isEmpty()) {
            return null;
        }

        List<FormInputRequestPeriodS> periods = formInputRequestPeriodDao.findRequestPeriodByformInputRequestId(formInputRequestId);
        List<String> periodTypes = periods.stream().map(period -> period.getDataPeriodType()).collect(Collectors.toList());
        String periodType = periodTypes.get(0);

        List<FormInputRequestPeriodDto> periodDtos = formInputRequestPeriodDao.findByformInputRequestId(formInputRequestId);
        List<Integer> periodYearList = periodDtos.stream().map(FormInputRequestPeriodDto::getYear).distinct().collect(Collectors.toList());
        List<Integer> periodStartMonthList = periodDtos.stream().map(FormInputRequestPeriodDto::getStartMonth).collect(Collectors.toList());

        List<FormInputRequestindicatorS> requestIndicatorSList = formInputRequestIndicatorDao.findByformInputRequestId(formInputRequestId);
        List<String> indicatorCodeList = requestIndicatorSList.stream().map(FormInputRequestindicatorS::getIndCode).collect(Collectors.toList());

        ArrayList<Map<String, Object>> dataList = new ArrayList<>();

        // 1. 组装target map
        // 1.1 获取target 数据
        List<String> moluCodeList = entrySList.stream().map(FormInputEntryS::getMoluCode).collect(Collectors.toList());
        Integer year = periodDtos.get(0).getYear();
        List<FormInputTargetDto> indicatorTargetList = indicatorsTargetDao.findByIndCodeAndYearAndMoluCodes(indicatorCodeList, year, moluCodeList);

        // 1.2 组装target map
        HashMap<String, Object> targetMap = new HashMap<>();
        targetMap.put("type", "TARGET");
        targetMap.put("data", indicatorTargetList);

        //1.3 将target map 加入 dataList
        dataList.add(targetMap);

        ArrayList<Object> remarkList = new ArrayList<>();

        //获取 historical Month
        ArrayList<Integer> historicalMonthList = new ArrayList<>();
        for (int m = 1; m < Collections.min(periodStartMonthList); m++) {
            historicalMonthList.add(m);
        }

        // MOTOTAL map
        HashMap<String, Double> MOTotalMap = new HashMap<>();
        for (String indCode : indicatorCodeList) {
            MOTotalMap.put(indCode, 0D);
        }

        //遍历 moluCode 生成 各个 moluCode 的map数据
        for (FormInputEntryS formInputEntryS : entrySList) {

            HashMap<String, Double> totalMap = new HashMap<>();

            String moluCode = formInputEntryS.getMoluCode();

            //获取历史数据
            //get Approval data (通过 Use " v_form_data_actual" to get current year historical data)
            // form request only allow period with in same year.
            Integer searchHistoryYear = periodType.equals("E") ? year - 1 : year;
            List<Map<String, Object>> historicalList = formInputEntryDao.findApprovalDataGroupByMonthIndCode(searchHistoryYear,
                    moCode, moluCode, historicalMonthList, indicatorCodeList);

            //获取current数据
            List<Map<String, Object>> currentList = formInputValueDao.findGroupByIndCodeAndMonth1(formInputEntryS.getId(), periodStartMonthList);

            //声明一个合并list
            ArrayList<Map<String, Object>> newList = new ArrayList<>(historicalList);

            //合并 currentList
            newList.addAll(currentList);

            //累加 totalMap
            for (Map<String, Object> c : newList) {
                String indCode = (String) c.get("indicatorCode");
                Double addValue = c.get("value") == null ? 0 : Double.valueOf(c.get("value").toString());
                if (!totalMap.containsKey(indCode)) {
                    totalMap.put(indCode, addValue);
                } else {
                    totalMap.put(indCode, totalMap.get(indCode) + addValue);
                }
            }

            //2. 组装每个 moluCode 的 total map 元素
            HashMap<String, Object> totalMapByMolu = new HashMap<>();

            //2.1 组装 total map的type和moluCode
            totalMapByMolu.put("type", "TOTAL");
            totalMapByMolu.put("moluCode", moluCode);

            //2.2 组装 total map的 data 数据
            ArrayList<Map<String, Object>> totalMapDataList = new ArrayList<>();
            totalMap.forEach((key, value) -> {
                HashMap<String, Object> e = new HashMap<>();
                e.put("indicatorCode", key);
                e.put("value", value);
                totalMapDataList.add(e);

                //累加MOTotal map
                MOTotalMap.put(key, MOTotalMap.get(key) + value);
            });
            totalMapByMolu.put("data", totalMapDataList);

            // 2.3 将每个moluCode的total map 加入 dataList
            dataList.add(totalMapByMolu);

            for (String indCode : indicatorCodeList) {
                //Item I: Use form_input_entry_id and  ind_code data to find  form_input_remark_s remark data
                FormInputRemarkDto formInputRemarkDto = formInputRemarkDao.findByFormInputEntryIdAndIndCode(formInputEntryS.getId(), indCode);
                if (formInputRemarkDto != null)
                    remarkList.add(formInputRemarkDto);
            }
        }

        //将 indicatorTargetList变形 为 map {"ETO001": 60, "ETO002": 60}
        Map<String, Double> indicatorTargetMap = indicatorTargetList.stream()
                .collect(Collectors.toMap(FormInputTargetDto::getIndicatorCode, FormInputTargetDto::getValue));

        //3. 组装 MOTOTAL 数据
        // 3.1 创建MOTOTAL map
        HashMap<String, Object> moTotalMap = new HashMap<>();
        moTotalMap.put("type", "MOTOTAL");

        //3.2 组装 MOTOTAL map 的 data 数据
        ArrayList<Map<String, Object>> moTotalData = new ArrayList<>();
        ArrayList<Object> varianceData = new ArrayList<>();
        MOTotalMap.forEach((key, value) -> {
            HashMap<String, Object> e = new HashMap<>();
            e.put("indicatorCode", key);
            e.put("value", value);
            moTotalData.add(e);

            //组装 variance map 的data数据
            HashMap<String, Object> varianceDataMap = new HashMap<>();
            varianceDataMap.put("indicatorCode", key);
            Double targetValue = indicatorTargetMap.get(key);
            // Consider the over 0, will let the % ->Infinity
            if (targetValue == null) {
                varianceDataMap.put("value", "0%");
            } else {
                varianceDataMap.put("value", String.format("%.2f", value / ((targetValue != null) ? targetValue : 1) * 100) + "%");
            }
            varianceData.add(varianceDataMap);
        });
        moTotalMap.put("data", moTotalData);

        // 3.2 将 MOTOTAL Map 加入 dataList
        dataList.add(moTotalMap);

        //4. 组装 VARIANCE map数据
        HashMap<String, Object> varianceMap = new HashMap<>();
        varianceMap.put("type", "VARIANCE");
        varianceMap.put("data", varianceData);
        dataList.add(varianceMap);

        //5. 组装 REMARK map数据
        HashMap<String, Object> remarkMap = new HashMap<>();
        remarkMap.put("type", "REMARK");
        remarkMap.put("data", remarkList);
        dataList.add(remarkMap);

        //获取indicator 和 subcategory
        ArrayList<Map<String, Object>> indicatorList = new ArrayList<>();
        indicatorCodeList.forEach((indCode -> {
            List<IndicatorsS> indicatorsByIndCode = indicatorDao.findIndicatorsByIndCode(indCode);
            List<SubcategoryS> subcategory = subcategoryDao.findSubcategoryBySubcategoryCode(indicatorsByIndCode.get(0).getSubcategoryCode());
            indicatorList.add(Map.of(
                    "subcategoryCode", subcategory.get(0).getSubcategoryCode(),
                    "subcategory", lang.equals("EN") ? subcategory.get(0).getSubcategoryNameEn() : subcategory.get(0).getSubcategoryNameTc(),
                    "indicatorCode", indCode,
                    "indicator", lang.equals("EN") ? indicatorsByIndCode.get(0).getIndNameEn() : indicatorsByIndCode.get(0).getIndNameTc()
            ));
        }));

        HashMap<String, Object> resMap = new HashMap<>();

        resMap.put("indicatorList", indicatorList);

        resMap.put("formentryid", entrySList.stream().map(FormInputEntryS::getId).collect(Collectors.toList()));

        resMap.put("dataperiodtype", entrySList.get(0).getDataPeriodType());

        resMap.put("inputyear", periodYearList);

        resMap.put("inputmonth", periodStartMonthList);

        resMap.put("indicatorCode", indicatorCodeList);

        resMap.put("data", dataList);

        return resMap;
    }

    @Override
    public boolean acceptMOFormData(String formInputEntryId) {

        try {

            FormInputEntryS formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();

            if (!formInputEntryS.getFormInputStatus().equals("V") && !formInputEntryS.getFormInputStatus().equals("R3")) {
                return false;
            }

            formInputEntryS.setFormInputStatus("AC");
            formInputEntryDao.save(formInputEntryS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean endorseMOFormData(String formInputRequestId) {

        List<FormInputEntryS> entrySList = formInputEntryDao.findByFormInputRequestIDAndMoCode(formInputRequestId, ContextHolder.getOffice());
        if (entrySList.isEmpty()) {
            return false;
        }

        for (FormInputEntryS inputEntryS : entrySList) {
            if (!inputEntryS.getFormInputStatus().equals("AC")) {
                return false;
            }
        }

        entrySList.forEach(formInputEntryS -> {
            formInputEntryS.setFormInputStatus("E");
            formInputEntryDao.save(formInputEntryS);
        });

        return true;
    }

    @Override
    @Transactional
    public boolean rejectFormData(String formInputEntryId) {

        FormInputEntryS formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();

        if (!formInputEntryS.getFormInputStatus().equals("S")) {
            return false;
        }

        formInputEntryS.setFormInputStatus("R1");

        formInputEntryDao.save(formInputEntryS);

        return true;
    }

    @Override
    public boolean rejectMOFormData(String formInputEntryId) {

        FormInputEntryS formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();

        if (!formInputEntryS.getFormInputStatus().equals("V")) {
            return false;
        }

        if (formInputEntryS.getWorkflowType() == 1) {
            formInputEntryS.setFormInputStatus("R1");
        } else if (formInputEntryS.getWorkflowType() == 2) {
            formInputEntryS.setFormInputStatus("R2");
        }

        formInputEntryDao.save(formInputEntryS);

        return true;
    }

    @Override
    public boolean rejectCMABFormData(String formInputRequestId, String moCode) {

        List<FormInputEntryS> entrySList = formInputEntryDao.findByFormInputRequestIDAndMoCode(formInputRequestId, moCode);
        if (entrySList.isEmpty()) {
            return false;
        }

        for (FormInputEntryS inputEntryS : entrySList) {
            if (!inputEntryS.getFormInputStatus().equals("E")) {
                return false;
            }
        }

        entrySList.forEach(formInputEntryS -> {
            formInputEntryS.setFormInputStatus("R3");
            formInputEntryDao.save(formInputEntryS);
        });
        return true;
    }

    @Override
    public boolean approveCMABFormData(String formInputRequestId, String moCode) {

        List<FormInputEntryS> entrySList = formInputEntryDao.findByFormInputRequestIDAndMoCode(formInputRequestId, moCode);
        for (FormInputEntryS formInputEntryS : entrySList) {
            if (!formInputEntryS.getFormInputStatus().equals("E")) {
                return false;
            }
        }

        for (FormInputEntryS formInputEntryS : entrySList) {
            formInputEntryS.setFormInputStatus("A");
            formInputEntryDao.save(formInputEntryS);
        }

        return true;
    }

    @Override
    public boolean saveMORemark(String formInputRequestId, String moCode, String indicatorCode, String remark) {

        try {
            List<FormInputEntryS> entrySList = formInputEntryDao.findByFormInputRequestIDAndMoCode(formInputRequestId, moCode);

            for (FormInputEntryS formInputEntryS : entrySList) {
                FormInputRemarkS formInputRemarkS = new FormInputRemarkS();
                formInputRemarkS.setFormInputEntryId(formInputEntryS.getId());
                formInputRemarkS.setIndCode(indicatorCode);
                formInputRemarkS.setRemark(remark);
                formInputRemarkDao.save(formInputRemarkS);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * get user role name list
     *
     * @param userId user id
     * @return List
     */
    private List<String> getUserRoleNameList(String userId) {

        List<String> userRoleIdList = userDao.getUserRoles(userId);

        if (!userRoleIdList.isEmpty()) {
            return roleDao.getRoleNameByRoleIds(userRoleIdList);
        }
        return Collections.emptyList();
    }

    /**
     * check if user is CMAB Approver
     *
     * @return Boolean
     */
    private boolean isCMABApprover() {

        String userRole = ContextHolder.getUserRole();

        String[] userRoleArray = userRole.split(",");

        List<String> userRoleNameList = Arrays.stream(userRoleArray).collect(Collectors.toList());

        if (userRoleNameList.isEmpty()) return false;

        return userRoleNameList.contains("CMAB Approver");
    }

    /**
     * check if user is MO Reviewer
     *
     * @return Boolean
     */
    private boolean isMOReviewer() {

        String userRole = ContextHolder.getUserRole();

        String[] userRoleArray = userRole.split(",");

        List<String> userRoleNameList = Arrays.stream(userRoleArray).collect(Collectors.toList());

        if (userRoleNameList.isEmpty()) return false;

        return userRoleNameList.contains("MO Reviewer");
    }

    /**
     * get user's Accept status.
     *
     * @return List
     */
    private List<String> getAcceptStatus() {

        List<String> formInputStatusList = null;

        String userRole = ContextHolder.getUserRole();

        String[] userRoleArray = userRole.split(",");

        List<String> userRoleNameList = Arrays.stream(userRoleArray).collect(Collectors.toList());

        if (!userRoleNameList.isEmpty()) {
            if (userRoleNameList.contains("MO/LU Inputter")) {
                formInputStatusList = Arrays.asList("N", "O", "D", "R1", "W1");
            } else if (userRoleNameList.contains("MO/LU Verifier")) {
                formInputStatusList = Arrays.asList("S", "R2", "W2");
            } else if (userRoleNameList.contains("MO Reviewer")) {
                formInputStatusList = Arrays.asList("V", "R3");
            } else {
                formInputStatusList = Arrays.asList("E");
            }
        }

        return formInputStatusList;
    }

    @Override
    public Map<String, Object> getInputterDownloadTemplate(String formInputEntryId, String lang) {

        FormInputEntryS formInputEntryS = formInputEntryDao.findById(formInputEntryId).get();

        HashMap<String, Object> resMap = new HashMap<>();

        resMap.put("molu", formInputEntryS.getMoluCode());

        FormInputRequestS formInputRequestS = formInputRequestDao.findById(formInputEntryS.getFormInputRequestID()).get();

        List<FormInputRequestPeriodS> formInputRequestPeriods = formInputRequestPeriodDao.findRequestPeriodByformInputRequestId(formInputRequestS.getId());

        if (formInputRequestPeriods.size() > 0) {
            resMap.put("periodType", formInputRequestPeriods.get(0).getDataPeriodType());
        } else {
            resMap.put("periodType", "");
        }

        resMap.put("requestPeriod", formInputRequestPeriods);

        // get subCategory by categoryCode
        List<SubcategoryS> subcategoryByCategoryCode = subcategoryDao.findSubcategoryByCategoryCode(formInputRequestS.getCategoryCode());

        ArrayList<Map<String, Object>> subcategoryArrayList = new ArrayList<>();

        //get indicator by request id and subCategoryCode
        for (SubcategoryS subcategoryS : subcategoryByCategoryCode) {
            List<Map<String, Object>> requestIndicatorList = formInputRequestIndicatorDao.findByFormInputRequestIdAndSubcategoryCode(formInputRequestS.getId(),
                    subcategoryS.getSubcategoryCode(), lang);
            subcategoryArrayList.add(
                    Map.of(
                            "subcategory", "EN".equals(lang) ? subcategoryS.getSubcategoryNameEn() : subcategoryS.getSubcategoryNameTc(),
                            "indicators", requestIndicatorList
                    )
            );
        }

        resMap.put("subcategoryArrayList", subcategoryArrayList);

        return resMap;
    }

    @Override
    public Map<String, Object> getConsolideFormDataExport(String formInputRequestId, String moCode, String lang) {

        FormInputRequestS formInputRequestS = formInputRequestDao.findById(formInputRequestId).get();

        List<FormInputEntryS> entrySList = formInputEntryDao.findByFormInputRequestIDAndMoCode(formInputRequestId, moCode);
        if (entrySList.isEmpty()) {
//            return false;
        }

        List<FormInputRequestPeriodDto> periodDtos = formInputRequestPeriodDao.findByformInputRequestId(formInputRequestId);
        List<Integer> periodYearList = periodDtos.stream().map(FormInputRequestPeriodDto::getYear).collect(Collectors.toList());
        List<Integer> periodStartMonthList = periodDtos.stream().map(FormInputRequestPeriodDto::getStartMonth).collect(Collectors.toList());

        List<FormInputRequestindicatorS> requestIndicatorSList = formInputRequestIndicatorDao.findByformInputRequestId(formInputRequestId);
        List<String> indicatorCodeList = requestIndicatorSList.stream().map(FormInputRequestindicatorS::getIndCode).collect(Collectors.toList());

        ArrayList<Integer> historicalMonthList = new ArrayList<>();
        for (int m = 1; m < Collections.min(periodStartMonthList); m++) {
            historicalMonthList.add(m);
        }


        ArrayList<Map<String, Object>> entryList = new ArrayList<>();

//        HashMap<String, Object> entryMap = new HashMap<>();

        for (FormInputEntryS formInputEntryS : entrySList) {

            HashMap<String, Integer> totalMap = new HashMap<>();

            HashMap<String, Object> entryMap = new HashMap<>();

            String moluCode = formInputEntryS.getMoluCode();

            HashMap<String, Object> moluDataMap = new HashMap<>();

            //获取历史数据
            //get Approval data (通过 Use " v_form_data_actual" to get current year historical data)
            List<Map<String, Object>> historicalList = formInputEntryDao.findApprovalDataGroupByMonthIndCode(DateUtil.thisYear(),
                    moCode, moluCode, historicalMonthList, indicatorCodeList);

            //获取current数据
            List<Map<String, Object>> currentList = formInputValueDao.findGroupByIndCodeAndMonth1(formInputEntryS.getId(), periodStartMonthList);

            //声明一个合并list
            ArrayList<Map<String, Object>> newList = new ArrayList<>(historicalList);

            //合并 currentList
            newList.addAll(currentList);

            //累加totalMap
            for (Map<String, Object> c : newList) {
                String indCode = (String) c.get("indicatorCode");
                Integer addValue = c.get("value") == null ? 0 : Integer.parseInt((String) c.get("value"));
                if (!totalMap.containsKey(indCode)) {
                    totalMap.put(indCode, addValue);
                } else {
                    totalMap.put(indCode, totalMap.get(indCode) + addValue);
                }
            }

            entryList.add(Map.of(
                    "moluCode", moluCode,
                    "totalMap", totalMap,
                    "newList", newList
            ));
        }

        HashMap<String, Object> resMap = new HashMap<>();

        // get subCategory by categoryCode
        List<SubcategoryS> subcategoryByCategoryCode = subcategoryDao.findSubcategoryByCategoryCode(formInputRequestS.getCategoryCode());

        ArrayList<Map<String, Object>> subcategoryArrayList = new ArrayList<>();

        //get indicator by request id and subCategoryCode
        for (SubcategoryS subcategoryS : subcategoryByCategoryCode) {
            List<Map<String, Object>> requestIndicatorList = formInputRequestIndicatorDao.findByFormInputRequestIdAndSubcategoryCode(formInputRequestS.getId(),
                    subcategoryS.getSubcategoryCode(), lang);
            subcategoryArrayList.add(
                    Map.of(
                            "subcategory", "EN".equals(lang) ? subcategoryS.getSubcategoryNameEn() : subcategoryS.getSubcategoryNameTc(),
                            "indicators", requestIndicatorList
                    )
            );
        }

        resMap.put("subcategoryArrayList", subcategoryArrayList);

        resMap.put("currentYear", DateUtil.thisYear());
        resMap.put("entryList", entryList);
        resMap.put("dataperiodtype", entrySList.get(0).getDataPeriodType());
        resMap.put("inputyear", periodYearList);
        resMap.put("inputmonth", periodStartMonthList);
//        resMap.put("indicatorCode", indicatorCodeList);
//        resMap.put("data", dataList);

        return resMap;
    }

    @Override
    public List<Map<String, Object>> getDataExport(String categoryCode, JSONArray subCategoryCodeArray, JSONArray indicatorCodeArray, String dataPeriodType,
                                                   JSONObject exportPeriodStart, JSONObject exportPeriodEnd, JSONArray moluArray, String lang) {

        List<Map<String, Object>> dataExport = null;

        try {
            ArrayList<String> subCategoryCodeList = new ArrayList<>();
            for (int i = 0; i < subCategoryCodeArray.length(); i++) {
                subCategoryCodeList.add(subCategoryCodeArray.getString(i));
            }

            ArrayList<String> indicatorCodeList = new ArrayList<>();
            for (int i = 0; i < indicatorCodeArray.length(); i++) {
                indicatorCodeList.add(indicatorCodeArray.getString(i));
            }

            ArrayList<String> moluList = new ArrayList<>();
            for (int i = 0; i < moluArray.length(); i++) {
                moluList.add(moluArray.getString(i));
            }

            int startYear = exportPeriodStart.getInt("year");
            int startMonth = exportPeriodStart.getInt("month");
            int endYear = exportPeriodEnd.getInt("year");
            int endMonth = exportPeriodEnd.getInt("month");

            dataExport = formInputValueDao.getDataExport(categoryCode, subCategoryCodeList,
                    indicatorCodeList, dataPeriodType, startYear, startMonth, endYear, endMonth, moluList, lang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataExport;
    }

    @Override
    public Object searchFormRecord(String molu, String refNum, String formInputRequestTitle, String categoryCode, String inputStartDate,
                                   String inputEndDate, String status, String lang, JSONObject pageState, JSONObject sort) {

        String moCode = null;

        String userOffice = ContextHolder.getOffice();
        String userRole = ContextHolder.getUserRole();

        String[] userRoleArray = userRole.split(",");

        List<String> userRoleNameList = Arrays.stream(userRoleArray).collect(Collectors.toList());

        if (!userRoleNameList.isEmpty()) {
            if (userRoleNameList.contains("MO/LU Inputter") || userRoleNameList.contains("MO/LU Verifier")) {
                molu = userOffice;
            } else if (userRoleNameList.contains("MO Reviewer")) {
                moCode = userOffice;
            }
        }

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

            inputStartDate = inputStartDate == null || inputStartDate.isEmpty() ? "1970-01-01" : inputStartDate;
            inputEndDate = inputEndDate == null || inputEndDate.isEmpty() ? "2888-12-31" : inputEndDate;

            Page<Map<String, Object>> inputEntryDaoAll = formInputEntryDao.findAll(molu, refNum, formInputRequestTitle, categoryCode,
                    inputStartDate, inputEndDate, status, moCode, lang, pageable);

            pageDataDto = new PageDataDto<>();

            pageDataDto.setData(inputEntryDaoAll.getContent());

            pageDataDto.setTotal(inputEntryDaoAll.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageDataDto;
    }
}
