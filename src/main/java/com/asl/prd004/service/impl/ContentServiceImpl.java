package com.asl.prd004.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.utils.JSchUtil;
import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.FolderDao;
import com.asl.prd004.dto.NodeDto;
import com.asl.prd004.entity.MisFolder;
import com.asl.prd004.service.*;
import com.asl.prd004.utils.SmbUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


import org.apache.commons.lang3.ArrayUtils;

import com.alibaba.fastjson.JSON;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.entity.*;
import com.asl.prd004.service.ContentService;
import com.asl.prd004.service.IRecordMangeService;
import com.asl.prd004.utils.ConstUtils;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import static com.asl.prd004.utils.ConstUtils.LINUXFILEPATH;

/**
 * @author billy
 * @version 1.0
 * @description: content功能处理实现类
 * @date 2022/11/21 17:43
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    IFolderService folderService;

    @Autowired
    IRecordMangeService recordMange;

    @Autowired
    FolderDao folderDao;

    @Autowired
    MisTypeDao typeDao;

    @Autowired
    MisColumnDao columnDao;

    @Autowired
    CmsRenditionDao renditionDao;

    @Autowired
    CmsFormatDao formatDao;

    @Autowired
    private MisCmsAutolinkDao misCmsAutolinkDao;

    @Autowired
    private MisCmsAutolinkDetailDao misCmsAutolinkDetailDao;

    @Autowired
    private MisCmsAutolinkConditionlDao misCmsAutolinkConditionlDao;

    @Autowired
    private MisCmsTagDao misCmsTagDao;

    @Autowired
    private MisCmsVersionDao misCmsVersionDao;

    @Autowired
    EntityManager entityManager;

    @Autowired
    private MisUserDao misUserDao;

    @Autowired
    private MisTypeDao misTypeDao;

    @Autowired
    private CmsAutolinkDefineDao cmsAutolinkDefineDao;

    @Autowired
    private MisCmsAutolinkDao autolinkDao;

    @Autowired
    IDictionaryService dictionaryService;


    @Override
    @Transactional
    public boolean upLoadFile(MultipartFile[] files, String treeData, String tableList, String folderId, String isAll, String isAutoLink) throws JSONException {
        //解析接受到的json数据
        List<NodeDto> tree = JSONArray.parseArray(treeData, NodeDto.class);
        JSONArray jsonTables = JSONArray.parseArray(tableList);
        JSONArray newTables = JSON.parseArray(jsonTables.toJSONString());
        JSONArray newTables1 = JSON.parseArray(jsonTables.toJSONString());
        JSONArray newTables2 = JSON.parseArray(jsonTables.toJSONString());
        addNewFolderByTree(tree, folderId);

        if("true".equals(isAutoLink)){
            for (int i = 0; i < jsonTables.size(); i++) {
                Map<String,JSONObject> jsonMap = getPropertyJson(jsonTables,newTables,i,isAll);
                addPropertyByAutoLink(jsonMap.get("tempJson"),folderId,jsonMap.get("j"));
            }
        }else{
            if(tree.size()>0) {
                for (NodeDto n : tree) {
                    if (n.getChildren().size() > 0) {
                        for (int i = 0; i < jsonTables.size(); i++) {
                            Map<String,JSONObject> jsonMap = getPropertyJson(jsonTables,newTables1,i,isAll);
                            if (jsonMap.get("tempJson").get("path").toString().contains("/")) {
                                addPropertyByFolder(tree, jsonMap.get("tempJson").get("path").toString().split("/"), jsonMap.get("tempJson"), jsonMap.get("j"));
                            }
                        }
                    } else {
                        for (int i = 0; i < jsonTables.size(); i++) {
                            Map<String,JSONObject> jsonMap = getPropertyJson(jsonTables,newTables1,i,isAll);
                            if (n.getPath().equals("/" + jsonMap.get("tempJson").get("path").toString())) {
                                addProperty(jsonMap.get("tempJson"), folderId, jsonMap.get("j"));
                            }
                        }
                    }
                }
            }
        }

        if (null == files) {
            return false;
        }
        if (files.length > 0) {
            int i = 0;
            for (MultipartFile mul : files) {
                //String fileName = getRenditionName(mul.getOriginalFilename());
                String cmsVersionId=SerialNumberUtils.getTableSequence("mis_cms_version");
                String cmsRenditionId=SerialNumberUtils.getTableSequence("cms_rendition");

                //System.out.println(mul.getName() + "===" + fileName);
                JSONObject j = (JSONObject)jsonTables.get(i);

                String fileName=j.getString("tableId")+"-"+cmsRenditionId+"-"+cmsVersionId+(mul.getOriginalFilename()).substring((mul.getOriginalFilename()).indexOf("."));
                i = i+1;
                //String liunxDir = "192.168.50.174/test";
                String liunxDir = LINUXFILEPATH;
                CmsRendition rendition = new CmsRendition();
                //rendition.setCmsFileLocation("\\\\" + liunxDir + "/" + fileName);
                rendition.setCmsRenditionId(cmsRenditionId);
                rendition.setCmsFileLocation(liunxDir + "/" + fileName);
                rendition.setCmsRenditionFile(fileName);
                rendition.setCmsCreatorUserId(ContextHolder.getUserId());
                rendition.setCmsFormatId(getFormatId(fileName));
                rendition.setCmsIsPrimary("Y");
                rendition.setCmsRenditionDate(new Timestamp(System.currentTimeMillis()));
                rendition.setMisRecordId(j.getString("recordId"));
                rendition.setMisTypeId(j.getString("tableId"));
                rendition = renditionDao.saveAndFlush(rendition);

                //rendition文件上传同时往version插入初始版本数据
                MisCmsVersion misCmsVersion=new MisCmsVersion();
                misCmsVersion.setCmsVersionId(cmsVersionId);
                misCmsVersion.setMisTypeId(j.getString("tableId"));
                misCmsVersion.setMisRecordId(j.getString("recordId"));
                misCmsVersion.setCmsVersionNo(ConstUtils.FIRSTVERSION);
                misCmsVersion.setCmsFileLocation( liunxDir + "/" + fileName);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateNormal = sdf.format(new Date());
                misCmsVersion.setCmsCreationDate(dateNormal);
                misCmsVersion.setVersionStatus(ConstUtils.UNLOCKED);
                misCmsVersion.setCmsCreatorUserId(ContextHolder.getUserId());
                misCmsVersion.setFileSize(String.valueOf(mul.getSize()));
                misCmsVersionDao.saveAndFlush(misCmsVersion);
                if(null != rendition.getCmsRenditionId()){
                    try {
                        String editFileName=j.getString("tableId")+"-"+cmsRenditionId+"-"+cmsVersionId;
                        MultipartFile newMul= SmbUtil.fileRename(mul,editFileName);
                        new JSchUtil().uploadFile(liunxDir, newMul.getInputStream(), newMul.getName());
                        //SmbUtil.smbPut(liunxDir,mul,fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                       // return false;
                    }
                }
            }

        }
        return true;
    }

    private Map<String,JSONObject> getPropertyJson(JSONArray jsonTables, JSONArray newTables,int i,String isAll){
        Map<String,JSONObject> map = new HashMap<>();
        JSONObject tempJson;
        JSONObject j;
        if (isAll.equals("true")) {
            tempJson = (JSONObject) newTables.get(0);
            j = (JSONObject) jsonTables.get(i);
            if (i != 0) {
                tempJson.remove("path");
                tempJson.remove("fileId");
                tempJson.put("path", j.getString("path"));
                tempJson.put("fileId", j.getString("fileId"));
            }
        } else {
            tempJson = (JSONObject) jsonTables.get(i);
            j = (JSONObject) jsonTables.get(i);
        }
        map.put("tempJson",tempJson);
        map.put("j",j);
        return map;
    }

    private String getFormatId(String fileName){
        String[] strArr = fileName.split("\\.");
        List<CmsFormat> formats = formatDao.getCmsFormatsByCmsFormatDosExt(strArr[strArr.length-1]);
        return formats.get(0).getCmsFormatId();

    }

    private String getRenditionName(String fileName){
        String currentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String[] strArr = fileName.split("\\.");
        return fileName.substring(0,fileName.length()-strArr[strArr.length-1].length()-1) + "_" + currentDate + "." + strArr[strArr.length-1];
    }

    private void addNewFolderByTree(List<NodeDto> tree, String folderId) {
        for (NodeDto n : tree) {
            if (n.getChildren().size() > 0) {
                if (!n.getMisFolderName().equals("")) {
                    MisFolder temp = null;
                    temp = folderDao.findMisFolderByMisFolderParentIdAndMisFolderName(folderId,n.getMisFolderName());
                    if(null == temp){
                        temp = folderService.addFolder(folderId, n.getMisFolderName());
                    }
                    if (null != temp) {
                        n.setId(temp.getMisFolderId());
                        addNewFolderByTree(n.getChildren(), temp.getMisFolderId());
                    }
                } else {
                    addNewFolderByTree(n.getChildren(), folderId);
                }
            }//Todo:忘了要做什么了
        }
    }

    private void addPropertyByFolder(List<NodeDto> tree,String[] folders,JSONObject prop,JSONObject jo){
        for(NodeDto n:tree){
                if(folders[0].equals(n.getMisFolderName()) && folders.length==2){
                    if(null != n.getId() && !"".equals(n.getId())){
                        addProperty(prop,n.getId(),jo);
                    }
                }else if(folders[0].equals(n.getMisFolderName())){
                    if(n.getChildren().size()!=0){
                        addPropertyByFolder(n.getChildren(), ArrayUtils.remove(folders,0),prop,jo);
                    }
                }
        }
    }
    private JSONObject addPropertyByAutoLink(JSONObject json,String folderId,JSONObject jo){
        JSONObject jsonObject = addProperty(json,"folderId",jo);
        String typeID = jsonObject.getString("tableId");
        List<MisCmsAutolink> links = autolinkDao.getMisCmsAutolinksByMisTypeIdAndMisFolderId(typeID,folderId);
//        List<MisCmsAutolink> links = autolinkDao.getMisCmsAutolinksByMisTypeIdAndMisFolderId("0028000000000074","0015000000000114");
        if(links.size() > 0){
            MisCmsAutolink autolink = null;
            for(MisCmsAutolink l:links){
                if(null != l){
                    if(getRecordList(typeID,l.getConditions(),"folderId")){
                        autolink = l;
                        break;
                    }
                }
            }
            //找到对应的文件夹id
            if(null != autolink){
                List<MisCmsAutolinkDetail> details = autolink.getDetails();
                int level = 1;
                String targetFolderId = getTargetFolderId(details,folderId,folderId,level,json,autolink.getCmsIsCreateFolder());
                //更新目标记录的folderID
                updateRecordFolderId(jsonObject.getString("recordId"),json.getString("tableId"),targetFolderId);
                jsonObject.remove("folder_id");
                jsonObject.put("folder_id",targetFolderId);
            }else{
                updateRecordFolderId(jsonObject.getString("recordId"),json.getString("tableId"),folderId);
                jsonObject.remove("folder_id");
                jsonObject.put("folder_id",folderId);
            }
        }else {
            updateRecordFolderId(jsonObject.getString("recordId"),json.getString("tableId"),folderId);
            jsonObject.remove("folder_id");
            jsonObject.put("folder_id",folderId);
        }
        return jsonObject;
    }

    private boolean updateRecordFolderId(String recordId, String typeId, String newFolderId){
        MisType type = typeDao.findById(typeId).get();
        String permissionId = folderDao.getPermissionIdByFolderId(newFolderId);
        StringBuilder querySQL = new StringBuilder("update "+type.getMisTypeName()+"_s set folder_id = '" +
                newFolderId + "', mis_permission_id = '" +
                permissionId + "' ");
        querySQL.append(" where id= '"+recordId+"'");
        Query query = entityManager.createNativeQuery(querySQL.toString());
        query.executeUpdate();
        return true;
    }

    private String getTargetFolderId(List<MisCmsAutolinkDetail> details,String targetFolderId,String folderId,int level,JSONObject json,String isCreate){
        for(MisCmsAutolinkDetail d: details){
            if(level == d.getCmsFolderLevel()){
                String columnId = d.getMisColumnId();
                String columnValue = getColumnValue(columnId,json);
                MisFolder folder = folderDao.findMisFolderByMisFolderParentIdAndMisFolderName(targetFolderId,columnValue);
                if(null != folder){
                    targetFolderId = folder.getMisFolderId();
                    level = level + 1;
                    getTargetFolderId(details,targetFolderId,folderId,level,json,isCreate);
                }else{
                    if("Y".equals(isCreate)){
                        MisFolder newFolder = new MisFolder();
                        newFolder.setMisFolderId(SerialNumberUtils.getTableSequence("mis_folder"));
                        newFolder.setMisFolderParentId(targetFolderId);
                        newFolder.setMisFolderName(columnValue);
                        newFolder.setDelFlag("0");
                        Optional<MisFolder> parentFolder = folderDao.findById(targetFolderId);
                        if(null != parentFolder){
                            newFolder.setMisPermissionId(parentFolder.get().getMisPermissionId());
                        }
                        newFolder = folderDao.saveAndFlush(newFolder);
                        if(null != newFolder){
                            level = level + 1;
                            targetFolderId = newFolder.getMisFolderId();
                            getTargetFolderId(details,targetFolderId,folderId,level,json,isCreate);
                        }
                    }else{
                        targetFolderId = folderId;
                    }
                    break;
                }
            }
        }
        return targetFolderId;
    }

    private String getColumnValue(String columnId,JSONObject json){
        MisColumn column =  columnDao.findById(columnId).get();
        String columnValue = "";
        //todo:需要处理不同类型字段的value
        switch (column.getMisColumnInputType()){
            case "2":
                columnValue = getDicValue(column,json.getString(column.getMisColumnName()));
                break;
            default:
                columnValue = json.getString(column.getMisColumnName());
        }

        return columnValue;
    }

    private String getDicValue(MisColumn column,String value){
        List<DicDto> dicList = dictionaryService.getDicListById(column.getMisColumnDictionary());
        String dicValue = "";
        if(dicList.size() > 0){
            for(DicDto d:dicList){
                if(value.contains(d.getKey())){
                    dicValue = d.getValue();
                }
            }
        }else{
            dicValue = value;
        }
        return dicValue;
    }

    private boolean getRecordList(String typeId,List<MisCmsAutolinkCondition> conditions,String folderId) {
        MisType type = typeDao.findById(typeId).get();
        StringBuilder querySQL = new StringBuilder("SELECT id,");
        String col = "mis_permission_id";
        querySQL.append(col);
        querySQL.append(" FROM "+type.getMisTypeName()+"_s where folder_id = '"+folderId+"'");
        StringBuilder whereSQL = new StringBuilder("");
        for(MisCmsAutolinkCondition c:conditions){
            String colName0 = c.getMisColumnId();
            String condition0 = c.getCmsAutolinkCondition();
            String value0 = c.getCmsAutolinkValue();
            if(!"".equals(colName0) && !"".equals(condition0) && !"".equals(value0)){
                MisColumn column =  columnDao.findById(colName0).get();
                String columnName = column.getMisColumnName();
                String contion = getCondition(condition0);
                if("".equals(c.getCmsAutolinkConditionRel()) || "1".equals(c.getCmsAutolinkConditionRel())){
                    if("like".equals(contion)){
                        whereSQL.append(" and ("+columnName +" like '%"+value0+"%')");
                    }else if("=".equals(contion)){
                        whereSQL.append(" and ("+columnName +" "+contion+" '"+value0+"')");
                    }else if("<>".equals(contion)){
                        whereSQL.append(" and ("+columnName +" "+contion+" '"+value0+"')");
                    }else{
                        whereSQL.append(" and ("+columnName +" "+contion+" "+value0+")");
                    }
                }else{
                    if("like".equals(contion)){
                        whereSQL.append(" or ("+columnName +" like '%"+value0+"%')");
                    }else if("=".equals(contion)){
                        whereSQL.append(" or ("+columnName +" "+contion+" '"+value0+"')");
                    }else if("<>".equals(contion)){
                        whereSQL.append(" or ("+columnName +" "+contion+" '"+value0+"')");
                    }else{
                        whereSQL.append(" or ("+columnName +" "+contion+" "+value0+")");
                    }
                }

            }
        }
        Query query = entityManager.createNativeQuery(querySQL.toString() + whereSQL.toString());
        List list = query.getResultList();
        if(list.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    private String getCondition(String condition){
        String str = "";
        if("0".equals(condition)){
            str = "=";
        }else if("1".equals(condition)){
            str = "<>";
        }else if("2".equals(condition)){
            str = "like";
        }else if("3".equals(condition)){
            str = ">=";
        }else{
            str = "<=";
        }
        return str;
    }

    private JSONArray addProperties(JSONArray jsonObjects) throws JSONException {
        for(int i =0;i< jsonObjects.size();i++){
            JSONObject json = (JSONObject)jsonObjects.get(i);
            String tableId=json.getString("tableId");
            String tableName=recordMange.getTableNameById(tableId);
            String tableSeqId= SerialNumberUtils.getTableSequence(tableName);
            tableSeqId="'"+tableSeqId+"'";
            json.remove("tableId");
            String path = json.getString("path");
            json.remove("path");
            Set<String> set = json.keySet();
            Iterator dataLs=set.iterator();
            String colArr="";
            if(dataLs.hasNext()) {
                while (dataLs.hasNext()) {
                    colArr += dataLs.next() + ",";
                }
            }
            colArr=colArr.substring(0, colArr.length() - 1);
            String[] colArrary=colArr.split(",");
            String dataArr="";
            for(int j=0;j<colArrary.length;j++){
                dataArr+="'"+json.getString(colArrary[j])+"'"+",";
            }
            String permissionId = folderDao.getPermissionIdByFolderId(json.getString("folder_id"));
            String userId = ContextHolder.getUserId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateNormal = sdf.format(new Date());
            dataArr=dataArr.substring(0, dataArr.length() - 1);
            //String currentTime=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(System.currentTimeMillis());
            String insertSql="insert into "+tableName+" (id,creation_date,creator_user_id,updated_date,updated_user_id,mis_permission_id,"+colArr+")"
                    +" values"+"("+tableSeqId+",'"+dateNormal+"','"+userId+"','"+dateNormal+"','"+userId+"','"+permissionId+"',"+dataArr+")";
            int insertFlag=recordMange.insertTableData(insertSql);
            json.put("path",path);
            if(insertFlag == 1){
                json.put("tableId",tableId);
                json.put("propId",tableSeqId);
            }

        }
        return jsonObjects;
    }

    private JSONObject addProperty(JSONObject json,String folderId,JSONObject jo) {
            String tableId=json.getString("tableId");
            String tableName=recordMange.getTableNameById(tableId);
            String tableSeqId= SerialNumberUtils.getTableSequence(tableName);
            String recordId= tableSeqId;
            String path = json.getString("path");
            String fileId = json.getString("fileId");
            String tags = json.getString("tags");
            String tableColumns = json.getString("tableColumns");
            tableSeqId="'"+tableSeqId+"'";
            if(null != json.getString("recordId")){
                json.remove("recordId");
            }
            json.remove("tableId");
            json.remove("folder_id");
            json.remove("path");
            json.remove("fileId");
            json.remove("tags");
            json.remove("tableColumns");
            json.put("folder_id",folderId);
            Set<String> set = json.keySet();
            Iterator dataLs=set.iterator();
            String colArr="";
            if(dataLs.hasNext()) {
                while (dataLs.hasNext()) {
                    colArr += dataLs.next() + ",";
                }
            }
            colArr=colArr.substring(0, colArr.length() - 1);
            String[] colArrary=colArr.split(",");
            String dataArr="";
            for(int i=0;i<colArrary.length;i++){
                dataArr+="'"+json.getString(colArrary[i])+"'"+",";
            }
            String permissionId = folderDao.getPermissionIdByFolderId(json.getString("folder_id"));
            String userId = ContextHolder.getUserId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateNormal = sdf.format(new Date());
            dataArr=dataArr.substring(0, dataArr.length() - 1);
            //String currentTime=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(System.currentTimeMillis());
            String insertSql="insert into "+tableName+" (id,creation_date,creator_user_id,updated_date,updated_user_id,mis_permission_id,"+colArr+")"
                    +" values"+"("+tableSeqId+",'"+dateNormal+"','"+userId+"','"+dateNormal+"','"+userId+"','"+permissionId+"',"+dataArr+")";
            int insertFlag=recordMange.insertTableData(insertSql);
            if(insertFlag == 1){
                json.put("tableId",tableId);
                json.put("recordId",recordId);
            }
            json.put("path",path);
            json.put("fileId",fileId);
            json.put("tags",tags);
            json.put("tableColumns",tableColumns);
            if(null != tags && tags.length() > 0){
                JSONArray tagDataObjArr = JSON.parseArray(tags);
                List<MisCmsTag> misCmsTagList=new ArrayList<>();
                if(tagDataObjArr.size()>0) {
                    for (int i = 0; i < tagDataObjArr.size(); i++) {
                        com.alibaba.fastjson.JSONObject obj=  tagDataObjArr.getJSONObject(i);
                        MisCmsTag misCmsTag=new MisCmsTag();
                        misCmsTag.setCmsTagId(SerialNumberUtils.getTableSequence("mis_cms_tag"));
                        misCmsTag.setMisTypeId(tableId);
                        misCmsTag.setMisRecordId(recordId);
                        misCmsTag.setCmsTag(obj.getString("cmsTag"));
                        misCmsTagList.add(misCmsTag);
                    }
                    misCmsTagDao.saveAllAndFlush(misCmsTagList);
                }
            }
            jo.put("recordId",recordId);
            jo.remove("tableId");
            jo.put("tableId",tableId);
        return json;
    }


    @Override
    @Transactional
    public boolean saveAutoLink(org.json.JSONObject json) {
        try {
            List<MisFolder> folderList=new ArrayList<>();
            Map<String, String> mapValue=new HashMap<String, String>();
            StringBuffer columnBuffer=new StringBuffer();
            String selectTable = json.getString("selectTable");
            String[] tableArr=selectTable.split("-");
            selectTable=tableArr[0];
            mapValue.put("0",tableArr[1]);
            boolean checked=json.getBoolean("checked");
            String folderId = json.getString("folderId");
            String selectColumn1 = json.getString("selectColumn1");
            String[] selectColumnArr=selectColumn1.split("-");
            selectColumn1=selectColumnArr[0];
            mapValue.put("1",selectColumnArr[1]);
            columnBuffer.append(selectColumnArr[1]+",");
            String levelData=json.getString("levelData");
            Object levelDataObj = JSON.parse(levelData);
            JSONArray levelDataArr = (JSONArray)levelDataObj;
            List<MisCmsAutolinkDetail> misCmsAutolinkDetailList=new ArrayList<>();
            List<MisCmsAutolinkCondition> misCmsAutolinkConditionList=new ArrayList<>();
            String OneConditionName=json.getString("OneConditionName");
            String OneFilter=json.getString("OneFilter");
            String OneConditionValue=json.getString("OneConditionValue");
            String conditionData=json.getString("conditionData");
            Object conditionDataObj = JSON.parse(conditionData);
            JSONArray conditionDataObjArr = (JSONArray)conditionDataObj;
            String cmsAutolinkId = SerialNumberUtils.getTableSequence("mis_cms_autolink");
            MisCmsAutolink misCmsAutolink=new MisCmsAutolink();
            misCmsAutolink.setCmsAutolinkId(cmsAutolinkId);
            misCmsAutolink.setMisTypeId(selectTable);
            misCmsAutolink.setMisFolderId(folderId);
            misCmsAutolink.setCmsIsCreateFolder(checked==true?"Y":"N");
            if(levelDataArr.size()>0){
                for(int i=0;i<levelDataArr.size();i++){
                    MisCmsAutolinkDetail misCmsAutolinkDetail =new MisCmsAutolinkDetail();
                    String cmsAutolinkDetailId = SerialNumberUtils.getTableSequence("mis_cms_autolink_Detail");
                    com.alibaba.fastjson.JSONObject obj=  levelDataArr.getJSONObject(i);
                    misCmsAutolinkDetail.setCmsAutolinkDetailId(cmsAutolinkDetailId);
                    misCmsAutolinkDetail.setCmsAutolinkId(cmsAutolinkId);
                    Integer level= (Integer) obj.get("level");
                    misCmsAutolinkDetail.setCmsFolderLevel(level+2);
                    String[] columnIdArr=String.valueOf(obj.get("columnId")).split("-");
                    mapValue.put(String.valueOf(level+2),columnIdArr[1]);
                    columnBuffer.append(columnIdArr[1]+",");
                    misCmsAutolinkDetail.setMisColumnId(columnIdArr[0]);
                    misCmsAutolinkDetailList.add(misCmsAutolinkDetail);
                }
                MisCmsAutolinkDetail misCmsAutolinkDetailFirst =new MisCmsAutolinkDetail();
                misCmsAutolinkDetailFirst.setCmsAutolinkDetailId(SerialNumberUtils.getTableSequence("mis_cms_autolink_Detail"));
                misCmsAutolinkDetailFirst.setCmsAutolinkId(cmsAutolinkId);
                misCmsAutolinkDetailFirst.setCmsFolderLevel(1);
                misCmsAutolinkDetailFirst.setMisColumnId(selectColumn1);
                misCmsAutolinkDetailList.add(misCmsAutolinkDetailFirst);
            }else{
                MisCmsAutolinkDetail misCmsAutolinkDetailFirst =new MisCmsAutolinkDetail();
                misCmsAutolinkDetailFirst.setCmsAutolinkDetailId(SerialNumberUtils.getTableSequence("mis_cms_autolink_Detail"));
                misCmsAutolinkDetailFirst.setCmsAutolinkId(cmsAutolinkId);
                misCmsAutolinkDetailFirst.setCmsFolderLevel(1);
                misCmsAutolinkDetailFirst.setMisColumnId(selectColumn1);
                misCmsAutolinkDetailList.add(misCmsAutolinkDetailFirst);
            }
            if(conditionDataObjArr.size()>0){
                for(int i=0;i<conditionDataObjArr.size();i++){
                    MisCmsAutolinkCondition misCmsAutolinkCondition =new MisCmsAutolinkCondition();
                    String cmsAutolinkConditionId = SerialNumberUtils.getTableSequence("mis_cms_autolink_condition");
                    com.alibaba.fastjson.JSONObject obj=  conditionDataObjArr.getJSONObject(i);
                    misCmsAutolinkCondition.setCmsAutolinkConditionId(cmsAutolinkConditionId);
                    misCmsAutolinkCondition.setCmsAutolinkId(cmsAutolinkId);
                    misCmsAutolinkCondition.setMisColumnId((String) obj.get("condColumnId"));
                    misCmsAutolinkCondition.setCmsAutolinkCondition((String) obj.get("condFilter"));
                    misCmsAutolinkCondition.setCmsAutolinkValue((String) obj.get("condValue"));
                    misCmsAutolinkCondition.setCmsAutolinkConditionRel((String) obj.get("condRadio"));
                    misCmsAutolinkConditionList.add(misCmsAutolinkCondition);
                }
                MisCmsAutolinkCondition misFirst =new MisCmsAutolinkCondition();
                misFirst.setCmsAutolinkConditionId(SerialNumberUtils.getTableSequence("mis_cms_autolink_condition"));
                misFirst.setCmsAutolinkId(cmsAutolinkId);
                misFirst.setMisColumnId(OneConditionName);
                misFirst.setCmsAutolinkCondition(OneFilter);
                misFirst.setCmsAutolinkValue(OneConditionValue);
                misFirst.setCmsAutolinkConditionRel("");
                misCmsAutolinkConditionList.add(misFirst);
            }else{
                MisCmsAutolinkCondition misFirst =new MisCmsAutolinkCondition();
                misFirst.setCmsAutolinkConditionId(SerialNumberUtils.getTableSequence("mis_cms_autolink_condition"));
                misFirst.setCmsAutolinkId(cmsAutolinkId);
                misFirst.setMisColumnId(OneConditionName);
                misFirst.setCmsAutolinkCondition(OneFilter);
                misFirst.setCmsAutolinkValue(OneConditionValue);
                misFirst.setCmsAutolinkConditionRel("");
                misCmsAutolinkConditionList.add(misFirst);
            }
            //记录上级folderId
            String lastFolderId="";
            //记录每次遍历的folderId
            String firstFolderId="";
            String columnAppend=columnBuffer.toString();
            columnAppend=columnAppend.substring(0,columnAppend.length()-1);
           /* String permissionId=folderDao.getPermissionIdByFolderId(folderId);
            String querySql="select "+columnAppend+" from "+tableArr[1]+"_s "+" where folder_id="+folderId+" and mis_permission_id="+permissionId;
            List typeData = entityManager.createNativeQuery(querySql).getResultList();
            if(typeData!=null&&typeData.size()>0){
                for(int i=0;i<typeData.size();i++){
                    String[] columnArr=columnAppend.split(",");
                    lastFolderId="";
                    int h=0;
                    if(i==0) {//第一条数据编历
                        for (int k = 0; k < columnArr.length; k++) {
                            Object[] objSecondArr = (Object[]) typeData.get(k);

                            if (h == 0) {
                                for (int l = 0; l < columnArr.length; l++) {
                                    MisFolder misFolder = new MisFolder();
                                    String fid = SerialNumberUtils.getTableSequence("mis_folder");
                                    misFolder.setMisFolderId(fid);
                                    misFolder.setMisFolderParentId(lastFolderId == "" ? folderId : lastFolderId);
                                    misFolder.setMisPermissionId(folderDao.getPermissionIdByFolderId(folderId));
                                    misFolder.setMisFolderName((String) objSecondArr[l]);
                                    misFolder.setDelFlag(ConstUtils.DELFLAG);
                                    folderList.add(misFolder);
                                    lastFolderId = fid;
                                    if (l == 0) {
                                        firstFolderId = fid;
                                    }
                                }
                                lastFolderId = "";
                            } else {
                                if (k != 0) {
                                    for (int l = 1; l < columnArr.length; l++) {
                                        MisFolder misFolder = new MisFolder();
                                        String fid = SerialNumberUtils.getTableSequence("mis_folder");
                                        misFolder.setMisFolderId(fid);
                                        misFolder.setMisFolderParentId(lastFolderId == "" ? firstFolderId : lastFolderId);
                                        misFolder.setMisPermissionId(folderDao.getPermissionIdByFolderId(folderId));
                                        misFolder.setMisFolderName((String) objSecondArr[l]);
                                        misFolder.setDelFlag(ConstUtils.DELFLAG);
                                        folderList.add(misFolder);
                                        lastFolderId = fid;
                                    }
                                }
                                lastFolderId = "";
                            }
                            h++;
                        }
                    }else{
                        Object[] objSecondArr = (Object[]) typeData.get(i);
                        MisFolder misFolder = new MisFolder();
                        String fid = SerialNumberUtils.getTableSequence("mis_folder");
                        misFolder.setMisFolderId(fid);
                        misFolder.setMisFolderParentId(lastFolderId == "" ? folderId : lastFolderId);
                        misFolder.setMisPermissionId(folderDao.getPermissionIdByFolderId(folderId));
                        misFolder.setMisFolderName((String) objSecondArr[0]);
                        misFolder.setDelFlag(ConstUtils.DELFLAG);
                        folderList.add(misFolder);
                        lastFolderId = fid;
                        for (int k = 0; k < columnArr.length; k++) {
                            Object[] objSecondNewArr = (Object[]) typeData.get(k);
                            for (int l = 1; l < columnArr.length; l++) {
                                MisFolder misFolder1 = new MisFolder();
                                String fid1 = SerialNumberUtils.getTableSequence("mis_folder");
                                misFolder1.setMisFolderId(fid1);
                                misFolder1.setMisFolderParentId(lastFolderId == "" ? firstFolderId : lastFolderId);
                                misFolder1.setMisPermissionId(folderDao.getPermissionIdByFolderId(folderId));
                                misFolder1.setMisFolderName((String) objSecondNewArr[l]);
                                misFolder1.setDelFlag(ConstUtils.DELFLAG);
                                folderList.add(misFolder1);
                                lastFolderId = fid1;
                            }
                            lastFolderId = fid;
                        }
                    }

                }

            }*/
            /*if(checked) {
                for (String key : mapValue.keySet()) {
                    MisFolder misFolder = new MisFolder();
                    String value = mapValue.get(key);
                    if ("0".equals(key)) {

                    } else {

                        String fid = SerialNumberUtils.getTableSequence("mis_folder");
                        misFolder.setMisFolderId(fid);
                        misFolder.setMisFolderParentId(lastFolderId);
                        misFolder.setMisPermissionId(folderDao.getPermissionIdByFolderId(folderId));
                        misFolder.setMisFolderName(value);
                        misFolder.setDelFlag(ConstUtils.DELFLAG);
                        folderList.add(misFolder);
                        lastFolderId = fid;
                    }
                }
            }else{*/
                /*for (String key : mapValue.keySet()) {
                    MisFolder misFolder = new MisFolder();
                    String value = mapValue.get(key);
                    if ("0".equals(key)) {
                        String fid = SerialNumberUtils.getTableSequence("mis_folder");
                        misFolder.setMisFolderId(fid);
                        misFolder.setMisFolderParentId(folderId);
                        misFolder.setMisPermissionId(folderDao.getPermissionIdByFolderId(folderId));
                        misFolder.setMisFolderName(value);
                        misFolder.setDelFlag(ConstUtils.DELFLAG);
                        folderList.add(misFolder);
                    }
                }*/
            //}
            //folderDao.saveAllAndFlush(folderList);
            misCmsAutolinkDao.saveAndFlush(misCmsAutolink);
            misCmsAutolinkDetailDao.saveAllAndFlush(misCmsAutolinkDetailList);
            misCmsAutolinkConditionlDao.saveAllAndFlush(misCmsAutolinkConditionList);
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean insertTableData(org.json.JSONObject json) {
        try {
            String tableId=json.getString("tableId");
            String tableName=recordMange.getTableNameById(tableId);
            String tableSeqId=SerialNumberUtils.getTableSequence(tableName);
            tableSeqId="'"+tableSeqId+"'";
            String tagData=json.getString("tagData");
            Object tagDataObj = JSON.parse(tagData);
            JSONArray tagDataObjArr = (JSONArray)tagDataObj;
            String oneTage=json.getString("oneTage");
            json.remove("tagData");
            json.remove("oneTage");
            json.remove("tableId");
            Iterator dataLs=json.keys();
            String colArr="";
            if(dataLs.hasNext()) {
                while (dataLs.hasNext()) {
                    colArr += dataLs.next() + ",";
                }
            }
            colArr=colArr.substring(0, colArr.length() - 1);
            String[] colArrary=colArr.split(",");
            String dataArr="";
            for(int i=0;i<colArrary.length;i++){
                dataArr+="'"+json.getString(colArrary[i])+"'"+",";
            }
            String permissionId = folderDao.getPermissionIdByFolderId(json.getString("folder_id"));
            String userId = ContextHolder.getUserId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateNormal = sdf.format(new Date());
            dataArr=dataArr.substring(0, dataArr.length() - 1);
            //String currentTime=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(System.currentTimeMillis());
            String insertSql="insert into "+tableName+" (id,creation_date,creator_user_id,updated_date,updated_user_id,mis_permission_id,"+colArr+")"
                    +" values"+"("+tableSeqId+",'"+dateNormal+"','"+userId+"','"+dateNormal+"','"+userId+"','"+permissionId+"',"+dataArr+")";
            int insertFlag=recordMange.insertTableData(insertSql);
            List<MisCmsTag> misCmsTagList=new ArrayList<>();
            if(tagDataObjArr.size()>0) {
                for (int i = 0; i < tagDataObjArr.size(); i++) {
                    com.alibaba.fastjson.JSONObject obj=  tagDataObjArr.getJSONObject(i);
                    MisCmsTag misCmsTag=new MisCmsTag();
                    misCmsTag.setCmsTagId(SerialNumberUtils.getTableSequence("mis_cms_tag"));
                    misCmsTag.setMisTypeId(tableId);
                    misCmsTag.setMisRecordId(tableSeqId);
                    misCmsTag.setCmsTag(obj.getString("tagValue"));
                    misCmsTagList.add(misCmsTag);
                }
                MisCmsTag misCmsTag=new MisCmsTag();
                misCmsTag.setCmsTagId(SerialNumberUtils.getTableSequence("mis_cms_tag"));
                misCmsTag.setMisTypeId(tableId);
                misCmsTag.setMisRecordId(tableSeqId);
                misCmsTag.setCmsTag(oneTage);
                misCmsTagList.add(misCmsTag);
            }else {
                MisCmsTag misCmsTag=new MisCmsTag();
                misCmsTag.setCmsTagId(SerialNumberUtils.getTableSequence("mis_cms_tag"));
                misCmsTag.setMisTypeId(tableId);
                misCmsTag.setMisRecordId(tableSeqId);
                misCmsTag.setCmsTag(oneTage);
                misCmsTagList.add(misCmsTag);
            }
            misCmsTagDao.saveAllAndFlush(misCmsTagList);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public PageDataDto queryVersionData(String data) {
        PageDataDto versionDto = null;
        try {
            org.json.JSONObject json = new org.json.JSONObject(data);
            org.json.JSONObject pageState = json.getJSONObject("pageState");
            org.json.JSONObject sortState = json.getJSONObject("sortModel");
            String recordId=json.getString("recordId");
            int pageNum = pageState.getInt("page")-1;
            int pageSize =  pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            String sortField ="cms_version_id";
            if(sortState.getString("sort").equalsIgnoreCase("asc")){
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
            }else{
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
            }
            //where if(?1!='',mis_user_name=?1,1=1)
            /*String userId="";
            if(userNameWhere!=null||userNameWhere!=""){
                userId=misUserDao.queryUserByName(userNameWhere);
            }*/
            Page<MisCmsVersion> versionLs=misCmsVersionDao.findAllVersion(recordId,pageable);
            List<MisCmsVersion> versionlist=versionLs.getContent();
            Session session = entityManager.unwrap(org.hibernate.Session.class);
            //jpa默认在实体属性set之后自动提交到数据库，这里不需要提交到库，直接清缓存
            session.clear();

            if(versionlist!=null&&versionlist.size()>0){
                for(int i=0;i<versionlist.size();i++){
                    String userName=versionlist.get(i).getCmsCreatorUserId()!=null?misUserDao.getUserInfoByUserId(versionlist.get(i).getCmsCreatorUserId()).get(0).getMisUserName():"";
                    versionlist.get(i).setCmsUserName(userName);
                    if(versionlist.get(i).getCmsCreatorUserId()==null||"".equals(versionlist.get(i).getCmsCreatorUserId())){
                        versionlist.get(i).setVersionStatus("未锁");
                    }else if(versionlist.get(i).getCmsCreatorUserId()!=null&&versionlist.get(i).getCmsCreatorUserId().equals(ContextHolder.getUserId())){

                        versionlist.get(i).setVersionStatus("未锁");
                    }else{
                        versionlist.get(i).setVersionStatus("已锁");
                    }
                }
            }
            versionDto = new PageDataDto();
            versionDto.setData(versionlist);
            versionDto.setTotal(versionLs.getTotalElements());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return versionDto;
    }
    @Transactional
    @Override
    public byte[] downloadFile(String data) {
        byte[] buffer=null;
        try {
            org.json.JSONObject json =new org.json.JSONObject(data);
            String misTypeId=json.getString("misTypeId");
            String misRecordId=json.getString("misRecordId");
            String downFile=json.getString("downFile");
            String versionId=json.getString("versionId");
            boolean isDisabled=json.getBoolean("isDisabled");
            int index = downFile.indexOf("/");
            //根据第一个点的位置 获得第二个点的位置
            index = downFile.indexOf("/", index + 1);
            String fileDir=downFile.substring(0, index);
            buffer =new JSchUtil().download(downFile,fileDir);
            if(isDisabled) {
                String misTypeName = misTypeDao.getTableNameById(misTypeId);
                //更新自定义表锁状态和锁对应的用户
                String updateTableSql = "update " + misTypeName + " set lock_status='" + ConstUtils.LOCKED + "',lock_user='" + ContextHolder.getUserId() + "' where id='" + misRecordId + "' ";
                javax.persistence.Query query = entityManager.createNativeQuery(updateTableSql);
                query.executeUpdate();
                //version表记录保存
                misCmsVersionDao.updateVersion(versionId, ConstUtils.LOCKED, ContextHolder.getUserId());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }
    @Transactional
    @Override
    public boolean UpLoadCheckFile(MultipartFile files, String misTypeId, String misRecordId, String cmsVersionNo, String cmsFileLocation) {
        try {
            int index = cmsFileLocation.indexOf("/");
            //根据第一个点的位置 获得第二个点的位置
            index = cmsFileLocation.indexOf("/", index + 1);
            String fileDir=cmsFileLocation.substring(0, index);
            //获取文件后缀与之前上传文件类型对比,如不同,当作rendition副本上传,不生成version;如果类型相同,生成累加version版本,并变更rendition文件后缀
            String oldFileType=cmsFileLocation.substring(cmsFileLocation.lastIndexOf("."));
            if (!files.isEmpty()) {
                //对文件的全名进行截取然后在后缀名进行删选。
                int begin = files.getOriginalFilename().indexOf(".");
                int last = files.getOriginalFilename().length();
                //获得文件后缀名
                String newFileType = files.getOriginalFilename().substring(begin, last);
                if (newFileType.endsWith(oldFileType)) {
                    String cmsVersionId=SerialNumberUtils.getTableSequence("mis_cms_version");
                    //rendition文变件后缀名更
                    String newRenditionName=cmsFileLocation.substring(0,cmsFileLocation.lastIndexOf("-"))+"-"+(cmsVersionId)+oldFileType;
                    //new JSchUtil().reName(cmsFileLocation,newRenditionName);
                    //修改rendition表对应的路径
                    String renditionId=cmsFileLocation.substring(cmsFileLocation.indexOf("-")+1,cmsFileLocation.lastIndexOf("-")-1);
                    renditionDao.updateRendition(renditionId,newRenditionName);
                    String fileName=cmsFileLocation.substring(cmsFileLocation.lastIndexOf("/")+1,cmsFileLocation.lastIndexOf("-")+1)+cmsVersionId;
                    MultipartFile newMul= SmbUtil.fileRename(files,fileName);
                    new JSchUtil().uploadFile(fileDir, newMul.getInputStream(), newMul.getName());
                    //new JSchUtil().uploadFile(fileDir,files.getInputStream(),files.getOriginalFilename());
                    MisCmsVersion misCmsVersion=new MisCmsVersion();
                    misCmsVersion.setCmsVersionId(cmsVersionId);
                    misCmsVersion.setMisTypeId(misTypeId);
                    misCmsVersion.setMisRecordId(misRecordId);
                    misCmsVersion.setCmsVersionNo(""+(Integer.valueOf(cmsVersionNo.substring(0,1))+1)+".0");
                    misCmsVersion.setCmsFileLocation(newRenditionName);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateNormal = sdf.format(new Date());
                    misCmsVersion.setCmsCreationDate(dateNormal);
                    misCmsVersion.setVersionStatus(ConstUtils.UNLOCKED);
                    misCmsVersion.setCmsCreatorUserId(ContextHolder.getUserId());
                    misCmsVersion.setFileSize(String.valueOf(files.getSize()));
                    misCmsVersionDao.saveAndFlush(misCmsVersion);
                    String misTypeName=misTypeDao.getTableNameById(misTypeId);
                    String updateTableSql="update "+misTypeName+ " set lock_status='"+ConstUtils.UNLOCKED+"' where id='"+misRecordId+"' and lock_user='"+ContextHolder.getUserId()+"' ";
                    javax.persistence.Query query= entityManager.createNativeQuery(updateTableSql);
                    query.executeUpdate();
                } else {
                        String fileName = getRenditionName(files.getOriginalFilename());
                        String liunxDir = LINUXFILEPATH;
                        CmsRendition rendition = new CmsRendition();
                        rendition.setCmsFileLocation(liunxDir + "/" + fileName);
                        rendition.setCmsRenditionFile(fileName);
                        rendition.setCmsCreatorUserId(ContextHolder.getUserId());
                        rendition.setCmsFormatId(getFormatId(fileName));
                        rendition.setCmsIsPrimary("N");
                        rendition.setCmsRenditionDate(new Timestamp(System.currentTimeMillis()));
                        rendition.setMisRecordId(misRecordId);
                        rendition.setMisTypeId(misTypeId);
                        rendition = renditionDao.save(rendition);
                        try {
                            new JSchUtil().uploadFile(liunxDir, files.getInputStream(), files.getOriginalFilename());
                        } catch (IOException e) {
                            //throw new RuntimeException(e);
                            return false;
                        }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    @Transactional
    @Override
    public boolean delVersion(String data) {
        try {
            org.json.JSONObject json = new org.json.JSONObject(data);
            String cmsVersionId=json.getString("cmsVersionId");
            String misTypeId=json.getString("misTypeId");
            String misRecordId=json.getString("misRecordId");
            misCmsVersionDao.deleteById(cmsVersionId);
            String misTypeName=misTypeDao.getTableNameById(misTypeId);
            String updateTableSql="update "+misTypeName+ " set lock_status='"+ConstUtils.UNLOCKED+"' where id='"+misRecordId+"' and lock_user='"+ContextHolder.getUserId()+"' ";
            javax.persistence.Query query= entityManager.createNativeQuery(updateTableSql);
            query.executeUpdate();
            return true;
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    @Override
    public PageDataDto getAllAutoLink(String folderId, String typeId, String folderName, org.json.JSONObject pageState, org.json.JSONObject sort) {
        PageDataDto autolinkDto = null;
        try {
            int pageNum = pageState.getInt("page")-1;
            int pageSize =  pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            String sortField ="cms_autolink_id";
            if(sort.getString("sort").equalsIgnoreCase("asc")){
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
            }else{
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
            }
            if(typeId!=null&&typeId!=""){
                String[] typeIdArr=typeId.split("-");
                typeId=typeIdArr[0];
            }
            Page<AutolinkDetail> autolinkDetail=cmsAutolinkDefineDao.getAllAutoLink(folderId,typeId,pageable);
            List<AutolinkDetail> autolinkDetailLs=autolinkDetail.getContent();
            autolinkDto =new PageDataDto();
            autolinkDto.setData(autolinkDetailLs);
            autolinkDto.setTotal(autolinkDetail.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return autolinkDto;
    }
    @Transactional
    @Override
    public boolean deleteAutolinkById(String data) {
        try {
            org.json.JSONObject json = new org.json.JSONObject(data);
            String cmsAutolinkConditionId=json.getString("cmsAutolinkConditionId");
            String misColumnId=json.getString("misColumnId");
            String misFolderId=json.getString("misFolderId");
            String misTypeId=json.getString("misTypeId");
            String cmsAutolinkLevel=json.getString("cmsAutolinkLevel");
            String cmsAutolinkId=json.getString("cmsAutolinkId");
            String cmsColumnId="";
            if(cmsAutolinkLevel=="1"){//如果是1级及下面子级都删除
                cmsColumnId="";
                cmsAutolinkConditionId="";
                misCmsAutolinkDao.deleteById(cmsAutolinkId);
                misCmsAutolinkDetailDao.deleteByCmsAutolinkId(cmsAutolinkId,cmsColumnId);
                misCmsAutolinkConditionlDao.deleteByContidionId(cmsAutolinkConditionId,cmsAutolinkId,misColumnId);
            }else{//如果不是1级,先判断它这级下面有没有子级，有子级也要删除
                List<Object[]> countLevel=misCmsAutolinkDetailDao.countAutoLinkDetail(cmsAutolinkId);
                if(countLevel.size()>=Integer.valueOf(cmsAutolinkLevel)){
                    for(int i=0;i<countLevel.size();i++){
                        Integer level= (Integer) countLevel.get(i)[0];
                        cmsColumnId= (String) countLevel.get(i)[1];
                        if(level>=Integer.valueOf(cmsAutolinkLevel)){
                            misCmsAutolinkDetailDao.deleteByCmsAutolinkId(cmsAutolinkId,cmsColumnId);
                            misCmsAutolinkConditionlDao.deleteByContidionId(cmsAutolinkConditionId,cmsAutolinkId,cmsColumnId);
                        }
                    }
                }

            }
            return true;
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }
    @Transactional
    @Override
    public boolean editAutolink(String data) {
        try {
            org.json.JSONObject json = new org.json.JSONObject(data);
            String cmsAutolinkConditionId=json.getString("cmsAutolinkConditionId");
            String misColumnId=json.getString("misColumnId");
            String cmsAutolinkCondition=json.getString("cmsAutolinkCondition");
            if(cmsAutolinkCondition!=null&&"is".equals(cmsAutolinkCondition)){
                cmsAutolinkCondition="0";
            }else if("is not".equals(cmsAutolinkCondition)){
                cmsAutolinkCondition="1";
            }else if("contains".equals(cmsAutolinkCondition)){
                cmsAutolinkCondition="2";
            }else if("greater".equals(cmsAutolinkCondition)){
                cmsAutolinkCondition="3";
            }else if("less".equals(cmsAutolinkCondition)){
                cmsAutolinkCondition="4";
            }
            String cmsAutolinkValue=json.getString("cmsAutolinkValue");
            misCmsAutolinkConditionlDao.editAutolink(cmsAutolinkConditionId,misColumnId,cmsAutolinkCondition,cmsAutolinkValue);
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return false;
        }
        return true;
    }
    @Transactional
    public boolean UpLoadBigFile(String targetFile,  String filename, String misTypeId, String misRecordId, String cmsVersionNo, String cmsFileLocation,String fileSize) {
        try {
            int index = cmsFileLocation.indexOf("/");
            //根据第一个点的位置 获得第二个点的位置
            index = cmsFileLocation.indexOf("/", index + 1);
            String fileDir=cmsFileLocation.substring(0, index);
            //获取文件后缀与之前上传文件类型对比,如不同,当作rendition副本上传,不生成version;如果类型相同,生成累加version版本,并变更rendition文件后缀
            String oldFileType=cmsFileLocation.substring(cmsFileLocation.lastIndexOf("."));
            if (!targetFile.isEmpty()) {
                //对文件的全名进行截取然后在后缀名进行删选。
                //int begin = files.getOriginalFilename().indexOf(".");
                //int last = files.getOriginalFilename().length();
                //获得文件后缀名
                String newFileType = targetFile.substring(targetFile.lastIndexOf("."));
                if (newFileType.endsWith(oldFileType)) {
                    String cmsVersionId=SerialNumberUtils.getTableSequence("mis_cms_version");
                    //rendition文变件后缀名更
                    String newRenditionName=cmsFileLocation.substring(0,cmsFileLocation.lastIndexOf("-"))+"-"+(cmsVersionId)+oldFileType;
                    //new JSchUtil().reName(cmsFileLocation,newRenditionName);
                    //修改rendition表对应的路径
                    String renditionId=cmsFileLocation.substring(cmsFileLocation.indexOf("-")+1,cmsFileLocation.lastIndexOf("-")-1);
                    renditionDao.updateRendition(renditionId,newRenditionName);
                    String fileName=cmsFileLocation.substring(cmsFileLocation.lastIndexOf("/")+1,cmsFileLocation.lastIndexOf("-")+1)+cmsVersionId;
                   // MultipartFile newMul= SmbUtil.fileRename((MultipartFile) new File(targetFile),fileName);

                    String subFileName=targetFile.substring(0,targetFile.lastIndexOf("\\"));
                    String fileType=targetFile.substring(targetFile.lastIndexOf("."));
                    String updateFile=subFileName+"\\"+fileName+fileType;
                    File soureFile=new File(targetFile);
                    File newFile=new File(updateFile);
                    boolean isExits=soureFile.renameTo(newFile);
                    new JSchUtil().put(updateFile,LINUXFILEPATH+"/"+newFile.getName());
                    MisCmsVersion misCmsVersion=new MisCmsVersion();
                    misCmsVersion.setCmsVersionId(cmsVersionId);
                    misCmsVersion.setMisTypeId(misTypeId);
                    misCmsVersion.setMisRecordId(misRecordId);
                    misCmsVersion.setCmsVersionNo(""+(Integer.valueOf(cmsVersionNo.substring(0,1))+1)+".0");
                    misCmsVersion.setCmsFileLocation(newRenditionName);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateNormal = sdf.format(new Date());
                    misCmsVersion.setCmsCreationDate(dateNormal);
                    misCmsVersion.setVersionStatus(ConstUtils.UNLOCKED);
                    misCmsVersion.setCmsCreatorUserId(ContextHolder.getUserId());
                    misCmsVersion.setFileSize(fileSize);
                    misCmsVersionDao.saveAndFlush(misCmsVersion);
                    String misTypeName=misTypeDao.getTableNameById(misTypeId);
                    String updateTableSql="update "+misTypeName+ " set lock_status='"+ConstUtils.UNLOCKED+"' where id='"+misRecordId+"' and lock_user='"+ContextHolder.getUserId()+"' ";
                    javax.persistence.Query query= entityManager.createNativeQuery(updateTableSql);
                    query.executeUpdate();
                } else {
                    String fileName = getRenditionName(targetFile);
                    String liunxDir = LINUXFILEPATH;
                    CmsRendition rendition = new CmsRendition();
                    rendition.setCmsFileLocation(liunxDir + "/" + fileName);
                    rendition.setCmsRenditionFile(fileName);
                    rendition.setCmsCreatorUserId(ContextHolder.getUserId());
                    rendition.setCmsFormatId(getFormatId(fileName));
                    rendition.setCmsIsPrimary("N");
                    rendition.setCmsRenditionDate(new Timestamp(System.currentTimeMillis()));
                    rendition.setMisRecordId(misRecordId);
                    rendition.setMisTypeId(misTypeId);
                    rendition = renditionDao.save(rendition);
                    try {
                        new JSchUtil().put(targetFile,LINUXFILEPATH+"/"+filename);
                    } catch (Exception e) {
                        //throw new RuntimeException(e);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    @Transactional
    @Override
    public void updateVersionData(String versionId,String misTypeId, String misRecordId) {
        try {
            String misTypeName = misTypeDao.getTableNameById(misTypeId);
            //更新自定义表锁状态和锁对应的用户
            String updateTableSql = "update " + misTypeName + " set lock_status='" + ConstUtils.LOCKED + "',lock_user='" + ContextHolder.getUserId() + "' where id='" + misRecordId + "' ";
            Query query = entityManager.createNativeQuery(updateTableSql);
            query.executeUpdate();
            //version表记录保存
            misCmsVersionDao.updateVersion(versionId, ConstUtils.LOCKED, ContextHolder.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public boolean doContentData(String targetFile,  String filename,String treeData,String tableList,String folderId,String isAll,String isAutoLink,String fileSize){
        //解析接受到的json数据
        List<NodeDto> tree = JSONArray.parseArray(treeData, NodeDto.class);
        JSONArray jsonTables = JSONArray.parseArray(tableList);
        JSONArray newTables = JSON.parseArray(jsonTables.toJSONString());
        JSONArray newTables1 = JSON.parseArray(jsonTables.toJSONString());
        JSONArray newTables2 = JSON.parseArray(jsonTables.toJSONString());
        addNewFolderByTree(tree, folderId);

        if("true".equals(isAutoLink)){
            for (int i = 0; i < jsonTables.size(); i++) {
                Map<String,JSONObject> jsonMap = getPropertyJson(jsonTables,newTables,i,isAll);
                addPropertyByAutoLink(jsonMap.get("tempJson"),folderId,jsonMap.get("j"));
            }
        }else{
            if(tree.size()>0) {
                for (NodeDto n : tree) {
                    if (n.getChildren().size() > 0) {
                        for (int i = 0; i < jsonTables.size(); i++) {
                            Map<String,JSONObject> jsonMap = getPropertyJson(jsonTables,newTables1,i,isAll);
                            if (jsonMap.get("tempJson").get("path").toString().contains("/")) {
                                addPropertyByFolder(tree, jsonMap.get("tempJson").get("path").toString().split("/"), jsonMap.get("tempJson"), jsonMap.get("j"));
                            }
                        }
                    } else {
                        for (int i = 0; i < jsonTables.size(); i++) {
                            Map<String,JSONObject> jsonMap = getPropertyJson(jsonTables,newTables1,i,isAll);
                            if (n.getPath().equals("/" + jsonMap.get("tempJson").get("path").toString())) {
                                addProperty(jsonMap.get("tempJson"), folderId, jsonMap.get("j"));
                            }
                        }
                    }
                }
            }
        }
        if (!targetFile.isEmpty()) {
            int i = 0;
            JSONObject j = (JSONObject)jsonTables.get(i);
            //String fileName = getRenditionName(mul.getOriginalFilename());
            String cmsVersionId=SerialNumberUtils.getTableSequence("mis_cms_version");
            String cmsRenditionId=SerialNumberUtils.getTableSequence("cms_rendition");
            String fileName=j.getString("tableId")+"-"+cmsRenditionId+"-"+cmsVersionId+targetFile.substring(targetFile.lastIndexOf("."));
            String liunxDir = LINUXFILEPATH;
            CmsRendition rendition = new CmsRendition();
            rendition.setCmsRenditionId(cmsRenditionId);
            rendition.setCmsFileLocation(liunxDir + "/" + fileName);
            rendition.setCmsRenditionFile(fileName);
            rendition.setCmsCreatorUserId(ContextHolder.getUserId());
            rendition.setCmsFormatId(getFormatId(fileName));
            rendition.setCmsIsPrimary("Y");
            rendition.setCmsRenditionDate(new Timestamp(System.currentTimeMillis()));
            rendition.setMisRecordId(j.getString("recordId"));
            rendition.setMisTypeId(j.getString("tableId"));
            rendition = renditionDao.saveAndFlush(rendition);

            //rendition文件上传同时往version插入初始版本数据
            MisCmsVersion misCmsVersion=new MisCmsVersion();
            misCmsVersion.setCmsVersionId(cmsVersionId);
            misCmsVersion.setMisTypeId(j.getString("tableId"));
            misCmsVersion.setMisRecordId(j.getString("recordId"));
            misCmsVersion.setCmsVersionNo(ConstUtils.FIRSTVERSION);
            misCmsVersion.setCmsFileLocation( liunxDir + "/" + fileName);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateNormal = sdf.format(new Date());
            misCmsVersion.setCmsCreationDate(dateNormal);
            misCmsVersion.setVersionStatus(ConstUtils.UNLOCKED);
            misCmsVersion.setCmsCreatorUserId(ContextHolder.getUserId());
            misCmsVersion.setFileSize(fileSize);
            misCmsVersionDao.saveAndFlush(misCmsVersion);
            if(null != rendition.getCmsRenditionId()){
                try {
                    String subFileName=targetFile.substring(0,targetFile.lastIndexOf("/"));
                    String fileType=targetFile.substring(targetFile.lastIndexOf("."));
                    String updateFile=subFileName+"/"+fileName;
                    File soureFile=new File(targetFile);
                    File newFile=new File(updateFile);
                    boolean isExits=soureFile.renameTo(newFile);
                    String returnFlag=new JSchUtil().put(updateFile,LINUXFILEPATH+"/"+newFile.getName());
                    newFile.delete();
                } catch (Exception e) {
                    //throw new RuntimeException(e);
                    return false;
                }
            }
        }

        return true;
    }
}


