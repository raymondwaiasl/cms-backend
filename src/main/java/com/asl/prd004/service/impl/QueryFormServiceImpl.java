package com.asl.prd004.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.MisCrossRefDao;
import com.asl.prd004.entity.MisCrossRef;
import com.asl.prd004.utils.SerialNumberUtils;
import com.asl.prd004.dao.MisQueryFormColumnDao;
import com.asl.prd004.dao.MisQueryFormConditionDao;
import com.asl.prd004.dao.MisQueryFormDao;
import com.asl.prd004.dao.MisSavedSearchDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisQueryForm;
import com.asl.prd004.entity.MisQueryFormColumn;
import com.asl.prd004.entity.MisQueryFormCondition;
import com.asl.prd004.entity.MisSavedSearch;
import com.asl.prd004.service.IQueryFormService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class QueryFormServiceImpl implements IQueryFormService {

    @Autowired
    MisQueryFormDao queryFormDao;

    @Autowired
    MisQueryFormColumnDao queryFormColumnDao;

    @Autowired
    MisQueryFormConditionDao queryFormConditionDao;

    @Autowired
    MisSavedSearchDao misSavedSearchDao;

    @Autowired
    MisCrossRefDao misCrossRefDao;

    @Override
    public PageDataDto getAllQueryForms(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "misQfId":
                    sortField = "misQfId";
                    break;
                case "misQfName":
                    sortField = "misQfName";
                    break;
                case "tableLabel":
                    sortField = "t.misTypeLabel";
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
        Page<QueryListDto> queryListDtoPage = queryFormDao.getQueryListPageable(page);
        List<QueryListDto> queryListDtos = queryListDtoPage.getContent();
        PageDataDto pageData = new PageDataDto();
        pageData.setData(queryListDtos);
        pageData.setTotal(queryListDtoPage.getTotalElements());
        return pageData;
    }

    @Override
    public boolean addQueryForm(MisQueryFormDTO dto) {
        saveSingle(dto);
        if(CollectionUtil.isNotEmpty(dto.getCrossRef())){
            dto.getCrossRef().forEach(r -> {
                r.setMisQfParentId(dto.getMisQfId());
                saveSingle(r);
            });
        }
        return true;
    }

    private boolean saveSingle(MisQueryFormDTO dto){
        MisQueryForm queryForm;
        MisQueryForm qf = new MisQueryForm();
        qf.setMisQfName(dto.getMisQfName());
        qf.setMisQfTableId(dto.getMisQfTableId());
        qf.setMisQfPublic("1");
        qf.setMisQfParentId(dto.getMisQfParentId());
        queryForm = queryFormDao.saveAndFlush(qf);
        dto.setMisQfId(queryForm.getMisQfId());
        List<MisQueryFormColumn> columnList = new ArrayList<>();
        for (MisQueryFormColumn c : dto.getQfColumns()) {
            c.setMisQfId(queryForm.getMisQfId());
            columnList.add(c);
        }
        queryForm.setQfColumns(columnList);
        List<MisQueryFormCondition> conditionList = new ArrayList<>();
        for (MisQueryFormCondition c : dto.getQfConditions()) {
            c.setMisQfId(queryForm.getMisQfId());
            conditionList.add(c);
        }
        queryForm.setQfConditions(conditionList);
        queryForm = queryFormDao.saveAndFlush(queryForm);
        if (null != queryForm) {
            return true;
        }
        throw new RuntimeException("save MisQueryForm fail!");
//        return false;
    }


    @Override
    public boolean editQueryForm(MisQueryFormDTO dto) {
        updateSingle(dto);
        Set<String> collect =
                queryFormDao.getQueryFormCrossRefInfoById(dto.getMisQfId()).stream().map(MisQueryFormDTO::getMisQfId).collect(Collectors.toSet());
        if(CollectionUtil.isNotEmpty(dto.getCrossRef())){
            dto.getCrossRef().forEach(cr -> {
                cr.setMisQfPublic("1");
                cr.setMisQfParentId(dto.getMisQfId());
                if(StrUtil.isNotBlank(cr.getMisQfId())){
                    updateSingle(cr);
                    collect.remove(cr.getMisQfId());
                }
                else {
                    saveSingle(cr);
                }
            });
        }
        if(CollectionUtil.isNotEmpty(collect)){
            collect.forEach(id -> deleteQueryForm(id));
        }
        return true;
    }

    @Override
    public TypeIdDto saveSearchForm(QueryFormDto dto) {
        MisQueryForm temp = new MisQueryForm();
        temp.setMisQfId(dto.getMisQfId());
        temp.setMisQfTableId(dto.getMisQfTableId());
        temp.setMisQfGroupId(dto.getMisQfGroupId());
        temp.setMisQfName(dto.getMisQfName());
        temp.setMisQfPublic(dto.getMisQfPublic());

        String seq = SerialNumberUtils.getTableSequence("mis_query_form_seq");
        temp.setMisQfId(seq);
        temp.setMisQfPublic("1");
        MisQueryForm queryForm = queryFormDao.saveAndFlush(temp);
        boolean isSucCol = addMisQfColumn(dto.getQfColumns(), queryForm);
        boolean isSucCon = addMisQfCondition(dto.getQfConditions(), queryForm);
        MisSavedSearch savedSearch = addMisSavedSearch(queryForm);

        TypeIdDto typeIdDto = new TypeIdDto();
        typeIdDto.setId(savedSearch.getMisSavedSearchId());

        return typeIdDto;
    }

    public MisSavedSearch addMisSavedSearch(MisQueryForm queryForm) {
        if (null != queryForm) {
            MisSavedSearch misSavedSearch = new MisSavedSearch();
            misSavedSearch.setMisQueryFormId(queryForm.getMisQfId());
            String seq = SerialNumberUtils.getTableSequence("mis_saved_search");
            misSavedSearch.setMisSavedSearchId(seq);
            misSavedSearch.setMisSavedSearchName(queryForm.getMisQfName());
            misSavedSearch.setMisSavedSearchUserId(ContextHolder.getUserId());
            misSavedSearch.setMisSavedSearchDate(new Timestamp(System.currentTimeMillis()));
            misSavedSearchDao.save(misSavedSearch);
            return misSavedSearch;
        } else {
            return null;
        }
    }

    public boolean addMisQfColumn(List<MisQueryFormColumn> columns, MisQueryForm queryForm) {
        if (null != queryForm) {
            if (columns.size() > 0) {
                for (MisQueryFormColumn column : columns) {
                    MisQueryFormColumn temp;
                    column.setMisQfId(queryForm.getMisQfId());
                    String seq = SerialNumberUtils.getTableSequence("mis_query_form_column_seq");
                    column.setMisQfcId(seq);
                    temp = queryFormColumnDao.saveAndFlush(column);
                    if (null == temp) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;

    }

    public boolean addMisQfCondition(List<MisQueryFormCondition> conditions, MisQueryForm queryForm) {
        if (null != queryForm) {
            if (conditions.size() > 0) {
                for (MisQueryFormCondition condition : conditions) {
                    MisQueryFormCondition temp;
                    condition.setMisQfId(queryForm.getMisQfId());
                    String seq = SerialNumberUtils.getTableSequence("mis_query_form_condition_seq");
                    condition.setMisQfc2Id(seq);
                    temp = queryFormConditionDao.saveAndFlush(condition);
                    if (null == temp) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean editQueryForm(QueryFormDto dto) {
        MisQueryForm temp = new MisQueryForm();
        temp.setMisQfId(dto.getMisQfId());
        temp.setMisQfTableId(dto.getMisQfTableId());
        temp.setMisQfGroupId(dto.getMisQfGroupId());
        temp.setMisQfName(dto.getMisQfName());
        temp.setMisQfPublic(dto.getMisQfPublic());
        MisQueryForm queryForm = queryFormDao.saveAndFlush(temp);
        boolean isSucCol = editQueryFormColumns(dto.getQfColumns(), queryForm);
        boolean isSucCon = editMisQfCondition(dto.getQfConditions(), queryForm);
        return isSucCol && isSucCon;

    }


    public boolean editQueryFormColumns(List<MisQueryFormColumn> columns, MisQueryForm queryForm) {
        if (null != queryForm) {
            List<MisQueryFormColumn> queryFormColumns = queryFormColumnDao.getMisQueryFormColumnByMisQfId(queryForm.getMisQfId());
            for (MisQueryFormColumn c : queryFormColumns) {
                queryFormColumnDao.delete(c);
            }
            if (columns.size() > 0) {
                for (MisQueryFormColumn column : columns) {
                    MisQueryFormColumn temp;
                    column.setMisQfId(queryForm.getMisQfId());
                    String seq = SerialNumberUtils.getTableSequence("mis_query_form_column_seq");
                    column.setMisQfcId(seq);
                    temp = queryFormColumnDao.saveAndFlush(column);
                    if (null == temp) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean editMisQfCondition(List<MisQueryFormCondition> conditions, MisQueryForm queryForm) {
        if (null != queryForm) {
            List<MisQueryFormCondition> queryFormConditions = queryFormConditionDao.getMisQueryFormConditionByMisQfId(queryForm.getMisQfId());
            for (MisQueryFormCondition c : queryFormConditions) {
                queryFormConditionDao.delete(c);
            }
            if (conditions.size() > 0) {
                for (MisQueryFormCondition column : conditions) {
                    MisQueryFormCondition temp;
                    column.setMisQfId(queryForm.getMisQfId());
                    String seq = SerialNumberUtils.getTableSequence("mis_query_form_column_seq");
                    column.setMisQfc2Id(seq);
                    temp = queryFormConditionDao.saveAndFlush(column);
                    if (null == temp) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }


    @Override
    public boolean deleteQueryForm(String typeId) {
        Optional<MisQueryForm> queryFormOptional = queryFormDao.findById(typeId);
        if (queryFormOptional.isPresent()) {
            MisQueryForm queryForm = queryFormOptional.get();
            if (queryForm != null) {
                queryFormDao.deleteByParentId(typeId);
                queryFormDao.deleteById(typeId);
                queryFormColumnDao.deleteByMisQfId(typeId);
                queryFormConditionDao.deleteByMisQfId(typeId);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public QueryFormDto selectQueryFormById(String id) {
        QueryFormDto queryFormDto = new QueryFormDto();
        Optional<MisQueryForm> optional = queryFormDao.findById(id);
        if (optional.isPresent()) {
            MisQueryForm queryForm = optional.get();
            if (queryForm != null) {
                queryFormDto.setMisQfId(queryForm.getMisQfId());
                queryFormDto.setMisQfTableId(queryForm.getMisQfTableId());
                queryFormDto.setMisQfGroupId(queryForm.getMisQfGroupId());
                queryFormDto.setMisQfName(queryForm.getMisQfName());
                queryFormDto.setMisQfPublic(queryForm.getMisQfPublic());
                List<MisQueryFormColumn> columns = queryFormColumnDao.getMisQueryFormColumnByMisQfId(queryForm.getMisQfId());
                List<MisQueryFormCondition> conditions = queryFormConditionDao.getMisQueryFormConditionByMisQfId(queryForm.getMisQfId());
                queryFormDto.setQfColumns(columns);
                queryFormDto.setQfConditions(conditions);
            }
        }

        return queryFormDto;
    }

    @Override
    public MisQueryFormDTO getQueryFormById(String id) {
        List<MisQueryFormDTO> optional = queryFormDao.getQueryFormInfoById(id);
        if (optional.size()>0) {
            MisQueryFormDTO queryFormDto = optional.get(0);
            List<MisQueryFormColumn> columns = queryFormColumnDao.getMisQueryFormColumnByMisQfId(queryFormDto.getMisQfId());
            List<MisQueryFormCondition> conditions = queryFormConditionDao.getMisQueryFormConditionByMisQfId(queryFormDto.getMisQfId());
            queryFormDto.setQfColumns(columns);
            queryFormDto.setQfConditions(conditions);

            List<MisQueryFormDTO> crossRef = queryFormDao.getQueryFormCrossRefInfoById(id);
            if(CollectionUtil.isNotEmpty(crossRef)){
                for(MisQueryFormDTO cf:crossRef){
                    List<MisQueryFormColumn> cfColumns = queryFormColumnDao.getMisQueryFormColumnByMisQfId(cf.getMisQfId());
                    List<MisQueryFormCondition> cfConditions = queryFormConditionDao.getMisQueryFormConditionByMisQfId(cf.getMisQfId());
                    cf.setQfColumns(cfColumns);
                    cf.setQfConditions(cfConditions);
                }
            }
            queryFormDto.setCrossRef(crossRef);
            return queryFormDto;
        }
        return null;
    }

    @Override
    public List<QueryListDto> getQueryList() {
        return queryFormDao.getQueryList();
    }

    private boolean updateSingle(MisQueryFormDTO dto){
        MisQueryForm queryForm = BeanUtil.copyProperties(dto,MisQueryForm.class);
        List<MisQueryFormColumn> columnList = new ArrayList<>();
        for (MisQueryFormColumn c : dto.getQfColumns()) {
            c.setMisQfId(dto.getMisQfId());
            columnList.add(c);
        }

        dto.getQfColumns().clear();
        dto.getQfColumns().addAll(columnList);


        List<MisQueryFormCondition> conditionList = new ArrayList<>();
        for (MisQueryFormCondition c : dto.getQfConditions()) {
            c.setMisQfId(dto.getMisQfId());
            conditionList.add(c);
        }
        dto.getQfConditions().clear();
        dto.getQfConditions().addAll(conditionList);
        queryForm = queryFormDao.saveAndFlush(queryForm);
        if (null != queryForm) {
            return true;
        }
        throw new RuntimeException("update MisQueryForm fail!");
    }

}
