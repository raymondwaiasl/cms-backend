package com.asl.prd004.dao;

import com.asl.prd004.dto.WidgetDto;
import com.asl.prd004.dto.WorkspaceListPageDto;
import com.asl.prd004.entity.MisWidget;
import com.asl.prd004.entity.MisWorkspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WidgetDao extends JpaRepository<MisWidget, String> {

    @Query(value = "select new com.asl.prd004.dto.WidgetDto(wg.misWidgetId, wg.misWidgetName, wg.misBasicWidget, wg" +
            ".misWidgetConfig, wg.misWidgetType, wg.misDefaultTable, wg.misSimpleSearchId, wg.misDisplayHeader, wg.misHeaderTitle)" +
            " FROM MisWidget wg WHERE  wg.misWidgetId =?1")
    WidgetDto getMisWidgetById(String id);

    @Query(value = "select new com.asl.prd004.dto.WidgetDto(wg.misWidgetId, wg.misWidgetName, wg.misBasicWidget, wg" +
            ".misWidgetConfig, wg.misWidgetType, wg.misDefaultTable, wg.misSimpleSearchId, wg.misDisplayHeader, wg.misHeaderTitle)" +
            " FROM MisWidget wg ")
    List<WidgetDto> findAllWidget();

    @Query(value = "select new com.asl.prd004.dto.WidgetDto(wg.misWidgetId, wg.misWidgetName, wg.misBasicWidget, wg" +
            ".misWidgetConfig, wg.misWidgetType, wg.misDefaultTable, wg.misSimpleSearchId, wg.misDisplayHeader, wg.misHeaderTitle) FROM MisWidget wg ")
    Page<WidgetDto> findAllWidgetPageable(Pageable pageable);

}
