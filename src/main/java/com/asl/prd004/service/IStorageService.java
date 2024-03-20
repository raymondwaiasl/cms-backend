package com.asl.prd004.service;

import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.entity.CmsStorage;

public interface IStorageService {
    PageDataDto getAllStorage(PageableDto pageable);

    public boolean addNewStorage(CmsStorage storage);

    CmsStorage getStorageById(String id);

    boolean deleteStorage(TypeIdDto dto);


}
