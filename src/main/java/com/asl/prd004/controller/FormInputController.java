package com.asl.prd004.controller;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.FormInputTargetDto;
import com.asl.prd004.entity.FormInputRequestPeriodS;
import com.asl.prd004.enums.MonthLettersEnum;
import com.asl.prd004.service.IFormInputService;
import com.asl.prd004.utils.Log;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/formInput")
public class FormInputController {

    @Autowired
    IFormInputService formInputService;

    @PostMapping(value = "/getActionList")
    public ResultGenerator getActionList(@RequestBody String data) throws Exception {
        String lang = "";
        if (data != null && !data.isEmpty()) {
            JSONObject json = new JSONObject(data);
            lang = json.getString("lang");
        }

        return ResultGenerator.getSuccessResult(formInputService.getActionList(lang));
    }

    @PostMapping(value = "/searchFormRecord")
    public ResultGenerator searchFormRecord(@RequestBody String data) throws Exception {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("参数为空!");
        }

        JSONObject json = new JSONObject(data);

        String molu = json.has("molu") ? json.getString("molu") : null;
        String refNum = json.has("refNum") ? json.getString("refNum") : null;
        String formInputRequestTitle = json.has("formInputRequestTitle") ? json.getString("formInputRequestTitle") : null;
        String categoryCode = json.has("categoryCode") ? json.getString("categoryCode") : null;
        String inputStartDate = json.has("inputStartDate") ? json.getString("inputStartDate") : null;
        String inputEndDate = json.has("inputEndDate") ? json.getString("inputEndDate") : null;
        String status = json.has("status") ? json.getString("status") : null;

        String lang = json.getString("lang");

        JSONObject pageState = json.getJSONObject("pageState");

        JSONObject sort = json.getJSONObject("sortModel");

        return ResultGenerator.getSuccessResult(formInputService.searchFormRecord(molu, refNum, formInputRequestTitle, categoryCode,
                inputStartDate, inputEndDate, status, lang, pageState, sort));
    }

    @PostMapping(value = "/getFormData")
    public ResultGenerator getFormData(@RequestBody String data) throws JSONException {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        return ResultGenerator.getSuccessResult(formInputService.getFormData(formInputEntryId, lang));
    }

    @Log("saveFormData request.")
    @RequestMapping(value = "/saveFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator saveFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();

        String datas = json.getString("data").isEmpty() ? "" : json.getString("data").trim();

        JSONArray datasArray = new JSONArray(datas);

        if (formInputService.saveFormData(formInputEntryId, datasArray)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("submitFormData request.")
    @RequestMapping(value = "/submitFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator submitFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();

        String datas = json.getString("data").isEmpty() ? "" : json.getString("data").trim();

        JSONArray datasArray = new JSONArray(datas);

        if (formInputService.submitFormData(formInputEntryId, datasArray)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("verifyFormData")
    @RequestMapping(value = "/verifyFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator verifyFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();

        String datas = json.getString("data").isEmpty() ? "" : json.getString("data").trim();

        JSONArray datasArray = new JSONArray(datas);

        if (formInputService.verifyFormData(formInputEntryId, datasArray)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("withdrawFormData")
    @RequestMapping(value = "/withdrawFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator withdrawFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();

        if (!checkUserHasRole("MO/LU Inputer") && !checkUserHasRole("MO/LU Verifier")) {
            return ResultGenerator.getFailResult("current user role can't operate.");
        }

        String userRole = ContextHolder.getUserRole();

        String[] userRoleArray = userRole.split(",");

        List<String> userRoleNameList = Arrays.stream(userRoleArray).collect(Collectors.toList());

        if (formInputService.withdrawFormData(formInputEntryId, userRoleNameList)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @PostMapping(value = "/getConsolideFormData")
    public ResultGenerator getConsolideFormData(@RequestBody String data) throws JSONException {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputRequestId = json.getString("formInputRequestId").isEmpty() ? "" : json.getString("formInputRequestId").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        return ResultGenerator.getSuccessResult(formInputService.getConsolideFormData(formInputRequestId, moCode, lang));
    }

    @PostMapping(value = "/getConsolideFormSummary")
    public ResultGenerator getConsolideFormSummary(@RequestBody String data) throws JSONException {

        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputRequestId = json.getString("formInputRequestId").isEmpty() ? "" : json.getString("formInputRequestId").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        Map<String, Object> consolideFormSummary = formInputService.getConsolideFormSummary(formInputRequestId, moCode, lang);

        consolideFormSummary.remove("indicatorList");

        return ResultGenerator.getSuccessResult(consolideFormSummary);
    }

    @Log("acceptMOFormData")
    @RequestMapping(value = "/acceptMOFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator acceptMOFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();

        if (!checkUserHasRole("MO Reviewer"))
            return ResultGenerator.getFailResult("only MO Reviewer role can operate!");

        if (formInputService.acceptMOFormData(formInputEntryId)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("endorseMOFormData")
    @RequestMapping(value = "/endorseMOFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator endorseMOFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputRequestId = json.getString("formInputRequestId").isEmpty() ? "" : json.getString("formInputRequestId").trim();

        //check role
        if (!checkUserHasRole("MO Reviewer"))
            return ResultGenerator.getFailResult("only MO Reviewer role can operate!");

        //check office
        String userOffice = ContextHolder.getOffice();
        if (userOffice.isEmpty()) {
            return ResultGenerator.getFailResult("your office is empty!");
        }

        if (formInputService.endorseMOFormData(formInputRequestId)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("rejectFormData")
    @RequestMapping(value = "/rejectFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator rejectFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();

        if (!checkUserHasRole("MO/LU Verifier"))
            return ResultGenerator.getFailResult("only MO/LU Verifier role can operate!");

        if (formInputService.rejectFormData(formInputEntryId)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    private boolean checkUserHasRole(String role) {

        String userRole = ContextHolder.getUserRole();

        String[] userRoleArray = userRole.split(",");

        List<String> userRoleNameList = Arrays.stream(userRoleArray).collect(Collectors.toList());

        if (userRoleNameList.isEmpty()) return false;

        return userRoleNameList.contains(role);
    }

    @Log("rejectMOFormData")
    @RequestMapping(value = "/rejectMOFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator rejectMOFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();

        if (!checkUserHasRole("MO Reviewer"))
            return ResultGenerator.getFailResult("only MO Reviewer role can operate!");

        if (formInputService.rejectMOFormData(formInputEntryId)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("rejectCMABFormData")
    @RequestMapping(value = "/rejectCMABFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator rejectCMABFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputRequestId = json.getString("formInputRequestId").isEmpty() ? "" : json.getString("formInputRequestId").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();

        if (!checkUserHasRole("CMAB Approver"))
            return ResultGenerator.getFailResult("only CMAB Approver role can operate!");

        if (formInputService.rejectCMABFormData(formInputRequestId, moCode)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("approveCMABFormData.")
    @RequestMapping(value = "/approveCMABFormData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator approveCMABFormData(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputRequestId = json.getString("formInputRequestId").isEmpty() ? "" : json.getString("formInputRequestId").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();

        if (formInputService.approveCMABFormData(formInputRequestId, moCode)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @Log("saveMORemark.")
    @RequestMapping(value = "/saveMORemark", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator saveMORemark(@RequestBody String data) throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("Parameter is empty\n!");
        }

        JSONObject json = new JSONObject(data);

        String formInputRequestId = json.getString("formInputRequestId").isEmpty() ? "" : json.getString("formInputRequestId").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();
        String indicatorCode = json.getString("indicatorCode").isEmpty() ? "" : json.getString("indicatorCode").trim();
        String remark = json.getString("remark").isEmpty() ? "" : json.getString("remark").trim();

        if (formInputService.saveMORemark(formInputRequestId, moCode, indicatorCode, remark)) {
            return ResultGenerator.getSuccessResult("success");
        } else {
            return ResultGenerator.getFailResult("failed");
        }
    }

    @RequestMapping(value = "/exportConsolideFormData", method = RequestMethod.POST, produces = "application/json")
    public void exportConsolideFormData(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {

        JSONObject json = new JSONObject(data);

        String formInputRequestId = json.getString("formInputRequestId").isEmpty() ? "" : json.getString("formInputRequestId").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        Map consolideFormData = formInputService.getConsolideFormDataExport(formInputRequestId, moCode, lang);

        int currentYear = (int) consolideFormData.get("currentYear");

        String dataperiodtype = (String) consolideFormData.get("dataperiodtype");

        List inputmonthList = (List) consolideFormData.get("inputmonth");
        Integer currentM = (Integer) Collections.min(inputmonthList);

        // 第一步，创建一个webbook，对应一个Excel文件
        Workbook wb = new XSSFWorkbook();

        // 创建一个居中格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setAlignment(HSSFCellStyle.VERTICAL_CENTER);

        //创建加粗字体大小格式
        CellStyle style1 = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        style1.setFont(font);

        // 创建换行格式
        DataFormat dataFormat = wb.createDataFormat();
        short format = dataFormat.getFormat("\n");
        CellStyle style2 = wb.createCellStyle();
        style2.setWrapText(true);
//        style2.setDataFormat(format);

        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = wb.createSheet("Sheet1");
//        sheet.autoSizeColumn(0);

        sheet.setColumnWidth(0, 30 * 256);
        sheet.setColumnWidth(1, 50 * 256);

        CellRangeAddress cellRangeAddress00 = new CellRangeAddress(0, 2, 0, 1);
        sheet.addMergedRegion(cellRangeAddress00);

        //创建第一行
        Row row = sheet.createRow(0);

        List entryList = (List) consolideFormData.get("entryList");

        int f = 2;
        Cell cell = row.createCell(f);
        for (int i = 0; i < entryList.size(); i++) {
            Map entry = (Map) entryList.get(i);
            cell.setCellValue(entry.get("moluCode").toString());
            cell.setCellStyle(style);
            cell.setCellStyle(style1);
            CellRangeAddress cellRangeAddress01 = new CellRangeAddress(0, 0, f, f + 12);
            sheet.addMergedRegion(cellRangeAddress01);
            f += 13;
            cell = row.createCell(f);
        }

        //创建第二行
        Row row1 = sheet.createRow(1);
        row1.setHeightInPoints(30);
        int from = 2;
        Cell cell1 = row1.createCell(from);

        ArrayList<Integer> needSetWidthCell = new ArrayList<>();
        for (int i = 0; i < entryList.size(); i++) {

            cell1.setCellValue(currentYear + " (Actual-locked data)");
            CellRangeAddress cellRangeAddress11 = new CellRangeAddress(1, 1, from, from + currentM - 2);
            sheet.addMergedRegion(cellRangeAddress11);
            cell1.setCellStyle(style);
            cell1.setCellStyle(style1);
            from += currentM - 2;

            cell1 = row1.createCell(++from);
            cell1.setCellValue("Current (Actual)");
            cell1.setCellStyle(style);
            cell1.setCellStyle(style1);
            CellRangeAddress cellRangeAddress12 = new CellRangeAddress(1, 1, from, from + (12 - currentM));
            sheet.addMergedRegion(cellRangeAddress12);
            from += 12 - currentM;

            cell1 = row1.createCell(++from);
            cell1.setCellValue("2024\n" +
                    "Actual\n" +
                    "(Jan - Dec)");
            cell1.setCellStyle(style);
            cell1.setCellStyle(style1);
            cell1.setCellStyle(style2);
            CellRangeAddress cellRangeAddress13 = new CellRangeAddress(1, 2, from, from);
            sheet.addMergedRegion(cellRangeAddress13);

            needSetWidthCell.add(from);

            cell1 = row1.createCell(++from);
        }

        needSetWidthCell.forEach(integer -> sheet.setColumnWidth(integer, 15 * 256));

        //开始第三行
        Row row2 = sheet.createRow(2);

        //前面空两个单元格
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue("");

        cell2 = row2.createCell(1);
        cell2.setCellValue("");

        from = 2;
        for (int i = 0; i < entryList.size(); i++) {
            for (MonthLettersEnum months : MonthLettersEnum.values()) {
                cell2 = row2.createCell(from + i);
                cell2.setCellValue(months.getFullLetter());
                cell2.setCellStyle(style);
                cell2.setCellStyle(style1);
                from++;
            }
        }

        //开始第四行-结尾
        List subcategoryList = (List) consolideFormData.get("subcategoryArrayList");

        int n = 3;
        int row_start = 3;
        for (Object o : subcategoryList) {
            Map map = (Map) o;
            String subcategory = map.get("subcategory").toString();

            //创建indicator 行
            List indicatorsList = (List) map.get("indicators");
            for (Object indicator : indicatorsList) {

                Map indicatorMap = (Map) indicator;

                Row row3 = sheet.createRow(n++);

                Cell cell3 = row3.createCell(0);
                cell3.setCellValue(subcategory);
                cell3.setCellStyle(style);
                cell3.setCellStyle(style1);

                cell3 = row3.createCell(1);
                cell3.setCellValue(indicatorMap.get("indicatorName").toString());
                cell3.setCellStyle(style1);

                int m = 2;
                for (int i = 0; i < entryList.size(); i++) {
                    Map entry = (Map) entryList.get(i);
                    Map<String, Integer> totalMap = (Map) entry.get("totalMap");
                    List newList = (List) entry.get("newList");

                    HashMap<String, Integer> monthMapByMolu = new HashMap<>();
                    for (int k = 0; k < newList.size(); k++) {
                        Map m1 = (Map) newList.get(k);
                        monthMapByMolu.put(m1.get("indicatorCode") + "-" + m1.get("startMonth"), Integer.parseInt((String) m1.get("value")));
                    }

                    for (int j = 1; j <= 12; j++) {
                        cell3 = row3.createCell(m + j - 1);
                        Integer value = monthMapByMolu.getOrDefault(indicatorMap.get("ind_code") + "-" + j, 0);
                        cell3.setCellValue(value);
                    }
                    m += 12;
                    cell3 = row3.createCell(m++);
                    cell3.setCellValue(String.valueOf(totalMap.get(indicatorMap.get("ind_code").toString())));
                }
            }
            if (indicatorsList.size() > 0) {
                CellRangeAddress cellRangeAddress30 = new CellRangeAddress(row_start, row_start + indicatorsList.size() - 1, 0, 0);
                sheet.addMergedRegion(cellRangeAddress30);
            }
            row_start += indicatorsList.size();
        }

        //第六步,输出Excel文件
        OutputStream output = response.getOutputStream();
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String fileName = "ExportData-CMAB-" + dataperiodtype + "_" + df.format(new Date());// new Date()为获取当前系统时间
        response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
    }

    @RequestMapping(value = "/exportConsolideFormSummary", method = RequestMethod.POST, produces = "application/json")
    public void exportConsolideFormSummary(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {

        JSONObject json = new JSONObject(data);

        String formInputRequestId = json.getString("formInputRequestId").isEmpty() ? "" : json.getString("formInputRequestId").trim();
        String moCode = json.getString("moCode").isEmpty() ? "" : json.getString("moCode").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        Map<String, Object> consolideFormSummary = formInputService.getConsolideFormSummary(formInputRequestId, moCode, lang);

        List inputyearList = (List) consolideFormSummary.get("inputyear");
        int currentYear = (int) inputyearList.get(0);

        String dataperiodtype = (String) consolideFormSummary.get("dataperiodtype");

//        List inputmonthList = (List) consolideFormSummary.get("inputmonth");
//        Integer currentM = (Integer) Collections.min(inputmonthList);

        // 第一步，创建一个webbook，对应一个Excel文件
        Workbook wb = new XSSFWorkbook();

        // 创建一个居中格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setAlignment(HSSFCellStyle.VERTICAL_CENTER);

        //创建加粗字体大小格式
        CellStyle style1 = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        style1.setFont(font);

        // 创建换行格式
        DataFormat dataFormat = wb.createDataFormat();
        short format = dataFormat.getFormat("\n");
        CellStyle style2 = wb.createCellStyle();
        style2.setWrapText(true);

        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = wb.createSheet("Sheet1");

        sheet.setColumnWidth(0, 30 * 256);
        sheet.setColumnWidth(1, 50 * 256);
        sheet.setColumnWidth(2, 20 * 256);

        CellRangeAddress cellRangeAddress00 = new CellRangeAddress(0, 1, 0, 1);
        sheet.addMergedRegion(cellRangeAddress00);

        HashMap<String, Double> targetMap = new HashMap<>();
        HashMap<String, List<Map<String, Object>>> totalMap = new HashMap<>();
        HashMap<String, Integer> MOTOTALMap = new HashMap<>();
        HashMap<String, String> VARIANCEMap = new HashMap<>();
        HashMap<String, String> REMARKMap = new HashMap<>();
        List dataList = (List) consolideFormSummary.get("data");
        for (int i = 0; i < dataList.size(); i++) {
            Map m = (Map) dataList.get(i);
            String type = m.get("type").toString();
            Object moluCodeObject = m.getOrDefault("moluCode", null);
            String moluCode = moluCodeObject == null ? "" : moluCodeObject.toString();
            List dataData = (List) m.get("data");
            switch (type) {
                case "TARGET":
                    for (Object ddObject : dataData) {
                        FormInputTargetDto formInputTargetDto = (FormInputTargetDto) ddObject;
                        targetMap.put(formInputTargetDto.getIndicatorCode(), formInputTargetDto.getValue());
                    }
                    break;
                case "TOTAL":
                    totalMap.put(moluCode, dataData);
                    break;
                case "MOTOTAL":
                    for (Object ddObject : dataData) {
                        Map ddMap = (Map) ddObject;
                        MOTOTALMap.put(ddMap.get("indicatorCode").toString(), (Integer) ddMap.get("value"));
                    }
                    break;
                case "VARIANCE":
                    for (Object ddObject : dataData) {
                        Map ddMap = (Map) ddObject;
                        VARIANCEMap.put(ddMap.get("indicatorCode").toString(), ddMap.get("value").toString());
                    }
                    break;
                case "REMARK":
                    for (Object ddObject : dataData) {
                        Map ddMap = (Map) ddObject;
                        REMARKMap.put(ddMap.get("indicatorCode").toString(), ddMap.get("value").toString());
                    }
                    break;
            }
        }

        Set<String> moluSet = totalMap.keySet();

        //创建第一行
        Row row = sheet.createRow(0);
        row.setHeightInPoints(40);

        //target cell
        Cell cell = row.createCell(2);
        cell.setCellValue(String.join("+", moluSet));
        cell.setCellStyle(style);
        cell.setCellStyle(style1);

        ArrayList<Integer> needSetWidthCell = new ArrayList<>();

        //molu cell
        int f = 3;
        for (String molu : moluSet) {
            cell = row.createCell(f);
            cell.setCellValue(molu);
            cell.setCellStyle(style);
            cell.setCellStyle(style1);
            needSetWidthCell.add(f);
            f++;
        }

        needSetWidthCell.add(f);

        //MOTOTAL cell
        cell = row.createCell(f++);
        cell.setCellValue("Total\n" + String.join("+", moluSet));
        cell.setCellStyle(style);
        cell.setCellStyle(style1);
        cell.setCellStyle(style2);

        cell = row.createCell(f);
        cell.setCellValue("");

        needSetWidthCell.add(f);

        CellRangeAddress cellRangeAddressMOTOTAL = new CellRangeAddress(0, 0, f - 1, f);
        sheet.addMergedRegion(cellRangeAddressMOTOTAL);

        //设置列宽度
        needSetWidthCell.forEach(integer -> sheet.setColumnWidth(integer, 20 * 256));

        //创建第二行
        Row row1 = sheet.createRow(1);

        //target cell
        Cell cell1 = row1.createCell(2);
        cell1.setCellValue(currentYear + "\nTarget");
        cell1.setCellStyle(style);
        cell1.setCellStyle(style1);
        cell1.setCellStyle(style2);

        //molu cell
        f = 3;
        for (String molu : moluSet) {
            cell1 = row1.createCell(f);
            cell1.setCellValue(currentYear + "\n" +
                    "Actual\n" +
                    "(Jan - Dec)");
            cell1.setCellStyle(style);
            cell1.setCellStyle(style1);
            cell1.setCellStyle(style2);
            f++;
        }

        //MOTOTAL cell
        cell1 = row1.createCell(f++);
        cell1.setCellValue(currentYear + "\n" +
                "Actual\n" +
                "(Jan - Dec)");
        cell1.setCellStyle(style);
        cell1.setCellStyle(style1);
        cell1.setCellStyle(style2);

        //Variance cell
        cell1 = row1.createCell(f);
        cell1.setCellValue("% Variance of\n" +
                "2024 Actual vs 2024 Target");
        cell1.setCellStyle(style);
        cell1.setCellStyle(style1);
        cell1.setCellStyle(style2);

        //设置行高
        row1.setHeightInPoints(40);

        //开始第三行-结尾
        List indicatorList = (List) consolideFormSummary.get("indicatorList");

        int n = 2;
        int mergeFrom = 2;
        HashSet<String> subcategorySet = new HashSet<>();

        for (Object indicator : indicatorList) {

            Map indicatorMap = (Map) indicator;

            String subcategoryCode = indicatorMap.get("subcategoryCode").toString();

            String indicatorCode = indicatorMap.get("indicatorCode").toString();

            Row row2 = sheet.createRow(n);

            //subCategory cell
            Cell cell2 = row2.createCell(0);
            cell2.setCellValue(!subcategorySet.contains(subcategoryCode) ? indicatorMap.get("subcategory").toString() : "");
            cell2.setCellStyle(style);
            cell2.setCellStyle(style1);

            //indicator cell
            cell2 = row2.createCell(1);
            cell2.setCellValue(indicatorMap.get("indicator").toString());
            cell2.setCellStyle(style1);

            //target
            cell2 = row2.createCell(2);
            cell2.setCellValue(targetMap.get(indicatorCode));

            //molu total value cell
            int mm = 3;
            for (String molu : moluSet) {
                List<Map<String, Object>> moluTotalList = totalMap.get(molu);
                int value = 0;
                for (Map<String, Object> m : moluTotalList) {
                    if (indicatorCode.equals(m.get("indicatorCode"))) {
                        value = (int) m.get("value");
                        break;
                    }
                }
                cell2 = row2.createCell(mm);
                cell2.setCellValue(value);
                mm++;
            }

            // MOTOTAL cell
            cell2 = row2.createCell(mm++);
            cell2.setCellValue(MOTOTALMap.get(indicatorCode));

            // Variance cell
            cell2 = row2.createCell(mm);
            cell2.setCellValue(VARIANCEMap.get(indicatorCode));

            if (subcategorySet.contains(subcategoryCode)) {
                CellRangeAddress cellRangeAddress20 = new CellRangeAddress(mergeFrom, n, 0, 0);
                sheet.addMergedRegion(cellRangeAddress20);
                mergeFrom += n - 1;
            }
            subcategorySet.add(subcategoryCode);
            n++;
        }

        //第六步,输出Excel文件
        OutputStream output = response.getOutputStream();
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String fileName = "ExportData-CMAB-Summary-" + dataperiodtype;
        response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
    }

    @RequestMapping(value = "/inputterDownloadTemplate", method = RequestMethod.POST, produces = "application/json")
    public void inputterDownloadTemplate(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {

        JSONObject json = new JSONObject(data);

        String formInputEntryId = json.getString("formInputEntryId").isEmpty() ? "" : json.getString("formInputEntryId").trim();
        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        Map<String, Object> templateData = formInputService.getInputterDownloadTemplate(formInputEntryId, lang);
        Workbook wb = new XSSFWorkbook();

        // 在webbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = wb.createSheet("Sheet1");

        //设置每列宽度
        sheet.setColumnWidth(0, 50 * 256);
        sheet.setColumnWidth(1, 10 * 256);
        sheet.setColumnWidth(2, 10 * 256);

        //创建居中格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //创建居中格式
        CellStyle style1 = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        style1.setFont(font);

        // 创建Molu 行
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Performance Indicators");
        cell.setCellStyle(style1);

        cell = row.createCell(1);
        cell.setCellValue(templateData.get("molu").toString());
        cell.setCellStyle(style1);

        cell = row.createCell(2);
        cell.setCellValue("");

        //创建request period 行
        Row row1 = sheet.createRow(1);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("");

        List requestPeriodList = (List) templateData.get("requestPeriod");

        int i = 1;
        cell1 = row1.createCell(i);
        for (Object requestPeriodS : requestPeriodList) {
            FormInputRequestPeriodS periodS = (FormInputRequestPeriodS) requestPeriodS;
            String startMonthStr = periodS.getYear() + " " + MonthLettersEnum.getLetterByMonth(periodS.getStartMonth());
            String endMonthStr = periodS.getYear() + " " + MonthLettersEnum.getLetterByMonth(periodS.getEndMonth());

            String str;
            String dataPeriodType = periodS.getDataPeriodType();
            if (dataPeriodType.equals("A")) {
                str = startMonthStr;
            } else if (dataPeriodType.equals("E")) {
                str = periodS.getYear().toString();
            } else {
                str = startMonthStr + " to " + endMonthStr;
            }

            cell1.setCellValue(str);
            cell1.setCellStyle(style1);
            i++;
            cell1 = row1.createCell(i);
        }

        // 获取 subcategory 数据
        List subcategoryList = (List) templateData.get("subcategoryArrayList");

        int n = 2;
        for (Object o : subcategoryList) {
            Map map = (Map) o;
            String subcategory = map.get("subcategory").toString();

            //创建subcategory 行
            Row row2 = sheet.createRow(n);

            Cell cell2 = row2.createCell(0);
            cell2.setCellValue(subcategory);
            cell2.setCellStyle(style1);

            cell2 = row2.createCell(1);
            cell2.setCellValue("");

            //创建indicator 行
            List indicatorsList = (List) map.get("indicators");
            for (Object indicator : indicatorsList) {

                Map indicatorMap = (Map) indicator;

                Row row3 = sheet.createRow(++n);

                Cell cell3 = row3.createCell(0);
                cell3.setCellValue(indicatorMap.get("indicatorName").toString());
                cell3.setCellStyle(style);
            }
            n++;
        }

        //第六步,输出Excel文件
        OutputStream output = response.getOutputStream();
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String fileName = "DownloadTemplate_" + df.format(new Date());// new Date()为获取当前系统时间
        response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
    }

    @RequestMapping(value = "/dataExport", method = RequestMethod.POST, produces = "application/json")
    public void dataExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {

        JSONObject json = new JSONObject(data);

        String categoryCode = json.getString("categoryCode");
        String subCategoryCode = json.getString("subCategoryCode");
        String indicatorCode = json.getString("indicatorCode");
        String dataPeriodType = json.getString("dataPeriodType");

        JSONObject exportPeriodStart = json.getJSONObject("exportPeriodStart");
        JSONObject exportPeriodEnd = json.getJSONObject("exportPeriodEnd");

        String molu = json.getString("molu");

        String lang = json.getString("lang").isEmpty() ? "" : json.getString("lang").trim();

        JSONArray subCategoryCodeArray = new JSONArray(subCategoryCode);
        JSONArray indicatorCodeArray = new JSONArray(indicatorCode);
        JSONArray moluArray = new JSONArray(molu);

        List<Map<String, Object>> dataExport = formInputService.getDataExport(categoryCode, subCategoryCodeArray, indicatorCodeArray,
                dataPeriodType, exportPeriodStart, exportPeriodEnd, moluArray, lang);

        Workbook wb = new XSSFWorkbook();

        // 在webbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = wb.createSheet("Sheet1");

        //设置每列宽度
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 30 * 256);
        sheet.setColumnWidth(2, 45 * 256);
        sheet.setColumnWidth(3, 10 * 256);
        sheet.setColumnWidth(4, 10 * 256);
        sheet.setColumnWidth(5, 10 * 256);
        sheet.setColumnWidth(6, 10 * 256);
        sheet.setColumnWidth(7, 10 * 256);
        sheet.setColumnWidth(8, 10 * 256);

        //创建居中格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //创建加粗字体大小格式
        CellStyle style1 = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        style1.setFont(font);

        // 创建title行
        Row row = sheet.createRow(0);
        List<String> titles = Arrays.asList("Category", "Sub-Category", "Indicator", "Data Period Type", "MO/LU", "Year",
                "Month Start", "Month End", "Count");
        int i = 0;
        for (String title : titles) {
            Cell cell = row.createCell(i);
            cell.setCellValue(title);
            cell.setCellStyle(style1);
            i++;
        }

        int rowsFrom = 1;
        Row rowR = sheet.createRow(rowsFrom);
        for (Map<String, Object> dataMap : dataExport) {
            Cell cell0 = rowR.createCell(0);
            cell0.setCellValue(dataMap.get("category").toString());

            Cell cell1 = rowR.createCell(1);
            cell1.setCellValue(dataMap.get("subCategory").toString());

            Cell cell2 = rowR.createCell(2);
            cell2.setCellValue(dataMap.get("indicator").toString());

            Cell cell3 = rowR.createCell(3);
            cell3.setCellValue(dataMap.get("dataPeriodType").toString());

            Cell cell4 = rowR.createCell(4);
            cell4.setCellValue(dataMap.get("moluCode").toString());

            Cell cell5 = rowR.createCell(5);
            cell5.setCellValue(dataMap.get("year").toString());

            Cell cell6 = rowR.createCell(6);
            String startMonth = dataMap.get("startMonth").toString();
            cell6.setCellValue(MonthLettersEnum.getFullLetterByMonth(Integer.parseInt(startMonth)));

            Cell cell7 = rowR.createCell(7);
            String endMonth = dataMap.get("endMonth").toString();
            cell7.setCellValue(dataPeriodType.equals("A") ? "" : MonthLettersEnum.getFullLetterByMonth(Integer.parseInt(endMonth)));

            Cell cell8 = rowR.createCell(8);
            Object countObj = dataMap.get("Count");
            cell8.setCellValue(countObj == null ? "" : countObj.toString());

            rowR = sheet.createRow(++rowsFrom);
        }

        //输出Excel文件
        OutputStream output = response.getOutputStream();
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String fileName = "DataExport_" + df.format(new Date());// new Date()为获取当前系统时间
        response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
    }
}
