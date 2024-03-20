package com.asl.prd004.service;

import com.asl.prd004.dto.PropertyColumnCondDto;

public interface IPropertyColumnConfService {
    
    PropertyColumnCondDto getPropertyColumnConfByColumnConfigId(String columnConfigId);

    Boolean addPropertyColumnConfByColumnConfigId(PropertyColumnCondDto propertyColumnCondDto) throws IllegalAccessException;

    Boolean editPropertyColumnConfByColumnConfigId(PropertyColumnCondDto propertyColumnCondDto) throws IllegalAccessException;

    PropertyColumnCondDto getPropertyColumnConfByMisPropertyConfigDetailId(String columnConfigId);

}
