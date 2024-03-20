package com.asl.prd004.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.DictionaryDao;
import com.asl.prd004.dao.DictionaryValueDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisDataDictionary;
import com.asl.prd004.entity.MisDataDictionaryValue;
import com.asl.prd004.service.IDictionaryService;
import com.asl.prd004.utils.CastEntity;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DictionaryServiceImpl implements IDictionaryService {
    @Autowired
    private DictionaryDao dictionaryDao;
    @Autowired
    private DictionaryValueDao dictionaryValueDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public PageDataDto getAllDictName(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "key":
                    sortField = "misDdId";
                    break;
                case "value":
                    sortField = "misDdName";
                    break;
            }
            if (pageable.getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize());
        }
        Page<DicDto> dicDtoPage = dictionaryDao.findAllDictName(page);
        List<DicDto> dicDtoList = dicDtoPage.getContent();
        PageDataDto dicDto = new PageDataDto();
        dicDto.setData(dicDtoList);
        dicDto.setTotal(dicDtoPage.getTotalElements());
        return dicDto;
    }

    @Override
    public MisDataDictionary getDicByDicId(String dicId){
        List<MisDataDictionary> dictionaries = dictionaryDao.findAllById(Collections.singleton(dicId));
        if(dictionaries.size()>0){
            return  dictionaries.get(0);
        }else{return  null;}

    }

    @Override
    public PageDataDto queryDictDetails(String id, JSONObject... params) {
        PageDataDto dicDto = null;
        try {
            JSONObject pageState = params[0];
            int pageNum = pageState.getInt("page")-1;
            int pageSize =  pageState.getInt("pageSize");
            Pageable pageable = PageRequest.of(pageNum, pageSize);

            if (params.length==2 && params[1].length()!=0) {
                JSONObject sortState = params[1];
                String sortField ="keyId";
                if(sortState.getString("sort").equalsIgnoreCase("asc")){
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).ascending());
                }else{
                    pageable = PageRequest.of(pageNum, pageSize, Sort.by(sortField).descending());
                }
            }
            Page<Object[]> dicLs = dictionaryDao.queryDictDetails(id,pageable);
            List<DictionaryDto> dictDTO= CastEntity.castEntity(dicLs.getContent(),DictionaryDto.class);
            dicDto = new PageDataDto();
            dicDto.setData(dictDTO);
            dicDto.setTotal(dicLs.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dicDto;
    }

    @Override
    public PageDataDto<Object[]> querySqlDictDetails(String sql, JSONObject... params) {
        List<DicDto> sqlOptions = getSqlOptions(sql);
        JSONObject pageState = params[0];
        int pageNum = pageState.optInt("page",1)-1;
        int pageSize =  pageState.optInt("pageSize",10);
        List<DicDto> results = ListUtil.page(pageNum, pageSize, sqlOptions);
        PageDataDto dicDto = new PageDataDto();
        dicDto.setData(results);
        dicDto.setTotal(sqlOptions.size());
        return dicDto;
    }

    private List<DicDto> getSqlOptions(String sql){
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<DicDto> result = Collections.EMPTY_LIST;
        if(CollectionUtil.isNotEmpty(list)){
            Map<String,Object> first = list.get(0);
            Object[] objects = first.keySet().toArray();
            String key = (String) objects[0];
            String value = objects.length == 1 ? (String) objects[0]:(String) objects[1];
            Function<Map<String, Object>,Object> keyGetter = (m) -> m.get(key);
            Function<Map<String, Object>,Object> valueGetter = (m) -> m.get(value);
            AtomicInteger i = new AtomicInteger();
            result =
                list.stream().map((m) -> {
                    DicDto dto = new DicDto();
                    dto.setKeyId(String.valueOf(i.getAndIncrement()));
                    dto.setKey(String.valueOf(keyGetter.apply(m)));
                    dto.setValue(String.valueOf(valueGetter.apply(m)));
                    return dto;
                }).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public List<DicDto> getDicEnumAndSqlListByDicId(String DicId) {
        List<DicDto> dicList = new ArrayList<>();
        MisDataDictionary misDataDictionary = dictionaryDao.findById(DicId).get();
        if(misDataDictionary.getMisDdPropType().equals(1)){
            dicList = dictionaryDao.getDicListById(DicId);
        }
        if(misDataDictionary.getMisDdPropType().equals(2)){
            dicList = getSqlOptions(misDataDictionary.getMisDdPropSql());
        }
        return  dicList;
    }

    @Override
    public  TypeIdDto createDictionary(MisDataDictionary dataDict){
        MisDataDictionary res = dictionaryDao.saveAndFlush(dataDict);
        TypeIdDto typeIdDto = new TypeIdDto();
        typeIdDto.setId(res.getMisDdId());
        return  typeIdDto;
    }

    @Override
    public  boolean updateDictionary(MisDataDictionary dataDict){
        dictionaryDao.updateProp(dataDict.getMisDdId(),dataDict.getMisDdName(),dataDict.getMisDdPropSql());
        return  true;
    }

    @Override
    @Transactional
    public boolean delDictionary(String dicId) {
        try {
            dictionaryDao.deleteById(dicId);
            dictionaryValueDao.delDicDetailsById(dicId);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }
        return  true;
    }

    @Override
    public  boolean createDictionaryItem(String dicId, String keyId, String key, String value){
        List<MisDataDictionary> dictionaries = dictionaryDao.findAllById(Collections.singleton(dicId));
        if(dictionaries.size()>0) {
            MisDataDictionaryValue misDataDictionaryValue = new MisDataDictionaryValue();
            misDataDictionaryValue.setMisDdId(dicId);
            misDataDictionaryValue.setMisDdvKey(key);
            misDataDictionaryValue.setMisDdvValue(value);
            dictionaryValueDao.saveAndFlush(misDataDictionaryValue);
            return  true;
        }else{
            return  false;
        }
    }

    @Override
    public  boolean updateDictionaryItem(String dicId, String keyId, String key, String value){
        List<MisDataDictionary> dictionaries = dictionaryDao.findAllById(Collections.singleton(dicId));
        List<MisDataDictionaryValue> dictionaryValues = dictionaryValueDao.findAllById(Collections.singleton(keyId));
        if(dictionaries.size()>0 && dictionaryValues.size()>0) {
            MisDataDictionaryValue misDataDictionaryValue = new MisDataDictionaryValue();
            misDataDictionaryValue.setMisDdId(dicId);
            misDataDictionaryValue.setMisDdvId(keyId);
            misDataDictionaryValue.setMisDdvKey(key);
            misDataDictionaryValue.setMisDdvValue(value);
            dictionaryValueDao.saveAndFlush(misDataDictionaryValue);
            return true;
        }else{
            return  false;
        }
    }

    @Override
    public  boolean delDictionaryItem(String keyId){
        dictionaryValueDao.deleteById(keyId);
        return  true;
    }

    @Override
    public List<DicDto> getDicListById(String id) {
        Optional<MisDataDictionary> byId = dictionaryDao.findById(id);
        if(byId.isPresent()){
            MisDataDictionary misDataDictionary = byId.get();
            if(Objects.equals(2,misDataDictionary.getMisDdPropType())){
                return getSqlOptions(misDataDictionary.getMisDdPropSql());
            }
        }
        return dictionaryDao.getDicListById(id);
    }

    @Override
    public ResultGenerator verifyPropSql(String sql) {
        String errMsg = "";
        if(StrUtil.isBlank(sql)){
            errMsg = "sql is blank";
        }
        if(StrUtil.isNotBlank(sql)){
            try {
                jdbcTemplate.queryForList(sql);
            }
            catch (Exception e){
                errMsg = "bad SQL grammar";
            }
        }
        return new ResultGenerator(200,errMsg);
    }
}
