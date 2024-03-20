package com.asl.prd004.cron;

import com.asl.prd004.dao.BiToolDao;
import com.asl.prd004.dao.MisColumnDao;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.entity.BiToolConfig;
import com.asl.prd004.entity.MisColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author billy
 * @version 1.0
 * @description: 定时每天晚上12点统日bi tool配置表对应表统计数据
 * @date 2023/6/28 17:30
 */

@Component
public class BiToolCountTimer {
    @Autowired
    private BiToolDao biToolDao;
    @Autowired
    private MisTypeDao misTypeDao;
    @Autowired
    private MisColumnDao misColumnDao;
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(BiToolCountTimer.class);
    @Transactional
    //@Scheduled(cron = "0 * * * * ?")
    @Scheduled(cron = "0 0 12 * * ?")
    public void BiToolCountTimer(){
        SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdfData.format(new Date(System.currentTimeMillis()));
        try {
            List<BiToolConfig> biTool=biToolDao.findAll();
            if(biTool.size()>0){
                for(int i=0;i<biTool.size();i++){
                    String tableId=biTool.get(i).getMisBiConfigTypeId();
                    if(tableId.equals("mis_workflow")){
                        continue;
                    }
                    String columnId=biTool.get(i).getMisBiConfigColumnHor();
                    String columnName = misColumnDao.findByMisColumnId(columnId).getMisColumnName();
                    String tableName=misTypeDao.getTableNameById(tableId);
                    List<MisColumn> columnList=misColumnDao.findByMisTypeIdAndMisColumnName(tableId,columnName);
                    String querySql="select "+columnName+" as columnName,count("+columnName+")as countData from "+tableName +" group by "+columnName;
                    javax.persistence.Query query= entityManager.createNativeQuery(querySql);
                    List executeResult=query.getResultList();
                    Object[] ints = executeResult.toArray();
                    if(executeResult.size()>0){
                        for(int j=0;j<executeResult.size();j++) {
                            Object[] dataIns= (Object[]) ints[j];
                            Object columnValue=dataIns[0];
                            String columnValue1 =columnValue!=null?columnValue.toString():"";
                            Object countData=dataIns[1];
                            String countData1 =countData.toString();
                            LOGGER.info("countData1===" + countData1);
                            int af = Integer.parseInt(countData1);
                            String insertSql = "insert into mis_bi_tool_count" + "(table_id,column_id,date,column_name,column_value,count_data)" + " values(" + '"' + tableId + '"' + "," + '"' + columnList.get(0).getMisColumnId() + '"' + "," + '"' + currentDate + '"' + ","+ '"' + columnName + '"'+","+'"' + columnValue1 + '"' + "," + af + " )";
                            javax.persistence.Query insertSqlExcu = entityManager.createNativeQuery(insertSql);
                            insertSqlExcu.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    //@Scheduled(cron = "0 * * * * ?")
    @Scheduled(cron = "0 0 12 * * ?")
    public void BiToolCountWorkflowTimer(){
        SimpleDateFormat sdfData=new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdfData.format(new Date(System.currentTimeMillis()));
        try {
            List<BiToolConfig> biTool=biToolDao.findAll();
            if(biTool.size()>0){
                for(int i=0;i<biTool.size();i++){
                    String querySql="select wf_workflow_status as columnName, count('wf_workflow_status') as countData from wf_workflow group by wf_workflow.wf_workflow_status";
                    javax.persistence.Query query= entityManager.createNativeQuery(querySql);
                    List executeResult=query.getResultList();
                    Object[] ints = executeResult.toArray();
                    if(executeResult.size()>0){
                        for(int j=0;j<executeResult.size();j++) {
                            Object[] dataIns= (Object[]) ints[j];
                            Object columnValue=dataIns[0];
                            String columnValue1 =columnValue!=null?columnValue.toString():"";
                            Object countData=dataIns[1];
                            String countData1 =countData.toString();
                            int af = Integer.parseInt(countData1);
                            String insertSql = "insert into mis_bi_tool_count" + "(table_id,column_id,date,column_name,column_value,count_data)" + " values('wf_workflow',''," + '"' + currentDate + '"' + ",'wf_workflow_status'," +'"' + columnValue1 + '"' + "," + af + " )";
                            javax.persistence.Query insertSqlExcu = entityManager.createNativeQuery(insertSql);
                            insertSqlExcu.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}


