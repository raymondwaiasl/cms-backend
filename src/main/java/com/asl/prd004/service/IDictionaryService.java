package com.asl.prd004.service;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.entity.MisDataDictionary;
import org.json.JSONObject;

import java.util.List;

public interface IDictionaryService {
    PageDataDto getAllDictName(PageableDto pageable);

    PageDataDto<Object[]> queryDictDetails(String id, JSONObject... params);

    PageDataDto<Object[]> querySqlDictDetails(String Sql, JSONObject... params);

    MisDataDictionary getDicByDicId(String id);

    TypeIdDto createDictionary(MisDataDictionary dataDict);

    boolean updateDictionary(MisDataDictionary dataDict);

    boolean createDictionaryItem(String dicId, String keyId, String key, String value);

    boolean updateDictionaryItem(String dicId, String keyId, String key, String value);

    boolean delDictionary(String dicId);

    boolean delDictionaryItem(String id);

    List<DicDto> getDicListById(String id);

    ResultGenerator verifyPropSql(String sql);

     List<DicDto> getDicEnumAndSqlListByDicId(String id);


}
