package com.asl.prd004.service;

import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisQueryForm;

import java.util.List;

public interface IQueryFormService {
    PageDataDto getAllQueryForms(PageableDto pageable);
    public boolean addQueryForm(MisQueryFormDTO dto);
    boolean editQueryForm(MisQueryFormDTO dto);
    public boolean editQueryForm(QueryFormDto dto);
    public boolean deleteQueryForm(String typeId);
    public QueryFormDto selectQueryFormById(String id);
    TypeIdDto saveSearchForm(QueryFormDto dto);
    MisQueryFormDTO getQueryFormById(String id);
    List<QueryListDto> getQueryList();
}
