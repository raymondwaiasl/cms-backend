package com.asl.prd004.service;

import com.asl.prd004.dto.*;
import com.asl.prd004.entity.BiToolConfig;
import org.json.JSONObject;

import java.util.List;

/**
 * @description: TODO
 * @author: billy
 * @date: 2023/6/5 16:27
 * @version: 1.0
 */
public interface BiToolService {
    PageDataDto getAllBiToolByPage(JSONObject... params);

    List<BiToolConfig> getAllBiTool();

    BiToolConfig getBiTool(String misBiConfigId);

    List<TypeListDto> queryTableData();

    List<DicDto> queryColumnData(String typeId);

    boolean editBiTool(JSONObject json);

    boolean deleteBiTool(JSONObject json);

    List<CountColumnDTO> countTableColumnData(JSONObject json);

    List<CountColumnDTO> countTableColumnDataByDate(JSONObject json);

    List<Object[]> countWorkflowData();

    List<Object[]> countWorkflowDataByDate();
}
