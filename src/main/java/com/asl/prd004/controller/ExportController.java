package com.asl.prd004.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asl.prd004.dao.MisColumnDao;
import com.asl.prd004.dao.MisPropertyConfigDetailDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisQueryFormColumn;
import com.asl.prd004.service.IRecordService;
import com.asl.prd004.service.ISimpleSearchService;
import com.asl.prd004.utils.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/export")
public class ExportController {
    
    @Resource
    private IRecordService recordService;

    @Resource
    private ISimpleSearchService simpleSearchService;

    @Autowired
    private MisColumnDao misColumnDao;

    @Autowired
    private MisPropertyConfigDetailDao misPropertyConfigDetailDao;
    @Log("Export Excel97")
    @RequestMapping(value="/excel97",method = RequestMethod.POST, produces = "application/json")
    public void exportExcel97(HttpServletRequest request,HttpServletResponse response,@RequestBody String data) throws Exception {

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("Sheet1");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        HSSFCell cell = row.createCell(0);
        Map<String,Object> map = getData(data);
        List<MisColumn> columnList = (List<MisColumn>)map.get("columnList");
        cell.setCellValue("id");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        List recordList = (List)map.get("recordList");
        for (int i = 0; i < columnList.size(); i++) {
            cell.setCellValue(columnList.get(i).getMisColumnName());
            cell.setCellStyle(style);
            cell = row.createCell(i+2);
        }
        // 第五步，写入实体数据
        for (int i = 0; i < recordList.size(); i++){
            row = sheet.createRow(i + 1);
            // 第四步，创建单元格，并设置值
            for (int j = 0; j < columnList.size()+1; j++){
                Object[] objects = (Object[])recordList.get(i);
                row.createCell(j).setCellValue((String)objects[j]);
            }
        }
        //第六步,输出Excel文件
        OutputStream output = response.getOutputStream();
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String fileName = df.format(new Date());// new Date()为获取当前系统时间
        response.setHeader("Content-disposition", "attachment; filename="+fileName+".xls");
        response.setContentType("application/msexcel");
        wb.write(output);
//        ZipOutputStream zipOutputStream = new ZipOutputStream(output);
//        zipOutputStream.putNextEntry(new ZipEntry(
//                wb.getSheetName(0).replace(" ", "").concat(".xlsx")));
//        wb.write(zipOutputStream);
//        zipOutputStream.flush();
//        zipOutputStream.close();
        output.close();
    }
    @Log("Export Excel03")
    @RequestMapping(value="/excel03",method = RequestMethod.POST, produces = "application/json")
    public void exportExcel03(HttpServletRequest request,HttpServletResponse response,@RequestBody String data) throws Exception {

        // 第一步，创建一个webbook，对应一个Excel文件
        Workbook wb = new XSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = wb.createSheet("Sheet1");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        Row row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        Cell cell = row.createCell(0);
        Map<String,Object> map = getData(data);
        List<MisColumn> columnList = (List<MisColumn>)map.get("columnList");
        List recordList = (List)map.get("recordList");
        cell.setCellValue("id");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        for (int i = 0; i < columnList.size(); i++) {
            cell.setCellValue(columnList.get(i).getMisColumnName());
            cell.setCellStyle(style);
            cell = row.createCell(i+2);
        }
        // 第五步，写入实体数据
        for (int i = 0; i < recordList.size(); i++){
            row = sheet.createRow(i + 1);
            // 第四步，创建单元格，并设置值
            for (int j = 0; j < columnList.size()+1; j++){
                Object[] objects = (Object[])recordList.get(i);
                row.createCell(j).setCellValue((String)objects[j]);
            }
        }
        //第六步,输出Excel文件
        OutputStream output = response.getOutputStream();
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String fileName = df.format(new Date());// new Date()为获取当前系统时间
        response.setHeader("Content-disposition", "attachment; filename="+fileName+".xlsx");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
    }

    private Map<String,Object> getData(String data) throws Exception{
        JSONObject json = new JSONObject(data);
        String typeId =  json.getString("typeId");
        String folderId =  json.getString("folderId");
        org.json.JSONArray columnData= json.getJSONArray("qfColumns");
        org.json.JSONArray conditionData= json.getJSONArray("qfConditions");
        String columns= "";

        for (int i = 0; i < columnData.length(); i++) {
            JSONObject qfc = (JSONObject) columnData.get(i);
            if(StringUtils.isNotEmpty((String)qfc.get("misQfcColumnId"))){
                columns+=qfc.get("misQfcColumnId")+",";
            }
        }

        List<MisColumn> misColumnList = new ArrayList<>();
        MisColumn misColumn = new MisColumn();
        misColumn.setMisTypeId(typeId);
        misColumn.setMisColumnLabel("id");
        misColumn.setMisColumnType("1");
        misColumnList.add(misColumn);
        List<MisColumn> columnList  = recordService.getColumnList(columns);
        misColumnList.addAll(columnList);

        List recordList = recordService.getRecordList(typeId,columnList,conditionData,folderId);

        Map<String,Object> map = new HashMap<>();
        map.put("columnList",columnList);
        map.put("recordList",recordList);
        return map;
    }
    @Log("Export Csv")
    @RequestMapping(value="/csv",method = RequestMethod.POST, produces = "application/json")
    public void exportCsv(HttpServletResponse response,@RequestBody ExportCsvDTO data) throws Exception{
        String filename =  new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".csv";

        if(StrUtil.equals("0",data.getExportType())){
            //单表查询
//            data = new ExportCsvDTO();
            data.setCrossRef(null);
            data.setQfConditions(null);

            List<MisQueryFormColumn> byMisTypeId =
                misColumnDao.findByMisTypeId(data.getTypeId()).stream()
                    .map(mc -> {
                        MisQueryFormColumn c = new MisQueryFormColumn();
                        c.setMisQfId(mc.getMisTypeId());
                        c.setMisQfcColumnId(mc.getMisColumnId());
                        c.setMisQfcId("");
                        return c;
                    })
                    .collect(Collectors.toList());

            List<TypeIdDto> propertyList  = misPropertyConfigDetailDao.getMisPropertyConfigDetailsByMisTypeId(data.getTypeId());
            List<MisQueryFormColumn> byMisTypeIdAfterFilter = new ArrayList<>();
            for (int i = 0; i < byMisTypeId.size(); i++) {
                TypeIdDto columnIdDto = new TypeIdDto();
                columnIdDto.setId(byMisTypeId.get(i).getMisQfcColumnId());
                if(propertyList.contains(columnIdDto)) {
                    byMisTypeIdAfterFilter.add(byMisTypeId.get(i));
                }
            }

            data.setQfColumns(byMisTypeIdAfterFilter);
        }

        SearchRecordVO searchRecordVO = recordService.searchRecord(data);
        List<MisColumn> columnList = searchRecordVO.getColumnList();
        List recordList = searchRecordVO.getRecordList();
//        Map<String,Object> map = getData(data);
//        List<MisColumn> columnList = (List<MisColumn>)map.get("columnList");
//        String[] colNames = new String[columnList.size()+1];
//        colNames[0] = "id";
//        for (int i = 0; i < columnList.size(); i++) {
//            colNames[i+1] = columnList.get(i).getMisColumnName();
//        }
//        List<String> collect =
        String[] colNames =
                columnList.stream().map(MisColumn::getMisColumnLabel).collect(Collectors.toList())
                        .toArray(new String[columnList.size()]);
        File file = new File(filename);
//        List recordList = (List)map.get("recordList");
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(colNames);
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
             CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);) {
            for (int i = 0; i < recordList.size(); i++) {
                Object[] objects = (Object[])recordList.get(i);
                Object[] object = new Object[objects.length-1];
                for (int j = 0; j < objects.length-1; j++) {
                    object[j] = "\t"+objects[j];
                }
                csvPrinter.printRecord(object);
            }
            csvPrinter.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            in = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            response.reset();
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            // 设置此response为文件下载响应
            response.setContentType("application/csv;charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.setCharacterEncoding("UTF-8");

            // 先写UTF-8文件标志头
            out.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
                file.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Log("Simple Search Export")
    @RequestMapping(value="/simpleSearchExport",method = RequestMethod.POST, produces = "application/json")
    public void simpleSearchExport(HttpServletResponse response,@RequestBody SimpleSearchRecordInputDto data){
        String filename =  new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".csv";


        Map<String,Object> map = simpleSearchService.simpleSearchExport(data);
        List<MisColumn> columnList = (List<MisColumn>)map.get("columnList");
        List recordList = (List)map.get("recordList");

        String[] colNames =
                columnList.stream().map(MisColumn::getMisColumnLabel).collect(Collectors.toList())
                        .toArray(new String[columnList.size()]);
        File file = new File(filename);
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(colNames);
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
             CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);) {
            for (int i = 0; i < recordList.size(); i++) {
                Object[] objects = (Object[])recordList.get(i);
                Object[] object = new Object[objects.length-1];
                for (int j = 0; j < objects.length-1; j++) {
                    object[j] = "\t"+objects[j];
                }
                csvPrinter.printRecord(object);
            }
            csvPrinter.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            in = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            response.reset();
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            // 设置此response为文件下载响应
            response.setContentType("application/csv;charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.setCharacterEncoding("UTF-8");

            // 先写UTF-8文件标志头
            out.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
                file.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
