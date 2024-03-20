package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.dao.MisSavedSearchDao;
import com.asl.prd004.dto.MySearchDto;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.service.IMySearchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class MySearchServiceImpl implements IMySearchService {

    @Autowired
    MisSavedSearchDao searchDao;

    @Override
    public PageDataDto getMySearchListPageable(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "misSavedSearchId":
                    sortField = "m.misSavedSearchId";
                    break;
                case "misSavedSearchName":
                    sortField = "m.misSavedSearchName";
                    break;
                case "tableName":
                    sortField = "t.misTypeName";
                    break;
                case "misSavedSearchDate":
                    sortField = "m.misSavedSearchDate";
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
        String userId = ContextHolder.getUserId();
        Page<MySearchDto> mySearchDtoPage = searchDao.getMySearchListPageable(userId, page);
        List<MySearchDto> mySearchList = mySearchDtoPage.getContent();
        PageDataDto pageData = new PageDataDto();
        pageData.setData(mySearchList);
        pageData.setTotal(mySearchDtoPage.getTotalElements());
        return pageData;
    }
}
