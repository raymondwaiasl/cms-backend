package com.asl.prd004.dao;

import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.dto.WidgetColDto;
import com.asl.prd004.entity.MisWidgetColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WidgetColumnDao extends JpaRepository<MisWidgetColumn, String> {

    @Query(value = "select new com.asl.prd004.dto.WidgetColDto(wc.misColumnId, '')" +
            " FROM MisWidgetColumn wc where wc.misWidgetId = :widgetId")
    List<WidgetColDto> getWidgetColListByWidgetId(String widgetId);

    @Query(value = "select new com.asl.prd004.dto.TypeIdDto(m.misColumnId) " +
            "from MisWidgetColumn m where m.misWidgetId = :widgetId")
    List<TypeIdDto> getDefaultColumnsByWidgetId(String widgetId);

    void deleteByMisWidgetId(String widgetId);

}
