package com.asl.prd004.service;

import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisWidget;

import java.util.List;

public interface IWidgetService {

    PageDataDto getWidgetListPageable(PageableDto pageable);

    List<WidgetDto> getWidgetList();

    WidgetInputDto getWidgetById(TypeIdDto dto);

    boolean addWidget(WidgetInputDto widgetDto);

    boolean editWidget(WidgetInputDto widgetDto);

    boolean deleteWidget(TypeIdDto dto);

}
