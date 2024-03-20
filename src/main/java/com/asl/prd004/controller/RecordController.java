package com.asl.prd004.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisGpTypePermission;
import com.asl.prd004.entity.MisPropertyConfig;
import com.asl.prd004.service.ITypeCrossRefService;
import com.asl.prd004.utils.ConstUtils;
import com.asl.prd004.utils.Log;
import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisImportHist;
import com.asl.prd004.service.IRecordService;
import com.asl.prd004.service.ISubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/record")
public class RecordController {

    @Resource
    private IRecordService recordService;

    @Autowired
    private MisColumnDao misColumnDao;

    @Resource
    private ISubscriptionService subscriptionService;

    @Autowired
    ITypeCrossRefService typeCrossRefService;

    @Autowired
    private MisCrossRefDao misCrossRefDao;

    @Autowired
    private MisTypeDao misTypeDao;

    @Autowired
    private MisPropertyConfigDetailDao misPropertyConfigDetailDao;

    @Autowired
    private MisPropertyConfigDao misPropertyConfigDao;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MisGpPermissionDao misGpPermissionDao;



    @Log("Search record.")
    @RequestMapping(value="/searchRecord", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator searchRecord(@RequestBody SearchRecordDTO data) throws JSONException {
        return ResultGenerator.getSuccessResult(recordService.searchRecord(data));
    }


    @Log("Get properties.")
    @RequestMapping(value="/getProperties", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getProperties(@RequestBody String data) throws Exception {
        //解析接受到的json数据
        JSONObject json = new JSONObject(data);

        String tableId = json.getString("tableId");
        String recordId = json.getString("recordId");

        //1. get property column
        List<ColumnInputDTO> columnDataList =  recordService.getProperties(tableId,recordId);
        Map<String,Object> map = new HashMap<>();
        if(!columnDataList.isEmpty()){
            Boolean flag = recordService.isSubscribe(recordId);
            Boolean isEdit = recordService.isEdit(tableId,recordId);
            Boolean isDelete = recordService.isDelete(tableId,recordId);
            List tableList = typeCrossRefService.getTypeRefByTypeId(tableId);

            List<Object> refTableList = new ArrayList<>();
            for (int i = 0; i < tableList.size(); i++) {
                HashMap<String, Object> refTableListObj = new HashMap<>();

                TypeRefPropertyDto refTable = (TypeRefPropertyDto) tableList.get(i);
                TypeRefDto typeRefDto = misCrossRefDao.findMisCrossRefByMisCrossRefId(refTable.getMisCrossRefId());
                List valueList = recordService.getRefPropertiesValue(typeRefDto,recordId);
                Boolean availableTable  = true;
                if(valueList.size() == 0){
                    availableTable = false;
                }
                refTableListObj.put( "misCrossRefId", refTable.getMisCrossRefId());
                refTableListObj.put( "misCrossRefParentTableID", refTable.getMisCrossRefParentTableID());
                refTableListObj.put( "misCrossRefParentTableLabel", refTable.getMisCrossRefParentTableLabel());
                refTableListObj.put( "misCrossRefChildTableID", refTable.getMisCrossRefChildTableID());
                refTableListObj.put( "misCrossRefChildTableLabel", refTable.getMisCrossRefChildTableLabel());
                refTableListObj.put( "availableTable", availableTable);
                refTableList.add(refTableListObj);
            }
            List<SectionColumnDTO>  sectionColumnLs=new ArrayList<>();
            SectionColumnDTO sectionColumnDTO=null;
            List<ColumnInputDTO> columnInputDTOList=null;
            List<MisPropertyConfig> misPropertyConfigList=misPropertyConfigDao.getMisPropertyConfigsByMisPropertyTableId(tableId);
            List<SectionLableColumnDTO> sectionLableColumnDTOList=misPropertyConfigDetailDao.getSectionLableColumnList(tableId);
            for(SectionLableColumnDTO sectionLableColumnDTO: sectionLableColumnDTOList){
                if(!sectionColumnLs.stream().anyMatch(p->p.getName().equals(sectionLableColumnDTO.getMisSectionLabel()))){
                    sectionColumnDTO=new SectionColumnDTO();
                    columnInputDTOList=new ArrayList<>();
                    sectionColumnDTO.setName(sectionLableColumnDTO.getMisSectionLabel());
                }
                for(int i=0;i<columnDataList.size();i++){
                    if(sectionLableColumnDTO.getMisPropertyConfigDetailColumnId().equals(columnDataList.get(i).getMisColumnId())){
                        columnDataList.get(i).setMisPropertySectionId(sectionLableColumnDTO.getMisPropertySectionId());
                        columnInputDTOList.add(columnDataList.get(i));

                        sectionColumnDTO.setColumns(columnInputDTOList);
                    }
                }
                sectionColumnLs.add(sectionColumnDTO);
            }
            List<SectionColumnDTO> sectionColumnDTOS=sectionColumnLs.stream().distinct().collect(Collectors.toList());
            map.put("typeId", tableId);
            map.put("tableLabel", misTypeDao.getTableLabelById(tableId));
            map.put("id", recordId);
            map.put("propertyConfig",misPropertyConfigList);
            map.put("columnDataList",columnDataList);
            map.put("propertyConfigDetails",sectionColumnDTOS);
            map.put("flag",flag);
            map.put("isEdit",isEdit);
            map.put("isDelete",isDelete);
            map.put("refTableList", refTableList);
        }
        return ResultGenerator.getSuccessResult(map);
    }

    @Log("Query refProperties")
    @RequestMapping(value="/getRefProperties", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getRefProperties(@RequestBody String data) throws Exception {
        //解析接受到的json数据
        JSONObject json = new JSONObject(data);

        String tableId = json.getString("tableId");
        String recordId = json.getString("recordId");
        String misCrossRefId = json.getString("misCrossRefId");
        List<ColumnInputDTO> columnDataList =  recordService.getRefProperties(tableId, recordId, misCrossRefId);
        TypeRefDto typeRefDto = misCrossRefDao.findMisCrossRefByMisCrossRefId(misCrossRefId);

        //get ref record id
        String refRecordId = recordService.getRefRecordId(typeRefDto, recordId);
        Boolean flag = recordService.isSubscribe(refRecordId);
        Boolean isEdit = recordService.isEdit(typeRefDto.getMisCrossRefChildTableID(),refRecordId);
        Boolean isDelete = recordService.isDelete(typeRefDto.getMisCrossRefChildTableID(),refRecordId);
        Boolean hasChildrenTable = recordService.hasChildrenTable(tableId);

        Map<String,Object> map = new HashMap<>();
        map.put("typeId", typeRefDto.getMisCrossRefChildTableID());
        map.put("id", refRecordId);
        map.put("columnDataList",columnDataList);
        map.put("flag",flag);
        map.put("isEdit",isEdit);
        map.put("isDelete",isDelete);
        map.put("hasChildrenTable", hasChildrenTable);
        return ResultGenerator.getSuccessResult(map);
    }

    @Log("Save properties.")
    @RequestMapping(value="/saveProperties", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator saveProperties(@RequestBody String data) throws JSONException{
        //解析接受到的json数据
        JSONObject json = new JSONObject(data);
        String id = json.getString("id");
        String typeId = json.getString("typeId");
        JSONObject arr = new JSONObject(json.getString("data"));

        recordService.saveProperties(id, typeId, arr);

        //subscription record
        List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId( typeId, id);
        if(!subscriptionList.isEmpty()){
            for (SubscriptionBuDto subscription : subscriptionList) {
                if(subscription.getMisSubEventMsg().equals("Modified")){
                    subscriptionService.insertMsg(subscription.getMisSubscriptionId(),subscription.getMisSubscriptionEventId());
                }
            }
        }
        return ResultGenerator.getSuccessResult(true);
    }

    @Log("Delete properties.")
    @RequestMapping(value="/deleteProperties", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteProperties(@RequestBody String data) throws JSONException{
        //解析接受到的json数据
        JSONObject json = new JSONObject(data);
        String id = json.getString("id");
        String typeId = json.getString("typeId");
        recordService.deleteProperties(id,typeId);

        //subscription record
        List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId(typeId, id);
        if(!subscriptionList.isEmpty()){
            for (SubscriptionBuDto subscription : subscriptionList) {
                if(subscription.getMisSubEventMsg().equals("Deleted")){
                    subscriptionService.insertMsg(subscription.getMisSubscriptionId(),subscription.getMisSubscriptionEventId());
                }
            }
        }
        return ResultGenerator.getSuccessResult(true);
    }
    @Log("Delete refProperties")
    @RequestMapping(value="/deleteRefProperties", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator deleteRefProperties(@RequestBody String data) throws JSONException{
        //解析接受到的json数据
        JSONObject json = new JSONObject(data);
        String id = json.getString("id");
        String typeId = json.getString("typeId");
        Boolean hasChildrenTable = json.getBoolean("hasChildrenTable");

        if(hasChildrenTable){
            recordService.deleteRefProperties(id, typeId);
        }else{
            recordService.deleteProperties(id,typeId);
        }


        //subscription record
        List<SubscriptionBuDto> subscriptionList = subscriptionService.getSubscriptionListByTypeIdAndObjId(typeId, id);
        if(!subscriptionList.isEmpty()){
            for (SubscriptionBuDto subscription : subscriptionList) {
                if(subscription.getMisSubEventMsg().equals("Deleted")){
                    subscriptionService.insertMsg(subscription.getMisSubscriptionId(),subscription.getMisSubscriptionEventId());
                }
            }
        }
        return ResultGenerator.getSuccessResult(true);
    }

    @Log("Get folder record list.")
    @RequestMapping(value="/getFolderRecordList", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getFolderRecordList(@RequestBody String data) throws JSONException {
        //解析接受到的json数据
        JSONObject json = new JSONObject(data);
        String typeId = json.getString("typeId");
        String folderId = json.getString("folderId");
        List<MisColumn> misColumnList = new ArrayList<>();
        MisColumn misColumn = new MisColumn();
        misColumn.setMisTypeId(typeId);
        misColumn.setMisColumnName("id");
        misColumn.setMisColumnLabel("id");
        misColumn.setMisColumnType("1");
        misColumnList.add(misColumn);
        JSONArray sortModel = json.optJSONArray("sortModel");
        JSONObject pageState = json.optJSONObject("pageState");
        List<MisColumn> columnList  = recordService.findByMisTypeId(typeId);
        if(columnList.size()==0){
            return new ResultGenerator(300,"No columns in table.");
        }
        //编辑或删除流程返回数据内容
        String tableName="";
        StringBuffer querySql=new StringBuffer("select id, ");
        List<TypeIdDto> propertyList  = misPropertyConfigDetailDao.getMisPropertyConfigDetailsByMisTypeId(typeId);
        for (int i = 0; i < columnList.size(); i++) {
            TypeIdDto columnIdDto = new TypeIdDto();
            columnIdDto.setId(columnList.get(i).getMisColumnId());
            if(propertyList.contains(columnIdDto)) {
                misColumnList.add(columnList.get(i));
            }
            if(i+1>=columnList.size()){
                querySql.append(columnList.get(i).getMisColumnName());
            }else{
                querySql.append(columnList.get(i).getMisColumnName()+" ,");
            }
            String userId=ContextHolder.getUserId();
            List<MisGpTypePermission> misGpTypePermissionList=misGpPermissionDao.findByMisTypeIdAndCreateBy(typeId,userId);
            if(columnList.get(i).getType()!=null&&misGpTypePermissionList.size()>0){
                tableName=columnList.get(i).getType().getMisTypeName()+ ConstUtils.TYPEDRAFT;
            }else{
                tableName=columnList.get(i).getType().getMisTypeName()+ ConstUtils.TYPEMAIN;
            }
        }
        querySql.append(" from ").append(tableName);
        Query nativeQuery=entityManager.createNativeQuery(querySql.toString());
        nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List tableListData=nativeQuery.getResultList();

        PageDataDto pageData = recordService.getFolderRecordByPage(typeId, folderId, misColumnList, sortModel, pageState);
        Map<String,Object> map = new HashMap<>();
        map.put("columnList", misColumnList);
        map.put("recordList", pageData.getData());
        map.put("tableListData", tableListData);
        map.put("total", pageData.getTotal());
        return ResultGenerator.getSuccessResult(map);
    }

    @Log("Get record list.")
    @RequestMapping(value="/getRecordListByRecIds", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getRecordListByRecIds(@RequestBody RecordIdListDto dto) throws JSONException {

        Map<String,Object> map;
        map = recordService.getRecordListByRecIds(dto);
        if(map.containsKey("error")){
            return (ResultGenerator) map.get("error");
        }
        return ResultGenerator.getSuccessResult(map);
    }

    @Log("Get record History.")
    @RequestMapping(value="/getRecordHistoryByRecId", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getRecordHistoryByRecId(@RequestBody RecordIdDto dto) {
        return recordService.getRecordHistoryByRecId(dto);
    }

    @Log("Get record Audit Detail.")
    @RequestMapping(value="/getRecordAuditDetailByRecId", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getRecordAuditDetailByRecId(@RequestBody RecordIdDto dto) {
        return recordService.getRecordAuditDetailByRecId(dto);
    }



    @Log("Get record Comparison.")
    @RequestMapping(value="/getRecordComparisonByRecId", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getRecordComparisonByRecId(@RequestBody RecordComparisonDto dto) {
        return recordService.getRecordComparisonByRecId(dto);
    }

    @Log("Get Default record list.")
    @RequestMapping(value="/getDefaultRecordList", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getDefaultRecordList(@RequestBody RecordListPageableDto dto){
        return recordService.getDefaultRecordList(dto);
    }


    @Log("Import excel data.")
    @RequestMapping(value="/importExcelData", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator importExcelData(@RequestBody String data) throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        org.json.JSONArray excelData=json.getJSONArray("excelData");
        if(excelData.length()>0){
            String tableName=json.getString("tableName");
            String excelType=json.getString("excelType");
            String excelName=json.getString("excelName");
            String isOverwrite=json.getString("isOverwrite");
            String decryptPass=json.getString("decryptPass");
            String confirmPass=json.getString("confirmPass");
            String folderId=json.getString("folderId");
            StringBuffer appendSql=appendCols(excelData,tableName);
            int i=0;
            for(int j=0;j<excelData.length();j++) {
                i++;
                if (i > 1) {
                    appendSql.append(",(");
                }
                JSONObject objKeyps= (JSONObject) excelData.get(j);
                String appendValues="";
                for (Iterator it = objKeyps.keys(); it.hasNext(); ) {
                    String key = (String) it.next();
                    Object param = objKeyps.get(key);
                    if (param instanceof Integer) {
                        int objValue = ((Integer) param).intValue();
                        appendValues+=objValue+",";
                    } else if (param instanceof String) {
                        String objValue = (String) param;
                        appendValues+="'"+objValue+"'"+",";
                    } else if (param instanceof Double) {
                        double objValue = ((Double) param).doubleValue();
                        appendValues+=objValue+",";
                    } else if (param instanceof Float) {
                        float objValue = ((Float) param).floatValue();
                        appendValues+=objValue+",";
                    } else if (param instanceof Long) {
                        long objValue = ((Long) param).longValue();
                        appendValues+=objValue+",";
                    } else if (param instanceof Boolean) {
                        boolean objValue = ((Boolean) param).booleanValue();
                        appendValues+=objValue+",";
                    } else if (param instanceof Date) {
                        Date objValue = (Date) param;
                        appendValues+=objValue+",";
                    }
                }
                String tableId= SerialNumberUtils.getTableSequence(  tableName);
                appendSql.append(appendValues+tableId+","+folderId+" )");
                if(i>1000){
                    if(isOverwrite!=null&&"1".equals(isOverwrite)){
                        //覆盖当前表，先删除表中数据
                        recordService.delImportTtable(tableName);
                    }
                    //插入数据
                    recordService.importExcelData(appendSql);
                    i=0;
                    appendSql=appendCols(excelData,tableName);
                }
            }
            if(i>0){
                if(isOverwrite!=null&&"1".equals(isOverwrite)){
                    //覆盖当前表，先删除表中数据
                    recordService.delImportTtable(tableName);
                }
                recordService.importExcelData(appendSql);
            }
            String importId= SerialNumberUtils.getTableSequence(  tableName);
            MisImportHist misImportHist=new MisImportHist();
            misImportHist.setMisImportId(importId);
            misImportHist.setMisImportFilename(tableName);
            misImportHist.setMisExcelType(excelType);
            misImportHist.setMisExportFilename(excelName);
            misImportHist.setMisDecryptPass(DigestUtils.md5Hex(decryptPass));
            misImportHist.setMisConfirmPass(DigestUtils.md5Hex(confirmPass));
            misImportHist.setMisImportDate(String.valueOf(new Date()));
            misImportHist.setMisImportUserId(ContextHolder.getUserId());
            recordService.insertImportConfig(misImportHist);
        }
        return ResultGenerator.getSuccessResult("导入数据成功!");
    }
    private StringBuffer appendCols(org.json.JSONArray excelData,String tableName) throws Exception{
        StringBuffer appendSql=new StringBuffer();
        StringBuffer sb=new StringBuffer(" insert into "+tableName+" (");
        JSONObject obj= (JSONObject) excelData.get(0);
        String colLs="";
        for (Iterator it = obj.keys(); it.hasNext(); ) {
            String key = (String) it.next();
            colLs=colLs+key+",";
        }
        colLs=colLs+"id"+","+"folder_id";
        colLs=colLs+" ) values ( ";
        appendSql.append(sb.append(colLs));
        return appendSql;
    }
    @Log("Get record edit History.")
    @RequestMapping(value="/getRecordEditListByRecIds", method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getRecordEditListByRecIds(@RequestBody RecordIdDto dto) {
        return recordService.getRecordEditListByRecIds(dto);
    }


}
