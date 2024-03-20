package com.asl.prd004.controller;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.MisColumnDao;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.StatisticsReportDTO;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.entity.MisReport;
import com.asl.prd004.service.IRecordService;
import com.asl.prd004.service.IReportService;
import com.asl.prd004.utils.Log;
import com.asl.prd004.utils.ReportProcess;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private IReportService reportService;

    @Autowired
    private MisColumnDao misColumnDao;

    @Autowired
    private IRecordService recordService;

    @Log("Get report template.")
    @RequestMapping(value="/getReportTemplate")
    public ResultGenerator getReportTemplate(){
        return ResultGenerator.getSuccessResult(reportService.getReportTemplate());
    }

    @Log("Get reports.")
    @RequestMapping(value="/getReports",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getReports(@RequestBody PageableDto page)throws Exception{
        return ResultGenerator.getSuccessResult(reportService.getReports(page));
    }

    @Log("Generate statistics report.")
    @RequestMapping(value="/generateStatisticsReport")
    public ResultGenerator generateStatisticsReport(@RequestBody String data) throws Exception {
        JSONObject json =new JSONObject(data);
        String dateFrom = json.getString("dateFrom");
        String dateTo = json.getString("dateTo");
        String format = json.getString("format");
        List<StatisticsReportDTO> list = reportService.getStatisticsReport(dateFrom,dateTo);
//        String jrxmlPath  =  path+"/jasper/statistics.jrxml";
//        String jasperPath = path+"/jasper/statistics.jasper";
        //编译模板
        //JasperCompileManager.compileReportToFile(jrxmlPath,jasperPath);
        ClassPathResource resource = new ClassPathResource("/jasper"+File.separator+"statistics.jasper");
        InputStream jasperStream = resource.getInputStream();
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        //第三个参数：JavaBean作为数据源，使用JRBeanCollectionDataSource对象来填充
        JasperPrint print = JasperFillManager.fillReport(jasperReport,new HashMap<>(),dataSource);
        String reportPath  = "";
        File file = new File(System.getProperty("user.dir")+"/report");
        if(!file.exists()){
            file.mkdir();
        }
        String fileName = "";
        if("pdf".equals(format)){
            fileName = System.currentTimeMillis()+".pdf";
            reportPath = file.getPath()+File.separator+fileName;
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportPath);
            exporter.exportReport();
            //JasperExportManager.exportReportToPdfFile(print,pdfPath);
        }else if("csv".equals(format)){
            fileName = System.currentTimeMillis()+".csv";
            reportPath = file.getPath()+File.separator+fileName;
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportPath);
            exporter.exportReport();
        }else if("excel".equals(format)){
            fileName = System.currentTimeMillis()+".xlsx";
            reportPath = file.getPath()+File.separator+fileName;
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportPath);
            exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            //exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            exporter.exportReport();
        }
        String templateId = json.getString("templateId");
        return ResultGenerator.getSuccessResult(reportService.generateReport(templateId,"report/"+fileName));
    }

    @Log("Generate inventory report.")
    @RequestMapping(value="/generateInventoryReport")
    public ResultGenerator generateInventoryReport(@RequestBody String data,HttpServletResponse response) throws Exception{
        JSONObject json =new JSONObject(data);
        String typeId = json.getString("typeId");
        String dateFrom = json.getString("dateFrom");
        String dateTo = json.getString("dateTo");
        String format = json.getString("format");
        String templateId = json.getString("templateId");
        List<DicDto> list = misColumnDao.getColumnDic(typeId);
        String[] headers = new String[list.size()];
        String columns = "";
        for (int i = 0; i < list.size(); i++) {
            headers[i] = list.get(i).getValue();
            if(i!= list.size()-1){
                columns+=list.get(i).getValue()+",";
            }else{
                columns+=list.get(i).getValue();
            }
        }
        List recordList = recordService.getRecords(typeId,columns,dateFrom,dateTo);
        InputStream is = null;
        JasperDesign jasperDesign = null;
        // 从资源文件中读取报表
        is = this.getClass().getResourceAsStream("/jasper/inventory.jrxml");
        jasperDesign = (JasperDesign) JRXmlLoader.load(is);
        String reportPath = ReportProcess.preview(headers,recordList,null,jasperDesign,format);
        return ResultGenerator.getSuccessResult(reportService.generateReport(templateId,reportPath));
    }

    @Log("Download report.")
    @RequestMapping(value="/download")
    public void download(@RequestBody String data,HttpServletResponse response)throws Exception{
        JSONObject json =new JSONObject(data);
        String misReportId = json.getString("misReportId");
        MisReport report = reportService.findById(misReportId);
        File f = new File(System.getProperty("user.dir")+File.separator+report.getMisReportPath());
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
        byte[] buf = new byte[1024];
        int len = 0;
        response.reset();
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        response.setHeader("Access-Control-Allow-Origin","*");
        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0)
            out.write(buf, 0, len);
        br.close();
        out.close();
    }
}
