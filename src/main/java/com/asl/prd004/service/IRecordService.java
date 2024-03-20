package com.asl.prd004.service;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.*;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisImportHist;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface IRecordService {

    List<MisColumn> getColumnList(String columns);

    List getRecordList(String typeId,  List<MisColumn> columnList, org.json.JSONArray conditionData ,String folderId) throws JSONException;

    List<ColumnInputTypeDTO> getPropertiesLabel(String tableId);
    
    SearchRecordVO searchRecord(SearchRecordDTO dto);

    List<MisColumn> getColumnList(SearchRecordDTO dto);

    List<ColumnInputDTO> getProperties(String tableId,String recordId) throws Exception;

    List<ColumnInputDTO> getRefProperties(String tableId,String recordId, String misCrossRefId) throws Exception;

    List getPropertiesValue(String tableId, String id);

    String getRefRecordId(TypeRefDto typeRefDto, String id);

    List getRefPropertiesValue(TypeRefDto typeRefDto, String recordId);

    Boolean saveProperties(String id, String typeId, JSONObject arr) throws JSONException;

    Boolean deleteProperties(String id,String typeId);

    Boolean deleteRefProperties(String id,String typeId);

    void importExcelData(StringBuffer appendSql);

    void delImportTtable(String tableName);

    void insertImportConfig(MisImportHist misImportHist);

    Boolean isSubscribe(String recordId);


    Boolean isEdit(String tableId, String id);

    Boolean isDelete(String tableId,String id);

    Boolean hasChildrenTable(String tableId);

    List getRecords(String typeId,String columns,String dateFrom,String dateTo);
    List<MisColumn> findByMisTypeId(String misTypeId);
    List<MisColumn> findByMisTypeId(String misTypeId,String allowSearch);

    PageDataDto getFolderRecordByPage(String typeId, String folderId, List<MisColumn> columnList, org.json.JSONArray sortModel, JSONObject pageState);

    Map<String,Object> getRecordListByRecIds(RecordIdListDto dto);

    ResultGenerator getRecordHistoryByRecId(RecordIdDto dto);

    ResultGenerator getRecordAuditDetailByRecId(RecordIdDto dto);

    ResultGenerator getRecordComparisonByRecId(RecordComparisonDto dto);

    ResultGenerator getDefaultRecordList(RecordListPageableDto dto);
	
    ResultGenerator getRecordEditListByRecIds(RecordIdDto dto);
}
