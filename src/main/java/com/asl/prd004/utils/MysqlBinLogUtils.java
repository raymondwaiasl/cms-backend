package com.asl.prd004.utils;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.service.IRecordService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

/**
 * @author billy
 * @version 1.0
 * @description: 监听mysql表二进制日志变动程序，主要针对增删改
 * @date 2023/6/20 9:28
 */
@Component
public class MysqlBinLogUtils /*implements ApplicationRunner*/ {
    @Autowired
    EntityManager entityManager;
    @Autowired
    IRecordService iRecordService;
    @Autowired
    MisTypeDao misTypeDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlBinLogUtils.class);
    String tableName="";

   /* public void run(ApplicationArguments args) throws Exception {
        //项目启动后开始监听mysql操作日志方法
        new Thread(() -> {
            connectMysqlBinLog();
        }).start();

    }

    public void connectMysqlBinLog() {
        LOGGER.info("监控BinLog服务已启动");

        //自己MySQL的信息。host，port，username，password
        BinaryLogClient client = new BinaryLogClient(PropertiesUtil.getProperty("linux.host"), Integer.valueOf(PropertiesUtil.getProperty("mysql.port")), PropertiesUtil.getProperty("linux.userName"), PropertiesUtil.getProperty("linux.password"));
        client.setServerId(100);
        client.registerEventListener(event -> {
            EventData data = event.getData();
            if (data instanceof TableMapEventData) {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                //可以通过转成TableMapEventData类实例的tableMapEventData来获取当前发生变更的数据库
                tableName=tableMapEventData.getTable();
            }
            if(tableName!="") {

                List<MisType> misTypeList = misTypeDao.getMisTypeByMisTypeName(tableName.substring(0,tableName.lastIndexOf("_")));
                if (!tableName.contains("_log") && (misTypeList != null && misTypeList.size() > 0 && (misTypeList.get(0).getMisTypeName()+"_s").equals(tableName))) {
                    //表数据发生修改时触发
                    if (data instanceof UpdateRowsEventData) {
                        List<Object> beforeObjectList = null;
                        List<Object> afterObjectList = null;
                        String creationDate="";
                        String uuId="";
                        StringBuffer updateSql = new StringBuffer();
                        StringBuffer insertSql = new StringBuffer();
                        StringBuffer columnData = new StringBuffer();
                        for (Map.Entry<Serializable[], Serializable[]> row : ((UpdateRowsEventData) data).getRows()) {
                            // 修改之前的数据
                            beforeObjectList = (List<Object>) JSONObject.toJSON(row.getKey());
                            // 修改之后的数据
                            afterObjectList = (List<Object>) JSONObject.toJSON(row.getValue());
                        }
                        String columnNames = "select column_name,data_type from information_schema.columns where table_schema ='MIS' and table_name ='" + tableName + "'";
                        List<String> res = entityManager.createNativeQuery(columnNames).getResultList();
                        Object[] ints = res.toArray();
                        for (int i = 0; i < ints.length; i++) {
                           Object[] dataType= (Object[]) ints[i];
                            updateSql = ConvertDataType(updateSql,  afterObjectList.get(i), dataType[1]);
                            insertSql = ConvertDataTypeByUpdate(insertSql,  afterObjectList.get(i),dataType[0], dataType[1]);
                            if((!dataType[0].equals("creation_date"))&&(!dataType[0].equals("updated_date"))){
                                columnData.append(dataType[0]).append(",");
                            }

                            if(dataType[0].equals("creation_date")){
                                SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd");
                                creationDate= sdfData.format(afterObjectList.get(i));
                                Object[] dataType1= (Object[]) ints[0];
                                uuId= (String) dataType1[0];

                            }
                        }
                        SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = sdfData.format(new Date(System.currentTimeMillis()));
                        String queryDateData="select count(1) from "+tableName+"_log"+" where date_format(creation_date, '%Y-%m-%d')="+'"'+currentDate+'"';
                        int dataCount=iRecordService.queryDataCount(queryDateData);
                        if(dataCount>0){//今天有数据同步,只需数据变更
                            String exeSql = "update  " + tableName + "_log "+" set " + insertSql.substring(0, insertSql.length() - 1) +" where "+uuId+"="+'"'+afterObjectList.get(0)+'"';
                            iRecordService.addData(exeSql);
                        }else{//当天没有数据,需把昨日数据同步,并把最新变更数据插入进来
                            SimpleDateFormat currentTime=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String currentDate1 = currentTime.format(new Date(System.currentTimeMillis()));
                            String exeSql="insert into "+tableName + "_log "+"("+columnData.substring(0, columnData.length() - 1)+",creation_date,updated_date"+")"+" select "+columnData.substring(0, columnData.length() - 1)+","+'"'+currentDate1+'"'+","+'"'+currentDate1+'"'+" from "+tableName + " where date_format(creation_date, '%Y-%m-%d')=DATE_SUB(CURDATE(),INTERVAL 1 day)";
                            iRecordService.addData(exeSql);
                            String exeSql1 = "insert into  " + tableName + "_log values(" + updateSql.substring(0, updateSql.length() - 1) + " ) ";
                            iRecordService.addData(exeSql1);
                        }
                        //String exeSql = "insert into  " + tableName + "_log values(" + updateSql.substring(0, updateSql.length() - 1) + " ) ";
                        //iRecordService.addData(exeSql);
                        //表数据发生插入时触发
                    } else if (data instanceof WriteRowsEventData) {
                        List<Object> objectList = null;
                        StringBuffer updateSql = new StringBuffer();
                        StringBuffer insertSql = new StringBuffer();
                        StringBuffer columnData = new StringBuffer();
                        String creationDate="";
                        String uuId="";
                        for (Serializable[] row : ((WriteRowsEventData) data).getRows()) {
                            objectList = (List<Object>) JSONObject.toJSON(row);
                        }
                        String columnNames = "select column_name,data_type from information_schema.columns where table_schema ='MIS' and table_name ='" + tableName + "'";
                        List<String> res = entityManager.createNativeQuery(columnNames).getResultList();
                        Object[] ints = res.toArray();
                        for (int i = 0; i < ints.length; i++) {
                            Object[] dataType= (Object[]) ints[i];
                            updateSql = ConvertDataType(updateSql, objectList.get(i), dataType[1]);

                        insertSql = ConvertDataTypeByUpdate(insertSql,  objectList.get(i),dataType[0], dataType[1]);
                        if((!dataType[0].equals("creation_date"))&&(!dataType[0].equals("updated_date"))){
                            columnData.append(dataType[0]).append(",");
                        }

                        if(dataType[0].equals("creation_date")){
                            SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd");
                            creationDate= sdfData.format(objectList.get(i));
                            Object[] dataType1= (Object[]) ints[0];
                            uuId= (String) dataType1[0];

                        }
                    }
                    SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = sdfData.format(new Date(System.currentTimeMillis()));
                    String queryDateData="select count(1) from "+tableName+"_log"+" where date_format(creation_date, '%Y-%m-%d')="+'"'+currentDate+'"';
                    int dataCount=iRecordService.queryDataCount(queryDateData);
                    if(dataCount>0){//今天有数据同步,只需数据变更
                        String exeSql = "insert into  " + tableName + "_log values(" + updateSql.substring(0, updateSql.length() - 1) + " ) ";
                        iRecordService.addData(exeSql);
                    }else{//当天没有数据,需把昨日数据同步,并把最新变更数据插入进来
                        SimpleDateFormat currentTime=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String currentDate1 = currentTime.format(new Date(System.currentTimeMillis()));
                        String exeSql="insert into "+tableName + "_log "+"("+columnData.substring(0, columnData.length() - 1)+",creation_date,updated_date"+")"+" select "+columnData.substring(0, columnData.length() - 1)+","+'"'+currentDate1+'"'+","+'"'+currentDate1+'"'+" from "+tableName + " where date_format(creation_date, '%Y-%m-%d')=DATE_SUB(CURDATE(),INTERVAL 1 day)";
                        iRecordService.addData(exeSql);
                        String exeSql1 = "insert into  " + tableName + "_log values(" + updateSql.substring(0, updateSql.length() - 1) + " ) ";
                        iRecordService.addData(exeSql1);
                    }
                        //表数据发生删除后触发
                    } else if (data instanceof DeleteRowsEventData) {
                        List<Object> objectList = null;
                        StringBuffer updateSql = new StringBuffer();
                        StringBuffer insertSql = new StringBuffer();
                        StringBuffer columnData = new StringBuffer();
                        String creationDate="";
                        String uuId="";
                        String columnNames = "select column_name,data_type from information_schema.columns where table_schema ='MIS' and table_name ='" + tableName + "'";
                        List<String> res = entityManager.createNativeQuery(columnNames).getResultList();
                        for (Serializable[] row : ((DeleteRowsEventData) data).getRows()) {
                            objectList = (List<Object>) JSONObject.toJSON(row);
                        }
                        Object[] ints = res.toArray();
                        for (int i = 0; i < ints.length; i++) {
                            Object[] dataType= (Object[]) ints[i];
                            updateSql = ConvertDataType(updateSql, objectList.get(i), dataType[1]);
                            //insertSql = ConvertDataTypeByUpdate(insertSql,  objectList.get(i),dataType[0], dataType[1]);
                            if((!dataType[0].equals("creation_date"))&&(!dataType[0].equals("updated_date"))){
                                columnData.append(dataType[0]).append(",");
                            }

                            if(dataType[0].equals("creation_date")){
                                SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd");
                                creationDate= sdfData.format(objectList.get(i));
                                Object[] dataType1= (Object[]) ints[0];
                                uuId= (String) dataType1[0];

                            }
                        }
                        SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = sdfData.format(new Date(System.currentTimeMillis()));
                        String queryDateData="select count(1) from "+tableName+"_log"+" where date_format(creation_date, '%Y-%m-%d')="+'"'+currentDate+'"';
                        int dataCount=iRecordService.queryDataCount(queryDateData);
                        if(dataCount>0){//今天有数据同步,只需数据变更
                            String exeSql = "delete from   " + tableName + "_log" + " where " + res.get(0) + "=" + "'" + objectList.get(0) + "'";
                            iRecordService.addData(exeSql);
                        }else{//当天没有数据,需把昨日数据同步,并把最新变更数据插入进来
                            SimpleDateFormat currentTime=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String currentDate1 = currentTime.format(new Date(System.currentTimeMillis()));
                            String exeSql="insert into "+tableName + "_log "+"("+columnData.substring(0, columnData.length() - 1)+",creation_date,updated_date"+")"+" select "+columnData.substring(0, columnData.length() - 1)+","+'"'+currentDate1+'"'+","+'"'+currentDate1+'"'+" from "+tableName + " where date_format(creation_date, '%Y-%m-%d')=DATE_SUB(CURDATE(),INTERVAL 1 day)";
                            iRecordService.addData(exeSql);
                            String exeSql1 = "delete from   " + tableName + "_log" + " where " + res.get(0) + "=" + "'" + objectList.get(0) + "'";
                            iRecordService.addData(exeSql1);
                        }
                       *//* String exeSql = "delete from   " + tableName + "_log" + " where " + res.get(0) + "=" + "'" + objectList.get(0) + "'";
                        iRecordService.addData(exeSql);*//*
                    }
                }
            }
        });

        try {
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public StringBuffer ConvertDataType(StringBuffer insertSql,Object dataArr1,Object dataArr2){

        if(dataArr2.toString().equals("varchar")||dataArr2.toString().equals("char")){
            insertSql.append("\"" + dataArr1 + "\"").append(",");
        }else if(dataArr2.toString().equals("datetime")){
            SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s3 = sdfData.format(dataArr1);
            insertSql.append("\"" + s3 + "\"").append(",");
        }else if(dataArr2.equals("int")||dataArr2.equals("integer")){
            Integer intData= (Integer) dataArr1;
            insertSql.append(intData).append(",");
        }else if(dataArr2.equals("long")){
            Long intData= (Long) dataArr1;
            insertSql.append(intData).append(",");
        }else if(dataArr2.equals("float")){
            Float intData=(Float)dataArr1;
            insertSql.append(intData).append(",");
        }else if(dataArr2.equals("double")){
            Double intData=(Double)dataArr1;
            insertSql.append(intData).append(",");
        }else if(dataArr2.equals("bigDecimal")){
            BigDecimal intData= (BigDecimal)dataArr1;
            insertSql.append(intData).append(",");
        }else{
            insertSql.append("\"" + dataArr1 + "\"").append(",");
        }
        return insertSql;
    }
    public StringBuffer ConvertDataTypeByUpdate(StringBuffer updateSql,Object dataArr1,Object dataArr0,Object dataArr2){

        if(dataArr2.toString().equals("varchar")||dataArr2.toString().equals("char")){
            updateSql.append( dataArr0 +" =").append("\"" + dataArr1 + "\"").append(",");
        }else if(dataArr2.toString().equals("datetime")){
            SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s3 = sdfData.format(dataArr1);
            updateSql.append( dataArr0 +" =").append("\"" + s3 + "\"").append(",");
        }else if(dataArr2.equals("int")||dataArr2.equals("integer")){
            Integer intData= (Integer) dataArr1;
            updateSql.append(dataArr0+" =").append(intData).append(",");
        }else if(dataArr2.equals("long")){
            Long intData= (Long) dataArr1;
            updateSql.append( dataArr0 +" =").append(intData).append(",");
        }else if(dataArr2.equals("float")){
            Float intData=(Float)dataArr1;
            updateSql.append(dataArr0+" =").append(intData).append(",");
        }else if(dataArr2.equals("double")){
            Double intData=(Double)dataArr1;
            updateSql.append(dataArr0 +" =").append(intData).append(",");
        }else if(dataArr2.equals("bigDecimal")){
            BigDecimal intData= (BigDecimal)dataArr1;
            updateSql.append(dataArr0+" =").append(intData).append(",");
        }else{
            updateSql.append(dataArr0+" =").append("\"" + dataArr1 + "\"").append(",");
        }
        return updateSql;
    }*/

}


