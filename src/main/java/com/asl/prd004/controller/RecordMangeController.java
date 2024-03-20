package com.asl.prd004.controller;
import com.alibaba.fastjson.JSON;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisGpTypePermission;
import com.asl.prd004.entity.MisPropertyConfig;
import com.asl.prd004.entity.MisPropertyConfigDetail;
import com.asl.prd004.entity.MisType;
import com.asl.prd004.utils.*;
import com.asl.prd004.service.IRecordMangeService;
import com.asl.prd004.vo.AuditLogVO;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import javax.servlet.http.HttpServletResponse;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import com.itextpdf.io.source.ByteArrayOutputStream;


@RestController
@RequestMapping("/RecordManage")
public class RecordMangeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordMangeController.class);

    @Autowired
    IRecordMangeService recordMange;

    @Autowired
    MisTypeDao misTypeDao;
    @Autowired
    MisColumnDao misColumnDao;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    MisGpPermissionDao misGpPermissionDao;

    @Autowired
    MisPropertyConfigDao misPropertyConfigDao;
    @Autowired
    MisPropertyConfigDetailDao misPropertyConfigDetailDao;

    @Log("Get table name")
    @PostMapping(value="/getTableName")
    public ResultGenerator getTableName(){
    List<DicDto> tableLs=recordMange.getTableName();
       return ResultGenerator.getSuccessResult(tableLs);
    }
    @Log("Get table column")
    @RequestMapping(value="/getTableColumn",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getTableColumn(@RequestBody String data)throws Exception {
        JSONObject json = new JSONObject(data);
        String tableId = json.getString("id");
        List<ColumnNestDTO> misColumnList=recordMange.getTableColumn(tableId);
        List<SectionNameColumnDetailDTO>  sectionColumnLs=new ArrayList<>();
        SectionNameColumnDetailDTO sectionColumnDTO=null;
        List<ColumnNestDTO> columnInputDTOList=null;
        List<SectionLableColumnDTO> sectionLableColumnDTOList=misPropertyConfigDetailDao.getSectionLableColumnList(tableId);
        for(SectionLableColumnDTO sectionLableColumnDTO: sectionLableColumnDTOList){
            if(!sectionColumnLs.stream().anyMatch(p->p.getName().equals(sectionLableColumnDTO.getMisSectionLabel()))){
                sectionColumnDTO=new SectionNameColumnDetailDTO();
                columnInputDTOList=new ArrayList<>();
                sectionColumnDTO.setName(sectionLableColumnDTO.getMisSectionLabel());
            }
            for(int i=0;i<misColumnList.size();i++){
                if(sectionLableColumnDTO.getMisPropertyConfigDetailColumnId().equals(misColumnList.get(i).getMisColumnId())){

                    columnInputDTOList.add(misColumnList.get(i));

                    sectionColumnDTO.setColumns(columnInputDTOList);
                }
            }
            sectionColumnLs.add(sectionColumnDTO);
        }
        List<SectionNameColumnDetailDTO> sectionColumnDTOS=sectionColumnLs.stream().distinct().collect(Collectors.toList());


        return ResultGenerator.getSuccessResult(sectionColumnDTOS);
    }

//    @Log("Insert table data.")
    @RequestMapping(value="/insertTableData",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator insertTableData(@RequestBody String data)throws Exception {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        AuditLogVO vo = recordMange.insertTable(data);
        if(vo != null){
            return ResultGenerator.getSuccessResult("添加数据成功!");
        }else{
            return ResultGenerator.getFailResult("添加数据失败!");
        }

    }

    @Log("calculate Column Query Result")
    @RequestMapping(value = "/calcColumnQueryResult")
    public ResultGenerator calcColumnQueryResult(@RequestBody CalcColumnQueryResultDTO dto) {
        return ResultGenerator.getSuccessResult(recordMange.calcColumnQueryResult(dto.getMisColumnId(),dto.getParam()));
    }
    @Log("Update table data.")
    @RequestMapping(value="/updateTableData",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateTableData(@RequestBody String data)throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json = new JSONObject(data);
        String tableId = json.getString("typeId");
        String editData=json.getString("editData");
        String createBy = ContextHolder.getUserId();
        Map maps = (Map) JSON.parse(editData);
        String setSQl="";
        String whereSql="";
        for (Object map : maps.entrySet()) {
            if("id".equals(((Map.Entry) map).getKey())){
                whereSql+=((Map.Entry) map).getKey() + "=" +'"'+((Map.Entry) map).getValue()+'"';
            }else{
                setSQl+=((Map.Entry) map).getKey() + "=" +'"'+ ((Map.Entry) map).getValue()+'"'+",";
            }
        }
        setSQl = setSQl.substring(0, setSQl.length() - 1);
        String tableName = recordMange.getTableNameById(tableId);
        List<MisGpTypePermission> misGpTypePermissionList= misGpPermissionDao.findByMisTypeIdAndCreateBy(tableId,createBy);
        if(misGpTypePermissionList.size()>0){
            if(ConstUtils.TYPEPERMISSIONTRUE.equals(misGpTypePermissionList.get(0).getDirectEdit())) {
                String updateSql = "update " + tableName + " set " + setSQl+" where "+whereSql;
                int returnUpdate=recordMange.updateTableData(updateSql);
                return ResultGenerator.getSuccessResult("1");
            }else{

                tableName = tableName.substring(0,tableName.lastIndexOf(ConstUtils.TYPEMAIN))+ConstUtils.TYPEDRAFT;
                String updateSql = "update " + tableName + " set " + setSQl+" where "+whereSql;
                int returnUpdate = recordMange.updateTableData(updateSql);
                return ResultGenerator.getSuccessResult("2");
            }

        }else{
            String updateSql = "update " + tableName + " set " + setSQl+" where "+whereSql;
            int returnUpdate = recordMange.updateTableData(updateSql);
            return ResultGenerator.getSuccessResult("2");
        }
    }
   /* @Log("Delete table data.")
    @RequestMapping(value="/deleteTableData",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteTableData(@RequestBody String data)throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json = new JSONObject(data);
        String typeId = json.getString("tableId");
        String recordId=json.getString("recordId");
        String createBy = ContextHolder.getUserId();
        String whereSql="";
        String tableName = recordMange.getTableNameById(typeId);
        List<MisGpTypePermission> misGpTypePermissionList= misGpPermissionDao.findByMisTypeIdAndCreateBy(typeId,createBy);
        if(misGpTypePermissionList.size()>0){
            whereSql="id="+'"'+recordId+'"';
                String updateSql = "delete from " + tableName + " where " + whereSql;
                int returnUpdate = recordMange.updateTableData(updateSql);
                return ResultGenerator.getSuccessResult("1");
                tableName = tableName.replace(ConstUtils.TYPEMAIN,ConstUtils.TYPEDRAFT);
                String updateDraftSql = "delete from " + tableName + " where " + whereSql;
                int returnDraftUpdate = recordMange.updateTableData(updateDraftSql);
                return ResultGenerator.getSuccessResult("2");

        }else{
            String updateSql = "delete from " + tableName + " where " + whereSql;
            int returnUpdate = recordMange.updateTableData(updateSql);
            return ResultGenerator.getSuccessResult("2");
        }
    }*/
    @Log("edit table data check permission.")
    @RequestMapping(value="/isHasPermission",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator isHasPermission(@RequestBody String data)throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json = new JSONObject(data);
        String typeId = json.getString("tableId");
        String recordId = json.getString("recordId");
        String editTableRef=json.getString("editTableRef");
        String userId = ContextHolder.getUserId();
       /* String workStatus ="";
        String workflowIdSql = "select wf_workflow_id from wf_workflow_attachment where wf_workflow_attachment_rec_id ='" + recordId + "' and wf_workflow_attachment_type_id='" + typeId + "'";
        javax.persistence.Query queryType = entityManager.createNativeQuery(workflowIdSql);
        List workflowIdLs = queryType.getResultList();
        if(workflowIdLs.size()>0) {
            for (int i = 0; i < workflowIdLs.size(); i++) {
                String workFlowId = workflowIdLs.get(i).toString();
                String workflowSql = "select wf_workflow_status from wf_workflow where wf_workflow_id ='" + workFlowId + "'";
                javax.persistence.Query queryWorkStatus = entityManager.createNativeQuery(workflowSql);
                List workflowStatus = queryWorkStatus.getResultList();
                workStatus = workflowStatus.get(0).toString();
            }
        }*/
            String columnId = "";
            List<MisPropertyConfig> misPropertyConfig = misPropertyConfigDao.getMisPropertyConfigsByMisPropertyTableId(typeId);
        /*    List<MisPropertyConfigDetail> misPropertyConfigDetailList = misPropertyConfigDetailDao.getMisPropertyConfigDetailsByMisPropertyId(misPropertyConfig.get(0).getMisPropertyId());
            if(misPropertyConfigDetailList.size()>0){
                for(int j=0;j<misPropertyConfigDetailList.size();j++){
                    String ab=misPropertyConfigDetailList.get(j).getMisPropertyConfigDetailColumnId();
                   columnId+=""+misPropertyConfigDetailList.get(j).getMisPropertyConfigDetailColumnId()+",";
                }
            }
            String replaceColumn=columnId.substring(0,columnId.lastIndexOf(",")-1);
            String columnSql="select mis_column_name from mis_column where mis_column_id in('"+replaceColumn+"')";
            javax.persistence.Query queryColumnName = entityManager.createNativeQuery(columnSql);
            List columnNameLs = queryColumnName.getResultList();*/



          /*  if("".equals(workStatus)){//此种情况不可能,记录编辑前都有记录创建流程

            }else if ("9".equals(workStatus)) {
                return ResultGenerator.getSuccessResult("one");//返回1表示无限制可做修改
            } else {//流程没有走完,视条件判断是否有权限处理
                if (misPropertyConfig.size() == 0) {
                    return ResultGenerator.getSuccessResult("one");
                } else if (misPropertyConfig.size() > 0 && (misPropertyConfig.get(0).getMisLockedBy() != null && misPropertyConfig.get(0).getMisLockedBy().equals(userId))) {
                    return ResultGenerator.getSuccessResult("two");
                } else if ("1".equals(misPropertyConfig.get(0).getMisLockedLevel())) {
                    return ResultGenerator.getSuccessResult("two");
                } else if ("2".equals(misPropertyConfig.get(0).getMisLockedLevel()) && misPropertyConfigDetailList.size() > 0) {
                    columnNameLs.contains(editTableRef);
                    return ResultGenerator.getSuccessResult("two");
                } else if ("3".equals(misPropertyConfig.get(0).getMisLockedLevel())) {
                    return ResultGenerator.getSuccessResult("two");
                }
            }*/

        if(misPropertyConfig.get(0).getMisLockedBy().equals(userId)){
            return ResultGenerator.getSuccessResult("one");
        }else if("p".equals(misPropertyConfig.get(0).getMisLockedLevel())){
            return ResultGenerator.getSuccessResult("two");
        }
        return ResultGenerator.getSuccessResult("two");
    }
    @PostMapping(value="/exportPDFContent")
    public void exportPDFContent(@RequestBody String data,  HttpServletResponse response) throws Exception {
        if (data == null || "".equals(data)) {
            //return ResultGenerator.getFailResult("参数为空!");
        }
        OutputStream out = null;
        JSONObject json = new JSONObject(data);
        String typeId=json.getString("typeId");
        String recordId=json.getString("recordId");
        String dataBuffer=exportContent2(typeId,recordId);
        //String fontPath =new SmbUtil().getWebInfPath()+"WEB-INF/classes/exportPdf/exportPDF.pdf";
        String upladPath="src\\main\\resources\\static\\exportPdf\\exportPDF.pdf";
        FileOutputStream fileOutputStream = new FileOutputStream(upladPath);
        fileOutputStream.write(convert(dataBuffer));
        new JSchUtil().put(upladPath, PropertiesUtil.getProperty("linux.upload.filepath"));
        byte[] buffer=new JSchUtil().download(PropertiesUtil.getProperty("linux.upload.file"),PropertiesUtil.getProperty("linux.upload.filepath"));
        response.setContentType("application/octet-stream");
        // 设置此项，在IE浏览器中下载Excel文件时可弹窗展示文件下载
        //response.setHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setHeader("Content-disposition","attachment; filename="+new String("exportPDF.pdf".getBytes("gb2312"), "ISO8859-1" )  );
        // 允许浏览器访问header中的FileName
        response.setHeader("Access-Control-Expose-Headers", "FileName");
        // 设置FileName，转码防止中文乱码
        response.setHeader("FileName", URLEncoder.encode(PropertiesUtil.getProperty("linux.upload.file"), "UTF-8"));
        response.getOutputStream().write(buffer);
        out = response.getOutputStream();
        out.close();
    }
    public static byte[] convert(String html) throws Exception {
        //此段代码只生成简单的pdf文件,不包含分页,页码,水纹等需单独开发
        com.itextpdf.io.source.ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //String fontPath =new SmbUtil().getWebInfPath()+"WEB-INF/classes/exportPdf/simhei.ttf";
        String fontPath =  "src\\main\\resources\\static\\exportPdf\\simhei.ttf";
        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider();
        FontProgram fontProgram;
        try {
            fontProgram = FontProgramFactory.createFont(fontPath);
            fontProvider.addFont(fontProgram);
            properties.setFontProvider(fontProvider);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HtmlConverter.convertToPdf(html, outputStream,  properties); // 无法灵活设置页边距等
        byte[] result = outputStream.toByteArray();
        outputStream.close();
        return result;
    }

    @PostMapping(value="/printTableData")
    public ResultGenerator printTableData(@RequestBody String data,  HttpServletResponse response)  {
        try {
            JSONObject json = new JSONObject(data);
            String typeId=json.getString("typeId");
            String recordId=json.getString("recordId");
            String dataBuffer=exportContent(typeId,recordId);
            //String fontPath =new SmbUtil().getWebInfPath()+"WEB-INF/classes/exportPdf/exportPDF.pdf";
            String upladPath="src\\main\\resources\\static\\exportPdf\\exportPDF.pdf";
            FileOutputStream fileOutputStream = new FileOutputStream(upladPath);
            fileOutputStream.write(convert(dataBuffer));
            File file = new File(upladPath);
            String printerName = ConstUtils.PRINTNAME;//打印机名包含字串
            pdfPrint(file,printerName);
        } catch (Exception e) {
            return ResultGenerator.getFailResult("print fail");
        }
           return ResultGenerator.getSuccessResult("print success");
    }

    @PostMapping(value="/getRelationData")
    public ResultGenerator getRelationData(@RequestBody String data)  throws Exception{

        JSONObject json = new JSONObject(data);
        String typeId=json.getString("tableId");
        String recordId=json.getString("recordId");
        String dataBuffer=exportContent(typeId,recordId);
        return ResultGenerator.getSuccessResult(dataBuffer);
    }
    public static void pdfPrint(File file ,String printerName) throws Exception {
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(file.getName());
            if (printerName != null) {
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if(printServices == null || printServices.length == 0) {
                    LOGGER.info("打印失败，未找到可用打印机，请检查。");
                    return ;
                }
                PrintService printService = null;
                for (int i = 0;i < printServices.length; i++) {
                    LOGGER.info(printServices[i].getName());
                    if (printServices[i].getName().contains(printerName)) {
                        printService = printServices[i];
                        break;
                    }
                }
                if(printService!=null){
                    printJob.setPrintService(printService);
                }else{
                    LOGGER.info("打印失败，未找到名称为" + printerName + "的打印机，请检查。");
                    return ;
                }
            }
            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.ACTUAL_SIZE);
            Book book = new Book();
            PageFormat pageFormat = new PageFormat();
            pageFormat.setOrientation(PageFormat.PORTRAIT);//纵向
            pageFormat.setPaper(getPaper());//设置纸张
            book.append(pdfPrintable, pageFormat, document.getNumberOfPages());
            printJob.setPageable(book);
            printJob.setCopies(1);//设置打印份数
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            pars.add(Sides.DUPLEX); //设置单双页
            printJob.print(pars);
        }finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static Paper getPaper() {
        Paper paper = new Paper();
        int width = 595;
        int height = 842;
        int marginLeft = 10;
        int marginRight = 0;
        int marginTop = 10;
        int marginBottom = 0;
        paper.setSize(width, height);
        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }

    public String exportContent(String typeId,String recordId){
        String columnSql="select CONCAT_WS(',', GROUP_CONCAT(mis_column_label))as columnName from MIS.mis_column mc where mc.mis_type_id ='"+typeId+"'";
        List columnList= entityManager.createNativeQuery(columnSql).getResultList();
        String[] columnArr=(columnList.get(0)).toString().split(",");
        MisType type = misTypeDao.findById(typeId).get();
        String querySql="select "+columnList.get(0)+" from "+type.getMisTypeName()+"_s where id='"+recordId+"'";
        List resultList= entityManager.createNativeQuery(querySql).getResultList();
        Map<String,String> columnMap=new HashMap();
        for(int i=0;i<resultList.size();i++){
            Object[]objArr= (Object[]) resultList.get(0);
            for(int ii=0;ii<objArr.length;ii++){
                columnMap.put(columnArr[ii], (String) objArr[ii]);
            }
        }
        String sectionSql="select mis_property_section_id,mis_section_label from MIS.mis_property_config mpc,MIS.mis_property_section mps where mpc.mis_property_id=mps.mis_property_id and mpc.mis_property_table_id ='"+typeId+"'";
        List sectionList= entityManager.createNativeQuery(sectionSql).getResultList();
        String columnNameSql="select mis_property_section_id,mis_column_label from (select mis_column_id ,mis_column_label from MIS.mis_column mc where mc.mis_type_id ='"+typeId+"')a,\n" +
                "(select mpcd.mis_property_section_id,mpcd.mis_property_config_detail_column_id from MIS.mis_property_config mpc,MIS.mis_property_config_detail mpcd where mpc.mis_property_id=mpcd.mis_property_id and mpc.mis_property_table_id ='"+typeId+"')b\n" +
                "where a.mis_column_id=b.mis_property_config_detail_column_id";
        List columnNameList= entityManager.createNativeQuery(columnNameSql).getResultList();
        StringBuffer htmlStringBuff=new StringBuffer("<table border='1px' width='100%' height='200px'>");
        for(int j=0;j<sectionList.size();j++){
            Object[] sectionArr= (Object[]) sectionList.get(j);
            String sectionId= (String) sectionArr[0];
            String sectionName= (String) sectionArr[1];
            String strHtml="";
            htmlStringBuff.append("<tr><td>"+sectionName+"</td></tr>");
            htmlStringBuff.append("<tr>");
            for(int h=0;h<columnNameList.size();h++){
                Object[] columnNameArr= (Object[]) columnNameList.get(h);
                if(sectionId.equals(columnNameArr[0])){
                    strHtml+="<td>"+columnNameArr[1]+":"+columnMap.get(columnNameArr[1])+"</td>";
                }
            }
            strHtml+="</tr>" ;
            htmlStringBuff.append(strHtml);
        }
        htmlStringBuff.append("</table>");

        String childSql="select mr.mis_child_type_id from MIS.mis_relation mr where mr.mis_parent_type_id ='"+typeId+"'";
        List childList= entityManager.createNativeQuery(childSql).getResultList();
        htmlStringBuff.append("<div class='wrap'>");
        for(int ch=0;ch<childList.size();ch++) {
            String misTypeId=childList.get(ch).toString();
            //String misTypeId="0266000000000002";
            //MisType type = misTypeDao.findById("0266000000000002").get();
            String childTable="";
            String recordSql = "select mrr.mis_child_rec_id from MIS.mis_relation_rec mrr where mrr.mis_relation_id in(select mr.mis_relation_id  from MIS.mis_relation mr where mr.mis_child_type_id ='"+misTypeId+"')";
            List recordList = entityManager.createNativeQuery(recordSql).getResultList();
            MisType childType = misTypeDao.findById(misTypeId).get();
            htmlStringBuff.append("<label><span>"+childType.getMisTypeName()+"_s </span><input type='radio' name='tab' checked>");
            //htmlStringBuff.append("<tr><td>"+childType.getMisTypeName()+"_s</td></tr>");
            recordList.stream().forEach((num) -> {
                String childColumnSql="select CONCAT_WS(',', GROUP_CONCAT(mis_column_label))as columnName from MIS.mis_column mc where mc.mis_type_id ='"+misTypeId+"'";
                List childColumnList= entityManager.createNativeQuery(childColumnSql).getResultList();
                String[] childColumnArr=(childColumnList.get(0)).toString().split(",");
                String singleSql = "select "+childColumnList.get(0)+" from  "+childType.getMisTypeName()+"_s where id='" + num + "'";
                List singleList = entityManager.createNativeQuery(singleSql).getResultList();
                String strHtml="<tr>";
                for (int h = 0; h < singleList.size(); h++) {
                    Object[] singleArr = (Object[]) singleList.get(h);
                    for(int sin=0;sin<singleArr.length;sin++) {
                        strHtml += "<td>" + childColumnArr[sin] + ":" + singleArr[sin] + "</td>&nbsp;&nbsp;";
                    }
                    strHtml += "</tr></table>";
                }
                htmlStringBuff.append("<div>"+strHtml+"</div></label>");
            });

        }
        htmlStringBuff.append("<style type=\"text/css\">\n" +
                "        *{margin: 0;padding: 0;}\n" +
                "        .wrap{\n" +
                "            margin: 20px auto;\n" +
                "            width: 403px;\n" +
                "            height: 600px;\n" +
                "            border:1px solid brown;\n" +
                "            position: relative;\n" +
                "        }\n" +
                "        label{\n" +
                "            width: 140px;\n" +
                "            height: 30px;\n" +
                "            float: left;\n" +
                "            text-align: center;\n" +
                "            line-height:30px;\n" +
                "            border-right: 1px solid brown;\n" +
                "            border-bottom: 1px solid brown;\n" +
                "        }\n" +
                "        label:nth-of-type(4){\n" +
                "            border-right: none;\n" +
                "        }\n" +
                "        label span{\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "        label div{\n" +
                "            width: 403px;\n" +
                "            height: 568px;\n" +
                "            position: absolute;\n" +
                "            left: 0;\n" +
                "            top: 31px;\n" +
                "            background: #eeeeee;\n" +
                "            display: none;\n" +
                "        }\n" +
                "        label input{\n" +
                "            width: 0;\n" +
                "        }\n" +
                "        input:checked+div{\n" +
                "            display: block;\n" +
                "        }\n" +
                "    </style>");
        return htmlStringBuff.toString();
    }
    public String exportContent2(String typeId,String recordId){
        String columnSql="select CONCAT_WS(',', GROUP_CONCAT(mis_column_label))as columnName from MIS.mis_column mc where mc.mis_type_id ='"+typeId+"'";
        List columnList= entityManager.createNativeQuery(columnSql).getResultList();
        String[] columnArr=(columnList.get(0)).toString().split(",");
        MisType type = misTypeDao.findById(typeId).get();
        String querySql="select "+columnList.get(0)+" from "+type.getMisTypeName()+"_s where id='"+recordId+"'";
        List resultList= entityManager.createNativeQuery(querySql).getResultList();
        Map<String,String> columnMap=new HashMap();
        for(int i=0;i<resultList.size();i++){
            Object[]objArr= (Object[]) resultList.get(0);
            for(int ii=0;ii<objArr.length;ii++){
                columnMap.put(columnArr[ii], (String) objArr[ii]);
            }
        }
        String sectionSql="select mis_property_section_id,mis_section_label from MIS.mis_property_config mpc,MIS.mis_property_section mps where mpc.mis_property_id=mps.mis_property_id and mpc.mis_property_table_id ='"+typeId+"'";
        List sectionList= entityManager.createNativeQuery(sectionSql).getResultList();
        String columnNameSql="select mis_property_section_id,mis_column_label from (select mis_column_id ,mis_column_label from MIS.mis_column mc where mc.mis_type_id ='"+typeId+"')a,\n" +
                "(select mpcd.mis_property_section_id,mpcd.mis_property_config_detail_column_id from MIS.mis_property_config mpc,MIS.mis_property_config_detail mpcd where mpc.mis_property_id=mpcd.mis_property_id and mpc.mis_property_table_id ='"+typeId+"')b\n" +
                "where a.mis_column_id=b.mis_property_config_detail_column_id";
        List columnNameList= entityManager.createNativeQuery(columnNameSql).getResultList();
        StringBuffer htmlStringBuff=new StringBuffer("<table border='1px' width='100%' height='200px'>");
        for(int j=0;j<sectionList.size();j++){
            Object[] sectionArr= (Object[]) sectionList.get(j);
            String sectionId= (String) sectionArr[0];
            String sectionName= (String) sectionArr[1];
            String strHtml="";
            htmlStringBuff.append("<tr><td>"+sectionName+"</td></tr>");
            htmlStringBuff.append("<tr>");
            for(int h=0;h<columnNameList.size();h++){
                Object[] columnNameArr= (Object[]) columnNameList.get(h);
                if(sectionId.equals(columnNameArr[0])){
                    strHtml+="<td>"+columnNameArr[1]+":"+columnMap.get(columnNameArr[1])+"</td>";
                }
            }
            strHtml+="</tr>" ;
            htmlStringBuff.append(strHtml);
        }
        htmlStringBuff.append("</table>");

        String childSql="select mr.mis_child_type_id from MIS.mis_relation mr where mr.mis_parent_type_id ='"+typeId+"'";
        List childList= entityManager.createNativeQuery(childSql).getResultList();
        htmlStringBuff.append("<div class='wrap'>");
        for(int ch=0;ch<childList.size();ch++) {
            String misTypeId=childList.get(ch).toString();
            //String misTypeId="0266000000000002";
            //MisType type = misTypeDao.findById("0266000000000002").get();
            String childTable="";
            String recordSql = "select mrr.mis_child_rec_id from MIS.mis_relation_rec mrr where mrr.mis_relation_id in(select mr.mis_relation_id  from MIS.mis_relation mr where mr.mis_child_type_id ='"+misTypeId+"')";
            List recordList = entityManager.createNativeQuery(recordSql).getResultList();
            MisType childType = misTypeDao.findById(misTypeId).get();
            htmlStringBuff.append("<span>"+childType.getMisTypeName()+"_s </span><input type='radio' name='tab' checked>");
            //htmlStringBuff.append("<tr><td>"+childType.getMisTypeName()+"_s</td></tr>");
            recordList.stream().forEach((num) -> {
                String childColumnSql="select CONCAT_WS(',', GROUP_CONCAT(mis_column_label))as columnName from MIS.mis_column mc where mc.mis_type_id ='"+misTypeId+"'";
                List childColumnList= entityManager.createNativeQuery(childColumnSql).getResultList();
                String[] childColumnArr=(childColumnList.get(0)).toString().split(",");
                String singleSql = "select "+childColumnList.get(0)+" from  "+childType.getMisTypeName()+"_s where id='" + num + "'";
                List singleList = entityManager.createNativeQuery(singleSql).getResultList();
                String strHtml="<tr>";
                for (int h = 0; h < singleList.size(); h++) {
                    Object[] singleArr = (Object[]) singleList.get(h);
                    for(int sin=0;sin<singleArr.length;sin++) {
                        strHtml += "<td>" + childColumnArr[sin] + ":" + singleArr[sin] + "</td>&nbsp;&nbsp;";
                    }
                    strHtml += "</tr></table>";
                }
                htmlStringBuff.append("<div>"+strHtml+"</div>");
            });

        }
        htmlStringBuff.append("<style type=\"text/css\">\n" +
                "        *{margin: 0;padding: 0;}\n" +
                "        .wrap{\n" +
                "            margin: 20px auto;\n" +
                "            width: 403px;\n" +
                "            height: 600px;\n" +
                "            border:1px solid brown;\n" +
                "            position: relative;\n" +
                "        }\n" +
                "        label{\n" +
                "            width: 140px;\n" +
                "            height: 30px;\n" +
                "            float: left;\n" +
                "            text-align: center;\n" +
                "            line-height:30px;\n" +
                "            border-right: 1px solid brown;\n" +
                "            border-bottom: 1px solid brown;\n" +
                "        }\n" +
                "        label:nth-of-type(4){\n" +
                "            border-right: none;\n" +
                "        }\n" +
                "        label span{\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "        label div{\n" +
                "            width: 403px;\n" +
                "            height: 568px;\n" +
                "            position: absolute;\n" +
                "            left: 0;\n" +
                "            top: 31px;\n" +
                "            background: #eeeeee;\n" +
                "            display: none;\n" +
                "        }\n" +
                "        label input{\n" +
                "            width: 0;\n" +
                "        }\n" +
                "        input:checked+div{\n" +
                "            display: block;\n" +
                "        }\n" +
                "    </style>");
        return htmlStringBuff.toString();
    }
    @Log("Update table record data.")
    @RequestMapping(value="/updateTypeRecord",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator updateTypeRecord(@RequestBody String data)throws Exception {
        if (data == null || "".equals(data)) {
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json = new JSONObject(data);
        String tableId = json.getString("tableId");
        String recordId=json.getString("recordId");
        String processContent = json.getString("processContent");
        String processDate=json.getString("processDate");
        String tableName = recordMange.getTableNameById(tableId);
        String updateSql="update "+tableName+" set reason='"+processContent+"' , submitDate='"+processDate+"' where id='"+recordId+"'";
        int updateResult=recordMange.updateTableData(updateSql);
    return ResultGenerator.getSuccessResult(updateResult);
    }
}
