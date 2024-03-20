package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;

public interface IMySearchService {
    public PageDataDto getMySearchListPageable(PageableDto pageable);
}
