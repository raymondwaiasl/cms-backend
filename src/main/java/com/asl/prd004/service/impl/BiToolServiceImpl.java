package com.asl.prd004.service.impl;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.BiToolDao;
import com.asl.prd004.dao.MisColumnDao;
import com.asl.prd004.dao.MisTypeDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.BiToolConfig;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.service.BiToolService;
import com.asl.prd004.utils.CastEntity;
import com.asl.prd004.utils.SerialNumberUtils;
import org.hibernate.transform.Transformers;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/6/5 16:07
 */
@Service
public class BiToolServiceImpl implements BiToolService {
    @Autowired
    private BiToolDao biToolDao;
    @Autowired
    private MisTypeDao misTypeDao;
    @Autowired
    private MisColumnDao misColumnDao;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public PageDataDto getAllBiToolByPage(JSONObject... params) {
        PageDataDto biToolDto = null;
        try {
            JSONObject pageState = params[0];
            int pageNum = pageState.getInt("page")-1;
            int pageSize =  pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);

            if (params.length==2 && params[1].length()!=0) {
                JSONObject sortState = params[1];
                String sortField ="mis_bi_config_id";
                if(sortState.getString("sort").equalsIgnoreCase("asc")){
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
                }else{
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
                }
            }
            Page<Object[]> biToolPage=biToolDao.getAllBiTool(pageable);
            List<BiToolConfigDTO> biToolList= CastEntity.castEntity(biToolPage.getContent(),BiToolConfigDTO.class);
            biToolDto = new PageDataDto();
            biToolDto.setData(biToolList);
            biToolDto.setTotal( biToolPage.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return biToolDto;
    }

    @Override
    public  List<BiToolConfig> getAllBiTool() {
        List<BiToolConfig>  biTool=biToolDao.findAll();
        return  biTool;
    }


    @Override
    public BiToolConfig getBiTool( String misBiConfigId) {
        BiToolConfig biTool=biToolDao.getBiToolConfigByMisBiConfigId(misBiConfigId);
        return  biTool;
    }

    @Override
    public List<TypeListDto> queryTableData() {
        return misTypeDao.getTypeAll();
    }

    @Override
    public List<DicDto> queryColumnData(String typeId) {
        return misColumnDao.getColumnDic(typeId);
    }

    @Override
    @Transactional
    public boolean editBiTool(JSONObject json) {
        try {
            boolean isMisBiConfigId=json.has("misBiConfigId");
            String misBiConfigName=json.getString("misBiConfigName");
            String misBiConfigType=json.getString("misBiConfigType");
            String misBiConfigTypeId = "";
            String misBiConfigColumnHor = "";
            String misBiConfigColumnVet = "";
            String misBiConfigGraphicType = json.getString("misBiConfigGraphicType");

            if(misBiConfigType.equals("defined_table")) {
                misBiConfigTypeId = json.getString("misBiConfigTypeId");
                misBiConfigColumnHor = json.getString("misBiConfigColumnHor");
                misBiConfigColumnVet = json.getString("misBiConfigColumnVet");
            }else{
                misBiConfigTypeId = misBiConfigType ;
            }

            String misBiConfigDefView=json.getString("misBiConfigDefView");
            String misBiConfigId="";
            if(isMisBiConfigId){
                misBiConfigId=json.getString("misBiConfigId");
            }else{
                misBiConfigId=SerialNumberUtils.getTableSequence("mis_bi_config");

            }
            BiToolConfig biToolConfig=new BiToolConfig();
            biToolConfig.setMisBiConfigId(misBiConfigId);
            biToolConfig.setMisBiConfigType(misBiConfigType);
            biToolConfig.setMisBiConfigName(misBiConfigName);
            biToolConfig.setMisBiConfigTypeId(misBiConfigTypeId);
            biToolConfig.setMisBiConfigGraphicType(misBiConfigGraphicType);
            biToolConfig.setMisBiConfigColumnHor(misBiConfigColumnHor);
            biToolConfig.setMisBiConfigColumnVet(misBiConfigColumnVet);
            biToolConfig.setMisBiConfigDefView(misBiConfigDefView);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            biToolConfig.setMisBiConfigDate(sdf.format(new Date()));
            biToolDao.saveAndFlush(biToolConfig);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }


    @Override
    public boolean deleteBiTool(JSONObject json) {
        try {
            boolean isMisBiConfigId=json.has("misBiConfigId");

            if(isMisBiConfigId){
               String misBiConfigId=json.getString("misBiConfigId");
                biToolDao.deleteById(misBiConfigId);
                return true;
            }
            return  false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<CountColumnDTO> countTableColumnData(JSONObject json) {
        try {
            String tableId=json.getString("tableId");
            tableId=misTypeDao.getTableNameById(tableId);
            String columnId =json.getString("columnId");
            MisColumn misColumn = misColumnDao.findByMisColumnId(columnId);

            String querySql="select "+misColumn.getMisColumnName()+" as columnName,count("+misColumn.getMisColumnName()+")as countData from "+tableId +" group by "+misColumn.getMisColumnName();
            List<CountColumnDTO> list=entityManager.createNativeQuery(querySql)
                    .unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(CountColumnDTO.class)).list();
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CountColumnDTO> countTableColumnDataByDate(JSONObject json) {
        try {
            //String startDate=json.getString("startDate");
            //String endDate=json.getString("endDate");
            String tableId=json.getString("tableId");
            String columnId =json.getString("columnId");
            MisColumn misColumn = misColumnDao.findByMisColumnId(columnId);
            String querySql="select `date`, column_Value as columnName,cast(sum(count_data)AS UNSIGNED INTEGER)as countData from  mis_bi_tool_count where table_id="+'"'+tableId+'"'+" and column_id="+'"'+columnId+'"'+" and column_name="+'"'+misColumn.getMisColumnName()+'"'+" group by `date`,column_Value ";
            List<CountColumnDTO> list=entityManager.createNativeQuery(querySql)
                    .unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(CountColumnDTO.class)).list();
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public   List<Object[]> countWorkflowData() {
        String querySql="select wf_workflow_status as columnName, count('wf_workflow_status') as countData from wf_workflow group by wf_workflow.wf_workflow_status";
        Query query = entityManager.createNativeQuery(querySql.toString());
        List<Object[]> list = query.getResultList();
        return list;
    }

    @Override
    public List<Object[]>  countWorkflowDataByDate() {
        String querySql="select `date`, column_Value as columnName, cast(sum(count_data)AS UNSIGNED INTEGER)as countData from  mis_bi_tool_count where table_id='wf_workflow' and column_name='wf_workflow_status' group by `date`, column_Value ";
        Query query = entityManager.createNativeQuery(querySql.toString());
        List<Object[]> list = query.getResultList();
        return list;
    }


    public  String getByKey(String key)  {
        String result="";
        switch (key) {
            case "Bar Chart":
                result ="1";
                break;
            case "Pie Chart":
                result ="2";
                break;
            case "Line Chart":
                result ="3";
                break;
            case "Bar Race":
                result ="4";
                break;
            case "Pie Race":
                result ="5";
                break;
            default:
                result ="0";
                break;
        }
        return result;
    }

}


