package com.asl.prd004.utils;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.*;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportProcess {
    /** 設置字段寬度 */
    private final static int textWidth = 100;
    /** 設置字段高度 */
    private final static int textHeight = 20;
    /** coulumnHeader區域字體大小 */
    private final static float columnHeaderfontSize = 14;
    /** detail 區域字體大小 */
    private final static float fontSize = 12;
    /** 設置間距 */
    private final static int X = 100;
    /** coulumnHeader區域高度 */
    private final static int columnHeaderHeight = 20;
    /** detail 區域高度 */
    private final static int detailHeight = 20;
    /**  */
    private static String aliasColumn = "column";

    public static void main(String args[]){
        try {
            String[] columns = {"a", "b", "c", "d"};
            File f = new File("d://test.pdf");
            List<Object[]> list = new ArrayList<Object[]>();
            Object[] obj = null;
            for(int j=0;j<50;j++){
                obj = new Object[columns.length];
                for(int i=0;i<columns.length;i++){
                    obj[i] = columns[i]+j+","+i;
                }
                list.add(obj);
            }
            OutputStream out = new FileOutputStream(f);
            //preview(columns, list, out);
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * PDF打印
     *
     * @param headers
     *            colimnHeaders
     * @param list
     *            數據來源
     * @param outputStream
     *            輸出流
     * @throws Exception
     */
    public static String preview(String headers[], List<Object[]> list,
                                 OutputStream outputStream,JasperDesign jasperDesign,String format) throws Exception {
        String[] alias = preaseAliasColumnHeaders(headers);
        JasperReport jp = getJasperReport(headers, alias,jasperDesign);
        Map<String, Object> parameters = new HashMap<String,Object>();
        JasperPrint print = JasperFillManager.fillReport(jp, parameters,
                new JRBeanCollectionDataSource(getBaseList(alias, phrase(list))));
        //JasperExportManager.exportReportToPdfStream(jasperPrint, out);
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
            exporter.exportReport();
        }
        return "report/"+fileName;
    }
    public static List<String[]> phrase(List<Object[]> list){
        List<String[]> temps = new ArrayList<String[]>();
        String []s= null;
        for(Object[] obj : list){
            s = new String[obj.length];
            for(int i = 0;i<obj.length;i++){
                s[i] = obj[i].toString();
            }
            temps.add(s);
        }
        return temps;
    }

    /**
     * 產生columnHeaders的別名(Headers[]有可能為中文，所以有必要用別名來替換)
     *
     * @param headers
     * @return
     */
    private static String[] preaseAliasColumnHeaders(String headers[]) {
        int size = headers.length;
        String[] alias = new String[size];
        for (int i = 0; i < size; i++) {
            alias[i] = aliasColumn + i;
        }
        return alias;
    }

    /**
     * 產生Template文件
     *
     * @param headers
     * @param alias
     * @return
     * @throws JRException
     */
    @SuppressWarnings("deprecation")
    private static JasperReport getJasperReport(String[] headers,
                                                String alias[],JasperDesign design) throws JRException {
        // name="statistics"
        design.setName("statistics");
        // columnCount="1"
        // printOrder="Vertical"
        design.setPrintOrder(PrintOrderEnum.VERTICAL);
        // orientation="Portrait"
        design.setOrientation(OrientationEnum.PORTRAIT);
        // pageWidth="595"
        design.setPageWidth(595);
        // pageHeight="842"
        design.setPageHeight(842);
        // columnWidth="535"
        design.setColumnWidth(535);
        // columnSpacing="0"
        design.setColumnSpacing(0);
        // leftMargin="30"
        design.setLeftMargin(30);
        // rightMargin="30"
        design.setRightMargin(30);
        // topMargin="20"
        design.setTopMargin(20);
        // bottomMargin="20"
        design.setBottomMargin(20);
        // whenNoDataType="NoPages"
        design.setWhenNoDataType(WhenNoDataTypeEnum.NO_PAGES);
        // isTitleNewPage="false"
        design.setTitleNewPage(false);
        // isSummaryNewPage="false"
        design.setSummaryNewPage(false);

        // JRDesignBand title = new JRDesignBand();
        // title.setHeight(50);
        // JRDesignStaticText titleText = new JRDesignStaticText();
        // titleText.setText("test report");
        // titleText.setX(230);
        // titleText.setFontSize(20);
        // titleText.setHeight(50);
        // titleText.setWidth(100);
        // title.addElement(titleText);
        // design.setTitle(title);
        JRDesignBand columnHeader = new JRDesignBand();
        columnHeader.setHeight(columnHeaderHeight);

        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(detailHeight);

        for (int i = 0; i < headers.length; i++) {
            // add column headers
            JRDesignStaticText staticText = new JRDesignStaticText();
            staticText.setText(headers[i]);
            staticText.setFontSize(columnHeaderfontSize);
            staticText.setHeight(textHeight);
            staticText.setWidth(textWidth);
            staticText.setX(X * i);
            staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            staticText.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
//            staticText.getLineBox().getLeftPen().setLineWidth(1);
//            staticText.getLineBox().getRightPen().setLineWidth(1);
//            staticText.getLineBox().getTopPen().setLineWidth(1);
//            staticText.getLineBox().getBottomPen().setLineWidth(1);
            staticText.setPdfEmbedded(true);
            columnHeader.addElement(staticText);

            // define fields
            JRDesignField field = new JRDesignField();
            field.setName(alias[i]);
            field.setValueClass(String.class);
            design.addField(field);

            // add text fields for displaying fields
            JRDesignTextField textField = new JRDesignTextField();
            JRDesignExpression expression = new JRDesignExpression();
            expression.setText("$F{" + alias[i] + "}");
            expression.setValueClass(String.class);
            textField.setExpression(expression);
            textField.setFontSize(fontSize);
            textField.setHeight(textHeight);
            textField.setWidth(textWidth);
            textField.setX(X * i);
            textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            textField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
//            textField.getLineBox().getLeftPen().setLineWidth(1);
//            textField.getLineBox().getRightPen().setLineWidth(1);
//            textField.getLineBox().getTopPen().setLineWidth(1);
//            textField.getLineBox().getBottomPen().setLineWidth(1);
            textField.setPdfEmbedded(true);
            detail.addElement(textField);
        }
        design.setColumnHeader(columnHeader);
        ((JRDesignSection)design.getDetailSection()).addBand(detail);

        return JasperCompileManager.compileReport(design);
    }

    /**
     * 利用反射機制，裝拼數據
     *
     * @param headers
     * @param list
     * @return
     * @throws Exception
     */
    private static List<Object> getBaseList(String[] headers,
                                            List<String[]> list) throws Exception {
        List<Object> result = new ArrayList<Object>();
        int length = headers.length;
        DynaProperty[] dynaProps = new DynaProperty[length];
        for (int i = 0; i < length; i++) {
            dynaProps[i] = new DynaProperty(headers[i], String.class);
        }
        BasicDynaClass dynaClass = new BasicDynaClass("first",
                BasicDynaBean.class, dynaProps);
        for (Object[] obj : list) {
            DynaBean employee = dynaClass.newInstance();
            for (int i = 0; i < length; i++) {
                employee.set(headers[i], obj[i]);
            }
            result.add(employee);
        }
        return result;
    }
}
