package com.asl.prd004.service.impl;

import cn.hutool.core.date.DateUtil;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.FormInputEntryS;
import com.asl.prd004.entity.FormInputRequestPeriodS;
import com.asl.prd004.entity.MoluOfficeS;
import com.asl.prd004.entity.SubcategoryS;
import com.asl.prd004.service.IConsolidateDataService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Andyli
 * @date 2024/3/28 11:20
 */
@Service
public class ConsolidateDataServiceImpl implements IConsolidateDataService {

    @Autowired
    private FormInputRequestDao formInputRequestDao;

    @Autowired
    private MoluOfficeDao moluOfficeDao;

    @Autowired
    private SubcategoryDao subcategoryDao;

    @Autowired
    private IndicatorDao indicatorDao;

    @Autowired
    private IndicatorsTargetDao indicatorsTargetDao;

    @Autowired
    private FormInputRequestPeriodDao formInputRequestPeriodDao;

    @Autowired
    private FormInputValueDao formInputValueDao;

    @Autowired
    private FormInputEntryDao formInputEntryDao;

    @Autowired
    private EntityManager entityManager;

    @Override
    public PageDataDto searchConsolidateDataList(String categoryCode, Integer yearStart, Integer yearEnd, String lang, JSONObject pageState, JSONObject sort) {

        PageDataDto<Map<String, Object>> consolidateDataListDto = null;
        try {
            int pageNum = pageState.getInt("page") - 1;
            int pageSize = pageState.getInt("pageSize");

            Pageable pageable = PageRequest.of(pageNum, pageSize);

            String sortField = "b.year";

            if (!sort.getString("field").isEmpty()) {
                sortField = sort.getString("field");
            }

            if (sort.getString("sort").equalsIgnoreCase("asc")) {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
            } else {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
            }

            Page<Map<String, Object>> consolidateDataList = formInputRequestDao.searchConsolidateDataList(yearStart, yearEnd, categoryCode, lang, pageable);

            consolidateDataListDto = new PageDataDto();

            consolidateDataListDto.setData(consolidateDataList.getContent());

            consolidateDataListDto.setTotal(consolidateDataList.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return consolidateDataListDto;
    }

    @Override
    public Object getMOFormDataByMonth(String categoryCode, Integer year) {

        List<String> allActiveMoCode = moluOfficeDao.findAllActiveMoCode();

        List<SubcategoryS> subcategorySList = subcategoryDao.findSubcategoryByCategoryCode(categoryCode);
        List<String> subcategoryCodeList = subcategorySList.stream().map(SubcategoryS::getSubcategoryCode).collect(Collectors.toList());
        List<IndicatorDto> indicatorDtos = indicatorDao.findIndicatorsSubCategoryCode(subcategoryCodeList);
        List<String> indCodeList = indicatorDtos.stream().map(IndicatorDto::getIndCode).collect(Collectors.toList());

        List<FormInputRequestPeriodS> requestPeriodSList = formInputRequestPeriodDao.findByYearAndDataPeriodType(year, "A");
        List<Integer> periodStartMonthList = requestPeriodSList.stream().map(FormInputRequestPeriodS::getStartMonth).distinct().collect(Collectors.toList());

        ArrayList<Integer> historicalMonthList = new ArrayList<>();
        for (int m = 1; m < Collections.min(periodStartMonthList); m++) {
            historicalMonthList.add(m);
        }

        ArrayList<Object> dataList = new ArrayList<>();

        for (String moCode : allActiveMoCode) {

            HashMap<String, Double> totalMap = new HashMap<>();

            HashMap<String, Object> moMap = new HashMap<>();

            moMap.put("moluCode", moCode);

            HashMap<String, List<Map<String, Object>>> moDataMap = new HashMap<>();

            //获取历史数据
            List<Map<String, Object>> historicalList = formInputEntryDao.findApprovalDataGroupByMonthIndCode(year, moCode, null, historicalMonthList, indCodeList);

            //获取current数据
            List<Map<String, Object>> currentList = formInputValueDao.findGroupByIndCodeAndMonth(year, "A", moCode, periodStartMonthList);

            //声明一个合并list
            ArrayList<Map<String, Object>> newList = new ArrayList<>(historicalList);

            //合并 currentList
            newList.addAll(currentList);

            //累加totalMap
            for (Map c : newList) {
                String indCode = (String) c.get("indicatorCode");
                Double addValue = c.get("value") == null ? Double.valueOf(0) : (Double) c.get("value");
                if (!totalMap.containsKey(indCode)) {
                    totalMap.put(indCode, addValue);
                } else {
                    totalMap.put(indCode, totalMap.get(indCode) + addValue);
                }
            }

            for(Map<String, Object> newMap : newList) {
                String startMonth = String.valueOf(newMap.get("startMonth"));
                Map<String, Object> nn = new HashMap<>(newMap);
                nn.remove("startMonth");
//                moDataMap.put(startMonth, nn);
                if (!moDataMap.containsKey(startMonth)) {
                    moDataMap.put(startMonth, new ArrayList<>(Arrays.asList(nn)));
                } else {
                    List<Map<String, Object>> mapList = moDataMap.get(startMonth);
                    mapList.add(nn);
                    moDataMap.put(startMonth, mapList);
                }
            }

            //组装 TARGET 数据
            List<MoluOfficeS> moluOfficeSByMoCode = moluOfficeDao.findByMoCode(moCode);
            List<String> moluCodeList = moluOfficeSByMoCode.stream().map(MoluOfficeS::getMoluCode).collect(Collectors.toList());
            Integer moluCodeBoolean = moluCodeList.size() > 0 ? 1 : 0;
            List<Map<String, Object>> targetGroupByIndCode = indicatorsTargetDao.findTargetGroupByIndCode(year, indCodeList, moluCodeBoolean, moluCodeList);
            moDataMap.put("TARGET", targetGroupByIndCode);

            //组装TOTAL数据
            ArrayList<Map<String, Object>> totalList = new ArrayList<>();
            totalMap.forEach((key, value) -> {
                HashMap<String, Object> e = new HashMap<>();
                e.put("indicatorCode", key);
                e.put("type", "TOTAL");
                e.put("value", value);
                totalList.add(e);
            });
            moDataMap.put("TOTAL", totalList);

            moMap.put("data", moDataMap);

            dataList.add(moMap);
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("currentYear", year);
        resMap.put("formentryid", "");
        resMap.put("dataperiodtype", "");
        resMap.put("inputyear", Collections.singletonList(year));
        resMap.put("inputmonth", periodStartMonthList);
        resMap.put("indicatorCode", indCodeList);
        resMap.put("data", dataList);

        return resMap;
    }

    @Override
    public Object getMOFormDataSummary(String categoryCode, Integer year) {

        List<String> allActiveMoCode = moluOfficeDao.findAllActiveMoCode();

        List<SubcategoryS> subcategorySList = subcategoryDao.findSubcategoryByCategoryCode(categoryCode);
        List<String> subcategoryCodeList = subcategorySList.stream().map(SubcategoryS::getSubcategoryCode).collect(Collectors.toList());
        List<IndicatorDto> indicatorDtos = indicatorDao.findIndicatorsSubCategoryCode(subcategoryCodeList);
        List<String> indCodeList = indicatorDtos.stream().map(IndicatorDto::getIndCode).collect(Collectors.toList());

        ArrayList<Object> dataList = new ArrayList<>();

        for (String moCode : allActiveMoCode) {

            HashMap<String, Object> moMap = new HashMap<>();

            moMap.put("moluCode", moCode);

            HashMap<String, Object> moDataMap = new HashMap<>();

            //组装 TARGET 数据
            List<MoluOfficeS> moluOfficeSByMoCode = moluOfficeDao.findByMoCode(moCode);
            List<String> moluCodeList = moluOfficeSByMoCode.stream().map(MoluOfficeS::getMoluCode).collect(Collectors.toList());
            Integer moluCodeBoolean = moluCodeList.size() > 0 ? 1 : 0;
            List<Map<String, Object>> targetGroupByIndCode = indicatorsTargetDao.findTargetGroupByIndCode(year, indCodeList, moluCodeBoolean, moluCodeList);
            moDataMap.put("TARGET", targetGroupByIndCode);

            //组装 ACTUAL 数据
            List<Map<String, Object>> actualList = formInputValueDao.findGroupByIndCodeByFormInputEntryId(year, "A", moCode);
            moDataMap.put("ACTUAL", actualList);

            //将 target 数组变为下面Map形式
            //{ "ETO003" : 1, "ETO004" : 2} //key=indCode, value = value
            HashMap<String, Double> targetValueMap = new HashMap<>();
            for (Map map : targetGroupByIndCode) {
                Double value = map.get("value") == null ? 0 : (Double) map.get("value");
                targetValueMap.put((String) map.get("indicatorCode"), value);
            }

            //ACTUAL_VS_TARGET
            ArrayList<Object> actualVsTargetList = new ArrayList<>();
            for (Map map : actualList) {
                Map<String, Object> map1 = new HashMap<>(map);
                map1.put("type", "ACTUAL_VS_TARGET");
                String indCode = (String) map1.get("indicatorCode");
                Double value = map1.get("value") == null ? 0 : (Double) map1.get("value");
                Double targetValue = targetValueMap.containsKey(indCode) ? targetValueMap.get(indCode) : 0;
                map1.put("value", value / targetValue * 100 + "%");
                actualVsTargetList.add(map1);
            }
            moDataMap.put("ACTUAL_VS_TARGET", actualVsTargetList);

            //BENCHMARK
            moDataMap.put("BENCHMARK", formInputEntryDao.findApprovalLastMonth(year, moCode));

            //PROJECTPERIOD
            List<Map<String, Object>> pyList = formInputValueDao.findGroupByIndCodeByFormInputEntryId(year, "PY", moCode);
            moDataMap.put("PROJECTPERIOD", pyList);

            //PROJECT_VS_TARGET PROJECTPERIOD / TARGET(2024) *100%
            ArrayList<Object> PROJECT_VS_TARGET_list = new ArrayList<>();
            for (Map map : pyList) {
                String indCode = (String) map.get("indicatorCode");
                Double value = map.get("value") == null ? 0 : (Double) map.get("value");
                HashMap<String, Object> m = new HashMap<>();
                m.put("indicatorCode", indCode);
                m.put("type", "PROJECT_VS_TARGET");
                Double targetValue = targetValueMap.containsKey(indCode) ? targetValueMap.get(indCode) : 0;
                m.put("value", value / targetValue * 100 + "%");
                PROJECT_VS_TARGET_list.add(m);
            }
            moDataMap.put("PROJECT_VS_TARGET", PROJECT_VS_TARGET_list);

            //ESTIMATE
            List<Map<String, Object>> estimateList = formInputValueDao.findGroupByIndCodeByFormInputEntryId(year, "E", moCode);
            moDataMap.put("ESTIMATE", estimateList);

            //ESTIMATE_VS_ACTUAL => ESTIMATE / SUM(last year actual)
            //last year actual -> currentYear:2024, lastYear:currentYear-1
            List<Map<String, Object>> lastActualYearlList = formInputValueDao.findGroupByIndCodeByFormInputEntryId(year - 1, "A", moCode);
            HashMap<String, Double> lastActualValueMap = new HashMap<>();
            for (Map map : lastActualYearlList) {
                Double value = map.get("value") == null ? 0 : (Double) map.get("value");
                lastActualValueMap.put((String) map.get("indicatorCode"), value);
            }
            ArrayList<Object> ESTIMATE_VS_ACTUAL_List = new ArrayList<>();
            for (Map map : estimateList) {
                String indCode = (String) map.get("indicatorCode");
                Double estimateValue = map.get("value") == null ? 0 : (Double) map.get("value");
                HashMap<String, Object> m = new HashMap<>();
                m.put("indicatorCode", indCode);
                m.put("type", "ESTIMATE_VS_ACTUAL");
                Double lastActual = lastActualValueMap.containsKey(indCode) ? lastActualValueMap.get(indCode) : 0;
                m.put("value", estimateValue / lastActual);
                ESTIMATE_VS_ACTUAL_List.add(m);
            }
            moDataMap.put("ESTIMATE_VS_ACTUAL", ESTIMATE_VS_ACTUAL_List);

            //ESTIMATE_VS_TARGET => ESTIMATE/ Target(currentYear + 1)
            List<Map<String, Object>> nextTargetGroupByIndCode = indicatorsTargetDao.findTargetGroupByIndCode(year + 1, indCodeList, moluCodeBoolean, moluCodeList);//
            HashMap<String, Double> nextTargetValueMap = new HashMap<>();
            for (Map map : nextTargetGroupByIndCode) {
                Double value = map.get("value") == null ? 0 : (Double) map.get("value");
                nextTargetValueMap.put((String) map.get("indicatorCode"), value);
            }
            ArrayList<Object> ESTIMATE_VS_TARGET_list = new ArrayList<>();
            for (Map map : estimateList) {
                String indCode = (String) map.get("indicatorCode");
                Double estimateValue = map.get("value") == null ? 0 : (Double) map.get("value");
                HashMap<String, Object> m = new HashMap<>();
                m.put("indicatorCode", indCode);
                m.put("type", "ESTIMATE_VS_TARGET");
                Double nextTargetValue = nextTargetValueMap.containsKey(indCode) ? nextTargetValueMap.get(indCode) : 0;
                m.put("value", estimateValue / nextTargetValue);
                ESTIMATE_VS_TARGET_list.add(m);
            }
            moDataMap.put("ESTIMATE_VS_TARGET", ESTIMATE_VS_TARGET_list);

            moMap.put("data", moDataMap);

            dataList.add(moMap);
        }

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("currentYear", year);
        resMap.put("formentryid", "");
        resMap.put("dataperiodtype", "");
        resMap.put("inputyear", Arrays.asList(year, year + 1));
        resMap.put("inputmonth", "");
        resMap.put("indicatorCode", indCodeList);
        resMap.put("data", dataList);

        return resMap;
    }

    @Override
    public Object getMOFormDataTotal(String categoryCode, Integer year) {

        List<SubcategoryS> subcategorySList = subcategoryDao.findSubcategoryByCategoryCode(categoryCode);
        List<String> subcategoryCodeList = subcategorySList.stream().map(SubcategoryS::getSubcategoryCode).collect(Collectors.toList());
        List<IndicatorDto> indicatorDtos = indicatorDao.findIndicatorsSubCategoryCode(subcategoryCodeList);
        List<String> indCodeList = indicatorDtos.stream().map(IndicatorDto::getIndCode).collect(Collectors.toList());
        List<String> allActiveMoCode = moluOfficeDao.findAllActiveMoCode();

        HashMap<String, Object> dataMap = new HashMap<>();

        Integer lastYear = year - 1;
        Integer nextYear = year + 1;

        //2023 ACTUAL
        List<Map<String, Object>> lastActualYearlList = formInputValueDao.findGroupByIndCodeByFormInputEntryId(lastYear, "A", null);

        dataMap.put(String.valueOf(lastYear), Map.of("ACTUAL", lastActualYearlList));

        //2024 target
        List<Map<String, Object>> yearTargetList = indicatorsTargetDao.findTargetGroupByIndCode(year, indCodeList, 0, null);

        //2024 ACTUAL
        List<Map<String, Object>> actualList = formInputValueDao.findGroupByIndCodeByFormInputEntryId(year, "A", null);

        //变形 yearTargetList
        HashMap<String, Double> indicatorTargetMap = new HashMap<>();
        for(Map<String, Object> target : yearTargetList) {
            indicatorTargetMap.put((String) target.get("indicatorCode"), (Double) target.get("value"));
        }

        //2024 ACTUAL_VS_TARGET
        ArrayList<Object> actualVsTargetList = new ArrayList<>();
        for (Map<String, Object> map : actualList) {

            Map<String, Object> map1 = new HashMap<>(map);

            map1.put("type", "ACTUAL_VS_TARGET");

            String indCode = (String) map1.get("indicatorCode");

            Double value = map1.get("value") == null ? 0 : (Double) map1.get("value");

            Double targetValue = indicatorTargetMap.containsKey(indCode) ? indicatorTargetMap.get(indCode) : 0;

            map1.put("value", value / targetValue * 100 + "%");

            actualVsTargetList.add(map1);
        }

        //BENCHMARK
        List<Map<String, Object>> BENCHMARKList = formInputEntryDao.findApprovalLastMonth(year, null);

        //2024 PROJECTPERIOD
        List<Map<String, Object>> pyList = formInputValueDao.findGroupByIndCodeByFormInputEntryId(year, "PY", null);

        //2024 PROJECT_VS_TARGET
        ArrayList<Object> pyVsTargetList = new ArrayList<>();
        for (Map<String, Object> map : pyList) {

            Map<String, Object> map1 = new HashMap<>(map);

            map1.put("type", "PROJECT_VS_TARGET");

            String indCode = (String) map1.get("indicatorCode");

            Double value = map1.get("value") == null ? 0 : (Double) map1.get("value");

            Double targetValue = indicatorTargetMap.containsKey(indCode) ? indicatorTargetMap.get(indCode) : 0;

            map1.put("value", value / targetValue * 100 + "%");

            pyVsTargetList.add(map1);
        }

        dataMap.put(String.valueOf(year), Map.of(
                "TARGET", yearTargetList,
                "ACTUAL", actualList,
                "ACTUAL_VS_TARGET", actualVsTargetList,
                "BENCHMARK", BENCHMARKList, //todo
                "PROJECTPERIOD", pyList,
                "PROJECT_VS_TARGET", pyVsTargetList
                )
        );

        //2025 ESTIMATE
        List<Map<String, Object>> eList = formInputValueDao.findGroupByIndCodeByFormInputEntryId(nextYear, "E", null);

        // lastActualValueMap 变形
        HashMap<String, Double> lastActualValueMap = new HashMap<>();
        for (Map map : lastActualYearlList) {
            Double value = map.get("value") == null ? 0 : (Double) map.get("value");
            lastActualValueMap.put((String) map.get("indicatorCode"), value);
        }

        //2025 ESTIMATE_VS_TARGET
        ArrayList<Object> eVsACTUALList = new ArrayList<>();
        ArrayList<Object> eVsTargetList = new ArrayList<>();
        for (Map<String, Object> map : eList) {
            String indCode = (String) map.get("indicatorCode");
            Double value = map.get("value") == null ? 0 : (Double) map.get("value");

            //组装 ESTIMATE_VS_ACTUAL Map
            Map<String, Object> mapA = new HashMap<>(map);
            mapA.put("type", "ESTIMATE_VS_ACTUAL");
            Double actualValue = lastActualValueMap.containsKey(indCode) ? lastActualValueMap.get(indCode) : 0;
            mapA.put("value", value / actualValue);
            eVsACTUALList.add(mapA);

            //组装 ESTIMATE_VS_TARGET map
            Map<String, Object> mapT = new HashMap<>(map);
            mapT.put("type", "ESTIMATE_VS_TARGET");
            Double targetValueT = indicatorTargetMap.containsKey(indCode) ? indicatorTargetMap.get(indCode) : 0;
            mapT.put("value", value / targetValueT * 100 + "%");
            eVsTargetList.add(mapT);
        }

        dataMap.put(String.valueOf(nextYear), Map.of(
                "ESTIMATE", eList,
                "ESTIMATE_VS_ACTUAL", eVsACTUALList,
                "ESTIMATE_VS_TARGET", eVsTargetList
                )
        );

        HashMap<String, Object> resMap = new HashMap<>();
        resMap.put("currentYear", year);
        resMap.put("formentryid", "");
        resMap.put("dataperiodtype", "");
        resMap.put("inputyear", Arrays.asList(lastYear, year, nextYear));
        resMap.put("inputmonth", "");
        resMap.put("indicatorCode", indCodeList);
        resMap.put("data", new ArrayList<>(Collections.singletonList(dataMap)));

        return resMap;
    }
}
