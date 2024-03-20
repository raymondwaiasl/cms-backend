package com.asl.prd004.service;

import com.asl.prd004.dto.*;

import java.util.List;

public interface IPropertyConfigService {
    PageDataDto getAllPropertyConfigs(PageableDto pageable);
    public boolean addPropertyPage(PropertyPageDto dto);
    public boolean editPropertyPage(PropertyPageDto dto);
    public boolean deleteProperty(TypeIdDto dto);
    public List<SelectProPertyDetailDTO> selectPropertyById(String id);
    PropertyConfigDto selectPropertyConfigDetailById(String id);
}
