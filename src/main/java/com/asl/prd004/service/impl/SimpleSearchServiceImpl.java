package com.asl.prd004.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dao.*;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.*;
import com.asl.prd004.enums.TableMgmtColumnInputType;
import com.asl.prd004.service.IDictionaryService;
import com.asl.prd004.service.ISimpleSearchService;
import com.asl.prd004.service.IWidgetService;
import com.asl.prd004.utils.SQLExecutor;
import com.asl.prd004.utils.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class SimpleSearchServiceImpl implements ISimpleSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSearchServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SimpleSearchDao simpleSearchDao;

    @Autowired
    private SimpleSearchItemDao simpleSearchItemDao;

    @Autowired
    private IDictionaryService dictionaryService;

    @Autowired
    private MisTypeDao typeDao;

    @Override
    public PageDataDto getSimpleSearchListPageable(PageableDto pageable){
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            if (pageable.getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize());
        }
        Page<SimpleSearchResponseDto> searchPage = simpleSearchDao.findAllSimpleSearchPageable(page);
        List<SimpleSearchResponseDto> searchPageContent = searchPage.getContent();
        PageDataDto widgetPageDto = new PageDataDto();
        widgetPageDto.setData(searchPageContent);
        widgetPageDto.setTotal(searchPage.getTotalElements());
        return widgetPageDto;
    }

//    @Override
//    public List<WidgetDto> getWidgetList(){
//        List<WidgetDto> widgetDtoList = widgetDao.findAllWidget();
//        return widgetDtoList;
//    }

    @Override
    public SimpleSearchResponseDto getSimpleSearchById(TypeIdDto dto){
        MisSimpleSearch simpleSearch = simpleSearchDao.getMisSimpleSearchByMisSimpleSearchId(dto.getId());
        List<MisSimpleSearchItem> simpleSearchItems = simpleSearchItemDao.getMisSimpleSearchItemsByMisSimpleSearchId(dto.getId());
        List<SimpleSearchItemDto> itemDtos = new ArrayList<>();
        for(MisSimpleSearchItem item:simpleSearchItems){
            SimpleSearchItemDto itemDto = new SimpleSearchItemDto(item);
            if(null != item.getItemDictionary()){
                itemDto.setItemLs(dictionaryService.getDicListById(item.getItemDictionary()));
            }
            itemDtos.add(itemDto);
        }
        SimpleSearchResponseDto simpleSearchDto = new SimpleSearchResponseDto(simpleSearch,itemDtos);
        return  simpleSearchDto;
    }

    @Override
    public boolean addSimpleSearch(SimpleSearchInputDto inputDto){
        MisSimpleSearch simpleSearch = new MisSimpleSearch();
        simpleSearch.setMisSimpleSearchName(inputDto.getMisSimpleSearchName());
        simpleSearch.setMisSimpleSearchSql(inputDto.getMisSimpleSearchSql());
        simpleSearch = simpleSearchDao.saveAndFlush(simpleSearch);
        if(null != simpleSearch.getMisSimpleSearchId()){
            if(inputDto.getItems().size() > 0){
                for(SimpleSearchItemDto i:inputDto.getItems()){
                    MisSimpleSearchItem item = new MisSimpleSearchItem();
                    item.setMisSimpleSearchId(simpleSearch.getMisSimpleSearchId());
                    item.setItemName(i.getItemName());
                    item.setItemType(i.getInputType());
                    item.setItemDictionary(i.getItemDictionary());
                    item.setItemColSize(i.getColSize());
                    item.setItemRowSize(i.getRowSize());
                    simpleSearchItemDao.saveAndFlush(item);
                }
            }
        }
        return  true;
    }

    @Override
    public boolean editSimpleSearch(SimpleSearchInputDto inputDto){
        MisSimpleSearch simpleSearch = new MisSimpleSearch();
        simpleSearch.setMisSimpleSearchId(inputDto.getMisSimpleSearchId());
        simpleSearch.setMisSimpleSearchName(inputDto.getMisSimpleSearchName());
        simpleSearch.setMisSimpleSearchSql(inputDto.getMisSimpleSearchSql());
        simpleSearch = simpleSearchDao.saveAndFlush(simpleSearch);
        if(null != simpleSearch.getMisSimpleSearchId()){
            simpleSearchItemDao.deleteByMisSimpleSearchId(simpleSearch.getMisSimpleSearchId());
            if(inputDto.getItems().size() > 0){
                for(SimpleSearchItemDto i:inputDto.getItems()){
                    MisSimpleSearchItem item = new MisSimpleSearchItem();
                    item.setMisSimpleSearchId(simpleSearch.getMisSimpleSearchId());
                    item.setItemName(i.getItemName());
                    item.setItemType(i.getInputType());
                    item.setItemDictionary(i.getItemDictionary());
                    item.setItemColSize(i.getColSize());
                    item.setItemRowSize(i.getRowSize());
                    simpleSearchItemDao.saveAndFlush(item);
                }
            }
        }
        return  true;
    }

    @Override
    public boolean deleteSimpleSearch(TypeIdDto dto){
        simpleSearchDao.deleteById(dto.getId());
        simpleSearchItemDao.deleteByMisSimpleSearchId(dto.getId());
        return true;
    }

    @Override
    public ResultGenerator simpleSearchRecord(SimpleSearchRecordInputDto dto) {
        Map<String, Object> inputName2value = new HashMap<>();
        Map<String, Object> map = JSON.parseObject(dto.getData());
        Set<String> keys = map.keySet();

        MisSimpleSearch simpleSearch = simpleSearchDao.getMisSimpleSearchByMisSimpleSearchId(dto.getSimpleSearchId());
        String simpleSearchQuerySql = simpleSearch.getMisSimpleSearchSql();
        //给simpleSearchSql自动添加Id字段，并且返回主表的名称
        StringBuilder sqlBuilder = new StringBuilder(simpleSearchQuerySql);
        String tableAlias = SQLExecutor.getTableAlias(simpleSearchQuerySql);
        System.out.println(tableAlias + ".id");
        Pattern pattern = Pattern.compile(!tableAlias.equals("") ? tableAlias + ".id" : "id");
        Matcher matcher = pattern.matcher(simpleSearchQuerySql);
        Pattern pattern1 = Pattern.compile("lower\\(");
        Matcher matcher1 = pattern1.matcher(simpleSearchQuerySql.toLowerCase());
        if(!matcher.find()){
            sqlBuilder.insert(simpleSearchQuerySql.toLowerCase().indexOf("select ") + 7, !tableAlias.equals("") ? tableAlias + ".id, " : "id, ");
        }
        for(String key:keys){
            System.out.println("value======" + map.get(key));
            Map<String, Object> m = JSON.parseObject(map.get(key).toString());
            if(!"0".equals(m.get("input_type").toString())){
                if("1".equals(m.get("input_type").toString())){
                    if(!matcher.find()){
                        inputName2value.put(key, m.get("value").toString().trim());
                    }else{
                        inputName2value.put(key, m.get("value").toString().toLowerCase().trim());
                    }
                }else{
                    inputName2value.put(key, m.get("value"));
                }

            }
        }
        String tableName = SQLExecutor.getTableName(sqlBuilder.toString());
        MisType type = typeDao.getMisTypeByMisTypeName(tableName.replace("_s",""));

        simpleSearchQuerySql = StrUtil.format(sqlBuilder.toString(),"$value(",")",inputName2value);

        List<MisColumn> misColumnList = new ArrayList<>();

        List<String> columns = SQLExecutor.getColumns(simpleSearchQuerySql);
        for(String s:columns){
            MisColumn misColumn = new MisColumn();
            misColumn.setMisColumnId(s);
            misColumn.setMisColumnName(s);
            if(s.equals("id")){
                misColumn.setMisColumnLabel("Id");
            }else {
                misColumn.setMisColumnLabel(s);
            }
            misColumn.setMisColumnType("1");
            misColumnList.add(misColumn);
        }

        long total = SQLExecutor.getTotal(simpleSearchQuerySql);
        PageDataDto pageData = getSimpleSearchRecordByPage(dto, total, simpleSearchQuerySql);

        Map<String,Object> resMap = new HashMap<>();
        resMap.put("columnList", misColumnList);
        resMap.put("recordList", pageData.getData());
        resMap.put("total", pageData.getTotal());
        resMap.put("tableId", type.getMisTypeId());
        return ResultGenerator.getSuccessResult(resMap);
    }

    @Override
    public Map<String, Object> simpleSearchExport(SimpleSearchRecordInputDto dto) {
        Map<String, Object> inputName2value = new HashMap<>();
        Map<String, Object> map = JSON.parseObject(dto.getData());
        Set<String> keys = map.keySet();
        for (String key : keys) {
            System.out.println("value======" + map.get(key));
            Map<String, Object> m = JSON.parseObject(map.get(key).toString());
            if (!"0".equals(m.get("input_type").toString())) {
                inputName2value.put(key, m.get("value"));
            }
        }
        MisSimpleSearch simpleSearch = simpleSearchDao.getMisSimpleSearchByMisSimpleSearchId(dto.getSimpleSearchId());
        String simpleSearchQuerySql = simpleSearch.getMisSimpleSearchSql();
        //给simpleSearchSql自动添加Id字段，并且返回主表的名称
        StringBuilder sqlBuilder = new StringBuilder(simpleSearchQuerySql);
        String tableAlias = SQLExecutor.getTableAlias(simpleSearchQuerySql);
        System.out.println(tableAlias + ".id");
        Pattern pattern = Pattern.compile(tableAlias + ".id");
        Matcher matcher = pattern.matcher(simpleSearchQuerySql);
        if (!matcher.find()) {
            sqlBuilder.insert(simpleSearchQuerySql.toLowerCase().indexOf("select ") + 7, tableAlias + ".id, ");
        }
        String tableName = SQLExecutor.getTableName(sqlBuilder.toString());
        MisType type = typeDao.getMisTypeByMisTypeName(tableName.replace("_s", ""));

        simpleSearchQuerySql = StrUtil.format(sqlBuilder.toString(), "$value(", ")", inputName2value);

        List<MisColumn> misColumnList = new ArrayList<>();

        List<String> columns = SQLExecutor.getColumns(simpleSearchQuerySql);
        for (String s : columns) {
            MisColumn misColumn = new MisColumn();
            misColumn.setMisColumnId(s);
            misColumn.setMisColumnName(s);
            if (s.equals("id")) {
                misColumn.setMisColumnLabel("Id");
            } else {
                misColumn.setMisColumnLabel(s);
            }
            misColumn.setMisColumnType("1");
            misColumnList.add(misColumn);
        }

        long total = SQLExecutor.getTotal(simpleSearchQuerySql);
        PageDataDto pageData = getSimpleSearchRecordByPage(dto, total, simpleSearchQuerySql);

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("columnList", misColumnList);
        resMap.put("recordList", pageData.getData());
        return resMap;
    }

    private PageDataDto getSimpleSearchRecordByPage(SimpleSearchRecordInputDto dto, long total, String sql){
        int page = dto.getPageState().getPage();
        int pageSize = dto.getPageState().getPageSize();
        String limit = String.format(" limit %d, %d",PageRequest.of(page, pageSize).getOffset(),pageSize);
        StringBuilder orderByStr = new StringBuilder();
        if(null != dto.getSortModel() && dto.getSortModel().size() > 0){
            orderByStr.append(" order by ");
            for(int i = 0,len = dto.getSortModel().size();i<len;i++){
                SortModelDto sort = dto.getSortModel().get(i);
                orderByStr.append(sort.getField() + " " + sort.getSort() + ",");
            }
            orderByStr.deleteCharAt(orderByStr.length() - 1);
        }
        StringBuilder querySQL = new StringBuilder(sql + orderByStr + limit);

        Query query = entityManager.createNativeQuery(querySQL.toString());
        List list = query.getResultList();
        PageDataDto pageDataDto = new PageDataDto();
        pageDataDto.setTotal(total);
        pageDataDto.setData(list);
        return pageDataDto;
    }

    @Override
    public List<DicDto> getSimpleSearchDic(){
        return simpleSearchDao.getSimpleSearchDic();
    }

}
