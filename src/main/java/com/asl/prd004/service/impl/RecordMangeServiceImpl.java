package com.asl.prd004.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.ColumnNestDTO;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.EffectColumn;
import com.asl.prd004.dto.TypeListDto;
import com.asl.prd004.entity.*;
import com.asl.prd004.enums.TableMgmtColumnInputType;
import com.asl.prd004.enums.TableMgmtColumnType;
import com.asl.prd004.service.IDictionaryService;
import com.asl.prd004.service.IRecordMangeService;
import com.asl.prd004.utils.*;
import com.asl.prd004.vo.AuditLogVO;
import com.asl.prd004.vo.CalcColumnQueryResultVO;
import com.asl.prd004.vo.MisColumnComputeFormulaVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RecordMangeServiceImpl implements IRecordMangeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IRecordMangeService.class);
    @Autowired
    private MisTypeDao misTypeDao;
    @Autowired
    private MisColumnDao misColumnDao;
    @Autowired
    private MisPropertyConfigDao propertyConfigDao;
    @Autowired
    private MisPropertyConfigDetailDao propertyConfigDetailDao;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DictionaryDao dictionaryDao;
    @Autowired
    private MisPermissionDetailDao misPermissionDetailDao;
    @Autowired
    private IDictionaryService dictionaryService;
    @Autowired
    FolderDao folderDao;
    @Autowired
    MisGpPermissionDao misGpPermissionDao;

    @Override
    public List<DicDto> getTableName() {
        return misTypeDao.getTableName();
    }

    @Override
    public List<ColumnNestDTO> getTableColumn(String tableId) {

        List<ColumnNestDTO> columnNestDTOList = misColumnDao.getColumnNestLs(tableId);
        List<MisPropertyConfig> propertyConfigs = propertyConfigDao.getMisPropertyConfigsByMisPropertyTableId(tableId);
        List<MisPropertyConfigDetail> details = new ArrayList<>();
        if (propertyConfigs != null && propertyConfigs.size() > 0) {
            details = propertyConfigDetailDao.getMisPropertyConfigDetailsByMisPropertyId(propertyConfigs.get(0).getMisPropertyId());
        }
        if (columnNestDTOList != null && columnNestDTOList.size() > 0) {
            Map<String,ColumnNestDTO> columnName2dto =
                columnNestDTOList.stream().collect(Collectors.toMap(o-> o.getMisColumnName().toLowerCase(), o->o,(o, n)->n));

            for (int i = 0; i < columnNestDTOList.size(); i++) {
                ColumnNestDTO dto = columnNestDTOList.get(i);
                if (dto.getMisColumnDictionary() != null) {
                    dto.setColumnLs(dictionaryService.getDicListById(dto.getMisColumnDictionary()));
                }
                for (MisPropertyConfigDetail d : details) {
                    if (d.getMisPropertyConfigDetailColumnId().equals(dto.getMisColumnId())) {
                        dto.setRowSize(d.getRowSize());
                        dto.setColSize(d.getColSize());
                        dto.setMisPropertySectionId(d.getMisPropertySectionId());
                    }
                }
                if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(dto.getMisColumnInputType()) ||
                        TableMgmtColumnInputType.QUERY_TEXT_BOX.getCode().equals(dto.getMisColumnInputType())){
                    MisColumn referenceById = misColumnDao.findByMisColumnId(dto.getMisColumnId());
                    String sql = referenceById.getMisColumnComputeQuery();
                    Pattern pattern = Pattern.compile("\\$value\\((.*?)\\)");
                    Matcher matcher = pattern.matcher(sql);
                    List<String> dependColumnNames = new ArrayList<>();
                    while (matcher.find()) {
                        ColumnNestDTO columnNestDTO = columnName2dto.get(matcher.group(1).toLowerCase());
                        if(Objects.nonNull(columnNestDTO)){
                            dependColumnNames.add(columnNestDTO.getMisColumnName());
                        }
                    }
                    if(CollectionUtil.isNotEmpty(dependColumnNames)){
                        EffectColumn effectColumn = new EffectColumn();
                        effectColumn.setFrom(StrUtil.join(",",dependColumnNames));
                        effectColumn.setTarget(referenceById.getMisColumnName());
                        for(String columnName:dependColumnNames){
                            ColumnNestDTO columnNestDTO = columnName2dto.get(columnName.toLowerCase());
                            if(Objects.nonNull(columnNestDTO)){
                                if(Objects.isNull(columnNestDTO.getEffect())){
                                    columnNestDTO.setEffect(new ArrayList<>());
                                }
                                columnNestDTO.getEffect().add(effectColumn);
                            }
                        }
                    }
                    //sql不为空,且不依赖其他列的 直接获取sql的查询值
                    if(StrUtil.isNotBlank(sql) && CollectionUtil.isEmpty(dependColumnNames)){
                        CalcColumnQueryResultVO calcColumnQueryResultVO = calcColumnQueryResult(dto.getMisColumnId(), null);
                        List<String> result = calcColumnQueryResultVO.getResult();
                        dto.setColumnLs(result.stream().map(str -> new DicDto(str,str)).collect(Collectors.toList()));
                    }
                }
            }
        }

        return columnNestDTOList;
        //return misColumnDao.getColumnDic(tableId);
    }

    @Transactional
    @Override
    public int insertTableData(String insertSql) {
        return insertTableData(Arrays.asList(insertSql));
    }

    @Override
    @Transactional
    public int insertTableData(Collection<String> insertSqls) {
        int i=0;
        for(String insertSql:insertSqls){
            javax.persistence.Query query = entityManager.createNativeQuery(insertSql);
            query.executeUpdate();
            i++;
        }
        //return misColumnDao.insertTableData(insertSql);
        return i;
    }

    @Override
    public String getTableNameById(String tableId) {
        return misTypeDao.getTableNameById(tableId);
    }

    @Override
    public List<Object[]> searchRecord(JSONObject json) {
        List<Object[]> misTypeList= null;
        try {
            String misTypeLabel=json.getString("MIS_TYPE_LABEL");
            String misTypeName=json.getString("MIS_TYPE_NAME");
            misTypeList = misTypeDao.searchRecord(misTypeLabel,misTypeName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return misTypeList;
    }

    @Override
    public List<Object[]> getRecordType(JSONObject json) {
        List<Object[]> misColumnList= null;
        try {
            String misTypeId=json.getString("MIS_TYPE_ID");
            misColumnList=misTypeDao.getRecordType(misTypeId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return misColumnList;
    }
    @Transactional
    @Override
    public String createRecord(JSONObject json, HttpServletRequest request) {
        try {
            String misFolderId=json.getString("MIS_FOLDER_ID");
            String misTypeName=json.getString("MIS_TYPE_NAME");
            String dataList=json.getString("DATA");
            String CONTENT=json.getString("CONTENT");
            String MD5=json.getString("MD5");
            String contentType=json.getString("CONTENT_TYPE");
            String misPermissionId=json.getString("MIS_PERMISSION_ID");
            String token = request.getHeader("authorization");
            Map<String, Object> map = JwtUtil.resolveToken(token);
            String userId = map.get("userId").toString();
            //是否有写的权限
            //Boolean flag = folderService.isPermission(misFolderId, userId, "5");
            List misPermissionLs=misPermissionDetailDao.queryPdRight(misPermissionId,userId);
            if ((misFolderId==null||"".equals(misFolderId))||(dataList==null||"".equals(dataList))) {
                return "-4";
            }else if (!(misPermissionLs.contains("5")||misPermissionLs.contains("7"))) {
                return "-3";
            }else {
                if (!(DigestUtils.md5Hex(CONTENT)).equals(MD5)) {
                    return "-5";
                } else {
                    JSONArray jsonArray = JSONArray.parseArray(dataList);

                    for (int i = 0; i < jsonArray.size(); i++) {
                        com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
                        Iterator<String> keys = jsonObject.keySet().iterator();// jsonObject.keys();
                        String tableColoumn ="";
                        StringBuffer tableValue =new StringBuffer();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String dataValue=jsonObject.getString(key);
                            tableColoumn+=key+",";
                            tableValue.append( "\""+dataValue+ "\"").append(",");
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                        Date currentDate = new Date();// 获取当前时间
                        Timestamp timeStamp=new Timestamp(currentDate.getTime());
                        String tableId=SerialNumberUtils.getTableSequence( misTypeName);
                        String tableColoumnStr="id,"+"creation_date,"+"updated_date,"+"creator_user_id,"+"updated_user_id,"+tableColoumn.substring(0,tableColoumn.length()-1);
                        String tableValueStr="\""+tableId+"\""+","+"\""+timeStamp+"\""+","+"\""+timeStamp+"\""+","+"\""+userId+"\""+","+"\""+userId+"\""+","+tableValue.substring(0,tableValue.length()-1);
                        String insertSql="insert into "+misTypeName+" ( "+tableColoumnStr+" ) "+"values"+" ( "+tableValueStr+" ) ";
                        javax.persistence.Query query= entityManager.createNativeQuery(insertSql);
                        query.executeUpdate();
                        return tableId;
                    }
                }
            }
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return "-1";
        }
        return "-1";
    }
    @Transactional
    @Override
    public String updateRecord(JSONObject json, HttpServletRequest request) {
        try {
            String misTypeId=json.getString("MIS_TYPE_ID");
            String misRecordId=json.getString("MIS_RECORD_ID");
            String dataList=json.getString("DATA");
            String CONTENT=json.getString("CONTENT");
            String MD5=json.getString("MD5");
            String contentType=json.getString("CONTENT_TYPE");
            String misPermissionId=json.getString("MIS_PERMISSION_ID");
            String token = request.getHeader("authorization");
            Map<String, Object> map = JwtUtil.resolveToken(token);
            String userId = map.get("userId").toString();
            //是否有写的权限
            //Boolean flag = folderService.isPermission(misFolderId, userId, "5");
            List misPermissionLs=misPermissionDetailDao.queryPdRight(misPermissionId,userId);
            if ((MD5==null||"".equals(MD5))||(misPermissionId==null||"".equals(misPermissionId))||(dataList==null||"".equals(dataList))) {
                return "-4";
            }else if (!(misPermissionLs.contains("5")||misPermissionLs.contains("7"))) {
                return "-3";
            }else {
                if (!(DigestUtils.md5Hex(CONTENT)).equals(MD5)) {
                    return "-5";
                } else {
                    JSONArray jsonArray = JSONArray.parseArray(dataList);
                    TypeListDto typeListDtos=misTypeDao.findTypeByMisTypeId(misTypeId);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
                        Iterator<String> keys = jsonObject.keySet().iterator();// jsonObject.keys();
                        StringBuffer tableColoumn =new StringBuffer();
                        StringBuffer tableValue =new StringBuffer();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String dataValue=jsonObject.getString(key);
                            tableColoumn.append(key).append("=").append("\""+dataValue+"\"").append(",");
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                        Date currentDate = new Date();// 获取当前时间
                        Timestamp timeStamp=new Timestamp(currentDate.getTime());
                        String tableColoumnStr="updated_date="+"\""+timeStamp+"\""+","+tableColoumn.substring(0,tableColoumn.length()-1);
                        String updateSql="update "+typeListDtos.getMisTypeName()+" set "+tableColoumnStr+" where id="+"\""+misRecordId+"\"";
                        javax.persistence.Query query= entityManager.createNativeQuery(updateSql);
                        query.executeUpdate();
                        return "0";
                    }
                }
            }

        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return "-1";
        }
        return "-1";
    }
    @Transactional
    @Override
    public String changeRecordLink(JSONObject json,HttpServletRequest request) {
        try {
            String misTypeId=json.getString("MIS_TYPE_ID");
            String misRecordId=json.getString("MIS_RECORD_ID");
            String link=json.getString("LINK");
            String unLink=json.getString("UNLINK");
            String token = request.getHeader("authorization");
            Map<String, Object> map = JwtUtil.resolveToken(token);
            String userId = map.get("userId").toString();
            TypeListDto typeListDtos=misTypeDao.findTypeByMisTypeId(misTypeId);
            String querySql="select mis_permission_id,folder_id from "+typeListDtos.getMisTypeName()+" where id="+"\""+misRecordId+"\"";
            javax.persistence.Query query= entityManager.createNativeQuery(querySql);
            List executeResult=query.getResultList();
            Object[] obj= (Object[]) executeResult.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();// 获取当前时间
            Timestamp timeStamp=new Timestamp(currentDate.getTime());
            if((link==null&&unLink==null)||("".equals(link)&&"".equals(unLink))){
                return "0";
            } else if(executeResult.size()==0){
                return "-3";
            }else if(link!=null&&!"".equals(link)){
                String permissionId= (String) obj[0];
                List misPermissionLs=misPermissionDetailDao.queryPdRight(permissionId,userId);
                if (!(misPermissionLs.contains("5")||misPermissionLs.contains("7"))) {
                    return "-4";
                }
            }
            if(unLink!=null&&!"".equals(unLink)){
                //如果unlink值不为空，并且传进来的record_id的 mis_folder_id值匹配传进来的unlink id, 则把mis_folder_id置换为link id, 如果不匹配则抛出异常
                String permissionId= (String) obj[0];
                List misPermissionLs=misPermissionDetailDao.queryPdRight(permissionId,userId);
                if (!(misPermissionLs.contains("5")||misPermissionLs.contains("7"))) {
                    return "-5";
                }
                String folderId= (String) obj[1];
                if(folderId.equals(unLink)){
                    String updateSql="update "+typeListDtos.getMisTypeName()+" set "+"updated_date="+"\""+timeStamp+"\""+","+"folder_id="+"\""+link+"\""+" where id="+"\""+misRecordId+"\"";
                    javax.persistence.Query excuteUpdate= entityManager.createNativeQuery(updateSql);
                    excuteUpdate.executeUpdate();
                }else{
                    return "-1";
                }
            }else {//如果unlink值为空，则直接替换record_id的 mis_folder_id值为link代表的mis_folder_id
                String updateSql="update "+typeListDtos.getMisTypeName()+" set "+"updated_date="+"\""+timeStamp+"\""+","+"folder_id="+"\""+link+"\""+" where id="+"\""+misRecordId+"\"";
                javax.persistence.Query excuteUpdate= entityManager.createNativeQuery(updateSql);
                excuteUpdate.executeUpdate();
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
            //return "-1";
        }
        return "0";
    }
    
    @Transactional
    @Override
    public String deleteRecord(JSONObject json, HttpServletRequest request) {
        try {
            String misTypeId=json.getString("MIS_TYPE_ID");
            String misRecordId=json.getString("MIS_RECORD_ID");
            String token = request.getHeader("authorization");
            Map<String, Object> map = JwtUtil.resolveToken(token);
            String userId = map.get("userId").toString();
            TypeListDto typeListDtos=misTypeDao.findTypeByMisTypeId(misTypeId);
            String querySql="select mis_permission_id,folder_id from "+typeListDtos.getMisTypeName()+" where id="+"\""+misRecordId+"\"";
            javax.persistence.Query query= entityManager.createNativeQuery(querySql);
            List executeResult=query.getResultList();
            Object[] obj= (Object[]) executeResult.get(0);
            List misPermissionLs=misPermissionDetailDao.queryPdRight((String) obj[0],userId);
            if(executeResult.size()==0){
                return "-3";
            }else if(!(misPermissionLs.contains("5")||misPermissionLs.contains("7"))){
                return "-4";
            }else{
                String updateSql="delete from  "+typeListDtos.getMisTypeName()+" where id="+"\""+misRecordId+"\"";
                javax.persistence.Query excuteUpdate= entityManager.createNativeQuery(updateSql);
                excuteUpdate.executeUpdate();
            }
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return "-1";
        }
        return "0";
    }

    @Override
    public List searchRecordData(JSONObject json) {
        try {
            String misTypeName=json.getString("MIS_TYPE_NAME");
            String bookName=json.getString("BOOK_NAME");
            String misRecordId=json.getString("MIS_RECORD_ID");
            List<MisType> misTypes=misTypeDao.getMisTypesByMisTypeName(misTypeName);
            String querySql="select * from "+misTypeName+ " where 1=1 ";
            if(misRecordId!=null&&!"".equals(misRecordId)){
                querySql+=" and id="+"\""+misRecordId+"\"";
            }
            /*if(bookName!=null&&!"".equals(bookName)){
                querySql+=" and id="+"\""+misRecordId+"\"";
            }*/
            List<Map<String, Object>> ls=entityManager.createNativeQuery(querySql).unwrap(SQLQuery.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).list();
            List<Map<String, Object>> returnList = new ArrayList<>(ls.size());
            ls.forEach(oneMap -> {
                Map<String, Object> returnMap = new HashMap<>(oneMap.size());
                // 转为小写 oneMap.forEach((key, val) -> returnMap.put(key.toLowerCase(), val));
                oneMap.forEach((key, val) -> returnMap.put(key.toUpperCase(), val));
                returnList.add(returnMap);
            });
            return returnList;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List getRecord(JSONObject json) {
        List obj=null;
        try {
            String misTypeId=json.getString("MIS_TYPE_ID");
            String misRecordId=json.getString("MIS_RECORD_ID");
            String withContent=json.getString("with content");

            TypeListDto typeListDtos=misTypeDao.findTypeByMisTypeId(misTypeId);
            String querySql="select ab.* from "+typeListDtos.getMisTypeName()+" as ab "+" where id="+"\""+misRecordId+"\"";
            obj=entityManager.createNativeQuery(querySql).unwrap(SQLQuery.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).list();
            String md5Obj="";
            if(withContent!=null&&"Y".equals(withContent)){
                md5Obj=DigestUtils.md5Hex(obj.toString());
            }
            Base64.Encoder encoder = Base64.getEncoder();
           byte[] textByte = obj.toString().getBytes("UTF-8");
            String encodedText = encoder.encodeToString(textByte);
            String querySql1="select ab.*,"+"\""+md5Obj+"\""+"as MD5,"+"\""+encodedText+"\""+" as CONTENT_TYPE "+" from "+typeListDtos.getMisTypeName()+" as ab "+" where id="+"\""+misRecordId+"\"";
            obj=entityManager.createNativeQuery(querySql1).unwrap(SQLQuery.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Log("Insert table data.")
    @Transactional
    @Override
    public AuditLogVO insertTable(String data) throws JSONException {
		
		JSONObject json = new JSONObject(data);
        String tableId=json.getString("tableId");
        TypeListDto typeByMisTypeId = misTypeDao.findTypeByMisTypeId(tableId);
        String tableName=getTableNameById(tableId);

        List<MisColumn> columns = misColumnDao.findByMisTypeId(tableId);
        Map<String,MisColumn> id2column = columns.stream().collect(Collectors.toMap(o-> o.getMisColumnId(), o->o,(o, n)->n));
        List<MisColumn> computeFieldColumns =
                columns.stream()
                        .filter(c -> TableMgmtColumnInputType.COMPUTE_FIELD.getCode().equals(c.getMisColumnInputType())).collect(Collectors.toList());

        String tableSeqId=SerialNumberUtils.getTableSequence(tableName);
        boolean checkData=json.getBoolean("checkData");
        int bulkCreateData= "".equals(json.getString("BulkCreateData"))?0:Integer.parseInt(json.getString("BulkCreateData"));
        tableSeqId="'"+tableSeqId+"'";
        json.remove("tableId");

        JSONObject arr = new JSONObject(json.getString("data"));
        String colArr = "";
        String dataArr = "";
        Map<String,Object> columnName2value = new HashMap<>();
        List<String> sqls = new ArrayList<>();
        for (Iterator<?> iter = arr.keys(); iter.hasNext();) {
            String key = (String) iter.next();
            colArr += key + ",";
            JSONObject getValue=new JSONObject(arr.getString(key));

            String inputType = getValue.getString("input_type");
            String value = getValue.getString("value");
            if(TableMgmtColumnInputType.SYSTEM_GENERATED_DATE_TIME.getCode().equals(inputType)){
                value =(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                dataArr += "'" + value + "'" + ",";
            } else if(TableMgmtColumnInputType.REPEATING_FIELD.getCode().equals(inputType)){
                if(StrUtil.isNotBlank(value)){
                    List<String> split = JSONArray.parseArray(value, String.class);
                    split = CollectionUtil.sort(split,String::compareTo);
                    List<MisColumn> repeatingColumnList = misColumnDao.findByMisTypeIdAndMisColumnName(tableId, key);
                    if(CollectionUtil.isNotEmpty(repeatingColumnList)){
                        String sqlFormat = "insert into %s(id,mis_column_id,value) values (%s,'%s','%s')";
                        MisColumn misColumn = repeatingColumnList.get(0);
                        for(String v:split){
                            sqls.add(String.format(sqlFormat,typeByMisTypeId.getMisTypeName() + "_r",tableSeqId,misColumn.getMisColumnId(),v));
                        }
                    }
                    value = split.get(0);
                    dataArr += "'" + value + "'" + ",";
                }
                else{
                    dataArr += "'" + value + "'" + ",";
                }
            }else {
                dataArr += "'" + value + "'" + ",";
            }
            // System.out.println("value==="+getValue.getString("value")+"input_type==="+getValue.getString("input_type"));
            columnName2value.put(key.toLowerCase(),value);
        }

        if(CollectionUtil.isNotEmpty(computeFieldColumns)){
            for(int i=0,len=computeFieldColumns.size();i<len;i++){
                MisColumn c = computeFieldColumns.get(i);
                String misColumnComputeFrom = c.getMisColumnComputeFrom();
                boolean fromFormula = "1".equals(misColumnComputeFrom);
                boolean fromQuery = "2".equals(misColumnComputeFrom);
                Object result = "";
                if(fromFormula){
                    try{
                        MisColumnComputeFormulaVO formula = JSON.parseObject(c.getMisColumnComputeFormula(),MisColumnComputeFormulaVO.class);
                        MisColumn column1 = id2column.get(formula.getMisColumnId1());
                        MisColumn column2 = id2column.get(formula.getMisColumnId2());
                        Object val1 = columnName2value.get(column1.getMisColumnName().toLowerCase());
                        Object val2 = columnName2value.get(column2.getMisColumnName().toLowerCase());
                        TableMgmtColumnType columnType = TableMgmtColumnType.getByCode(column1.getMisColumnType());
                        String operator = formula.getOperator();
                        result = MisColumnComputeFormulaVO.calc(columnType,val1,operator,val2);
                    }
                    catch (Exception e){
                        LOGGER.error("error during compute from formula,param:" + columnName2value,e);
                    }
                }
                if(fromQuery){
                    try{
                        String computeQuerySql = StrUtil.format(c.getMisColumnComputeQuery(),"$value(",")",columnName2value);
                        Query computeQuery = entityManager.createNativeQuery(computeQuerySql);
                        List computeQueryList = computeQuery.getResultList();
                        if(CollectionUtil.isNotEmpty(computeQueryList)){
                            result = computeQueryList.get(0);
                        }
                    }
                    catch (Exception e){
                        LOGGER.error("error during compute from sql,param:" + columnName2value,e);
                    }
                }
                colArr += c.getMisColumnName() + ",";
                dataArr += "'" + result + "'" + ",";
            }
        }

        colArr=colArr.substring(0, colArr.length() - 1);
        dataArr=dataArr.substring(0, dataArr.length() - 1);
        //String[] colArrary=colArr.split(",");
        //for(int i=0;i<colArrary.length;i++){
        //    dataArr+="'"+json.getString(colArrary[i])+"'"+",";
        // }
        String folderId = json.getString("folder_id");
        String permissionId = folderDao.getPermissionIdByFolderId(folderId);
        String userId = ContextHolder.getUserId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNormal = sdf.format(new Date());

        //String currentTime=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(System.currentTimeMillis());
        String insertSql="insert into "+tableName+" (id,creation_date,creator_user_id,updated_date,updated_user_id, mis_permission_id, folder_id, "+colArr+")"
                +" values"+"("+tableSeqId+",'"+dateNormal+"','"+userId+"','"+dateNormal+"','"+userId+"','" +permissionId+"','"+folderId+"',"+dataArr+")";
        /*Query query = entityManager.createNativeQuery(insertSql);
        int insertFlag = query.executeUpdate();*/
        int insertFlag=0;
        List<MisGpTypePermission> misGpTypePermissionList=misGpPermissionDao.findByMisTypeIdAndCreateBy(tableId,userId);
        if(misGpTypePermissionList.size()>0){
            for(int i=0;i<misGpTypePermissionList.size();i++){
                if(misGpTypePermissionList.get(i).getDirectCreate()!=null&&"1".equals(misGpTypePermissionList.get(i).getDirectCreate())){
                    if(checkData){
                        for(int l=0;l<bulkCreateData;l++){
                            String insertBulkSql="insert into "+tableName+" (id,creation_date,creator_user_id,updated_date,updated_user_id, mis_permission_id, folder_id, "+colArr+")"
                                    +" values"+"("+tableSeqId+",'"+dateNormal+"','"+userId+"','"+dateNormal+"','"+userId+"','" +permissionId+"','"+folderId+"',"+dataArr+")";
                            Query query = entityManager.createNativeQuery(insertBulkSql);
                            insertFlag = query.executeUpdate();
                            tableSeqId=SerialNumberUtils.getTableSequence(tableName);
                            tableSeqId="'"+tableSeqId+"'";
                        }
                    }else{
                        Query query = entityManager.createNativeQuery(insertSql);
                        insertFlag = query.executeUpdate();
                    }
                    break;
                }else{
                    tableName=typeByMisTypeId.getMisTypeName()+ConstUtils.TYPEDRAFT;
                    //tableName=tableName.replace(ConstUtils.TYPEMAIN,ConstUtils.TYPEDRAFT);
                    if(checkData) {
                        for (int l = 0; l < bulkCreateData; l++) {
                            insertSql = "insert into " + tableName + " (id,creation_date,creator_user_id,updated_date,updated_user_id, mis_permission_id, folder_id, " + colArr + ")"
                                    + " values" + "(" + tableSeqId + ",'" + dateNormal + "','" + userId + "','" + dateNormal + "','" + userId + "','" + permissionId + "','" + folderId + "'," + dataArr + ")";
                            Query query = entityManager.createNativeQuery(insertSql);
                            insertFlag = query.executeUpdate();
                            tableSeqId=SerialNumberUtils.getTableSequence(tableName);
                            tableSeqId="'"+tableSeqId+"'";
                        }
                    }else{
                        insertSql = "insert into " + tableName + " (id,creation_date,creator_user_id,updated_date,updated_user_id, mis_permission_id, folder_id, " + colArr + ")"
                                + " values" + "(" + tableSeqId + ",'" + dateNormal + "','" + userId + "','" + dateNormal + "','" + userId + "','" + permissionId + "','" + folderId + "'," + dataArr + ")";
                        Query query = entityManager.createNativeQuery(insertSql);
                        insertFlag = query.executeUpdate();
                        tableSeqId=SerialNumberUtils.getTableSequence(tableName);
                        tableSeqId="'"+tableSeqId+"'";
                    }
                }
            }
        }else{
            tableName=typeByMisTypeId.getMisTypeName()+ConstUtils.TYPEDRAFT;
            //tableName=tableName.replace(ConstUtils.TYPEMAIN,ConstUtils.TYPEDRAFT);
            if(checkData) {
                for (int l = 0; l < bulkCreateData; l++) {
                    insertSql = "insert into " + tableName + " (id,creation_date,creator_user_id,updated_date,updated_user_id, mis_permission_id, folder_id, " + colArr + ")"
                            + " values" + "(" + tableSeqId + ",'" + dateNormal + "','" + userId + "','" + dateNormal + "','" + userId + "','" + permissionId + "','" + folderId + "'," + dataArr + ")";
                    Query query = entityManager.createNativeQuery(insertSql);
                    insertFlag = query.executeUpdate();
                    tableSeqId=SerialNumberUtils.getTableSequence(tableName);
                    tableSeqId="'"+tableSeqId+"'";
                }
            }else{
                insertSql = "insert into " + tableName + " (id,creation_date,creator_user_id,updated_date,updated_user_id, mis_permission_id, folder_id, " + colArr + ")"
                        + " values" + "(" + tableSeqId + ",'" + dateNormal + "','" + userId + "','" + dateNormal + "','" + userId + "','" + permissionId + "','" + folderId + "'," + dataArr + ")";
                Query query = entityManager.createNativeQuery(insertSql);
                insertFlag = query.executeUpdate();
                tableSeqId=SerialNumberUtils.getTableSequence(tableName);
                tableSeqId="'"+tableSeqId+"'";
            }
        }
		
		
        AuditLogVO vo = new AuditLogVO();
        vo.setTypeId(tableId);
        vo.setTableName(tableName);
        vo.setRecordId(tableSeqId);
        if(insertFlag >= 1){
            return vo;
        }else{
            return null;
        }
    }

    @Override
    public CalcColumnQueryResultVO calcColumnQueryResult(String misColumnId, Map<String,Object> param) {
        MisColumn byMisColumnId = misColumnDao.findByMisColumnId(misColumnId);
        String misColumnComputeQuery =
                StrUtil.format(byMisColumnId.getMisColumnComputeQuery(), "$value(", ")", param);
        List<String> result = new ArrayList<>();
        try{
            List queryResult = entityManager.createNativeQuery(misColumnComputeQuery).getResultList();
            if(CollectionUtil.isNotEmpty(queryResult)){
                for(Object o:queryResult){
                    //只取第一列的值
                    result.add(
                            Objects.isNull(o) ? "":
                                    ((o instanceof Object[]) ?
                                            StrUtil.nullToEmpty(((Object[])o)[0]) : StrUtil.nullToEmpty(o)));
                }
            }
        }
        catch (Exception e){
            LOGGER.error("execute sql error:" + misColumnComputeQuery,e);
        }
        CalcColumnQueryResultVO vo = new CalcColumnQueryResultVO();
        vo.setResult(result);
        return vo;
    }
    @Transactional
    @Override
    public int updateTableData(String updateSql) {
        javax.persistence.Query excuteUpdate= entityManager.createNativeQuery(updateSql);
        excuteUpdate.executeUpdate();
        return 1;
    }
}
