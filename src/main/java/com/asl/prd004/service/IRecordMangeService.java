package com.asl.prd004.service;

import com.asl.prd004.dto.ColumnNestDTO;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.vo.AuditLogVO;
import org.json.JSONException;
import com.asl.prd004.vo.CalcColumnQueryResultVO;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IRecordMangeService {

    List<DicDto> getTableName();

    List<ColumnNestDTO> getTableColumn(String tableId);

    int insertTableData(String insertSql);

    int insertTableData(Collection<String> insertSql);

    String getTableNameById(String tableId);

    List<Object[]> searchRecord(JSONObject json);

    List<Object[]> getRecordType(JSONObject json);

    String createRecord(JSONObject json, HttpServletRequest request);

    String updateRecord(JSONObject json, HttpServletRequest request);

    String changeRecordLink(JSONObject json,HttpServletRequest request);

    String deleteRecord(JSONObject json, HttpServletRequest request);

    List searchRecordData(JSONObject json);

    List getRecord(JSONObject json);

    AuditLogVO insertTable(String data) throws JSONException;
	
    CalcColumnQueryResultVO calcColumnQueryResult(String misColumnId, Map<String,Object> param);
    int updateTableData(String updateSql);
}
