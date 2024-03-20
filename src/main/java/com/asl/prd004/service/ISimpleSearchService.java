package com.asl.prd004.service;

import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.*;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ISimpleSearchService {

    PageDataDto getSimpleSearchListPageable(PageableDto pageable);

//    List<WidgetDto> getSimpleSearchList();

    SimpleSearchResponseDto getSimpleSearchById(TypeIdDto dto);

    boolean addSimpleSearch(SimpleSearchInputDto inputDto);

    boolean editSimpleSearch(SimpleSearchInputDto inputDto);

    boolean deleteSimpleSearch(TypeIdDto dto);

    ResultGenerator simpleSearchRecord(SimpleSearchRecordInputDto dto);

    List<DicDto> getSimpleSearchDic();

    Map<String, Object> simpleSearchExport(SimpleSearchRecordInputDto dto);

}
