package com.asl.prd004.service.impl;

import com.asl.prd004.dao.MisColumnDao;
import com.asl.prd004.dao.WidgetColumnDao;
import com.asl.prd004.dao.WidgetDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisWidget;
import com.asl.prd004.entity.MisWidgetColumn;
import com.asl.prd004.entity.MisWorkspace;
import com.asl.prd004.service.IWidgetService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WidgetServiceImpl implements IWidgetService {

    @Autowired
    private WidgetDao widgetDao;

    @Autowired
    private WidgetColumnDao widgetColumnDao;

    @Autowired
    private MisColumnDao columnDao;

    @Override
    public PageDataDto getWidgetListPageable(PageableDto pageable){
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            if (pageable.getSortModel().getSort().equalsIgnoreCase("asc")) {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).ascending());
            } else {
                page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize(), Sort.by(sortField).descending());
            }
        } else {
            page = PageRequest.of(pageable.getPageState().getPage() - 1, pageable.getPageState().getPageSize());
        }
        Page<WidgetDto> widgetPage = widgetDao.findAllWidgetPageable(page);
        List<WidgetDto> widgetList = widgetPage.getContent();
        PageDataDto widgetPageDto = new PageDataDto();
        widgetPageDto.setData(widgetList);
        widgetPageDto.setTotal(widgetPage.getTotalElements());
        return widgetPageDto;

    }

    @Override
    public List<WidgetDto> getWidgetList(){
        List<WidgetDto> widgetDtoList = widgetDao.findAllWidget();
        return widgetDtoList;
    }

    @Override
    public WidgetInputDto getWidgetById(TypeIdDto dto){
        WidgetDto misWidget = widgetDao.getMisWidgetById(dto.getId());
        List<WidgetColDto> colDtos = widgetColumnDao.getWidgetColListByWidgetId(dto.getId());
        List<DicDto> columnList = columnDao.getColumnDicByTypeId(misWidget.getMisDefaultTable());
        List<WidgetColDto> includeList = new ArrayList<>();
        List<WidgetColDto> excludeList = new ArrayList<>();
        for(DicDto c:columnList){
            WidgetColDto widgetColDto = new WidgetColDto(c);
            if(isIdContained(colDtos,c.getKey())){
                includeList.add(widgetColDto);
            }else{
                excludeList.add(widgetColDto);
            }
        }
        WidgetColListDto widgetColListDto = new WidgetColListDto();
        widgetColListDto.setIncludeList(includeList);
        widgetColListDto.setExcludeList(excludeList);
        WidgetInputDto widgetInputDto = new WidgetInputDto(misWidget);
        widgetInputDto.setList(widgetColListDto);
        return  widgetInputDto;
    }

    private boolean isIdContained(List<WidgetColDto> list, String targetId) {
        for (WidgetColDto obj : list) {
            if (obj.getId().equals(targetId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addWidget(WidgetInputDto widgetDto){
        MisWidget misWidget = new MisWidget();
        misWidget.setMisBasicWidget(widgetDto.getMisBasicWidget());
        misWidget.setMisWidgetConfig(widgetDto.getMisWidgetConfig());
        misWidget.setMisWidgetName(widgetDto.getMisWidgetName());
        misWidget.setMisWidgetType(widgetDto.getMisWidgetType());
        misWidget.setMisDefaultTable(widgetDto.getMisDefaultTable());
        misWidget.setMisSimpleSearchId(widgetDto.getMisSimpleSearchId());
        widgetDao.saveAndFlush(misWidget);
        if(null != misWidget.getMisWidgetId()){
            if(null != widgetDto.getList().getIncludeList()){
                if(widgetDto.getList().getIncludeList().size() > 0){
                    for(WidgetColDto c:widgetDto.getList().getIncludeList()){
                        MisWidgetColumn col = new MisWidgetColumn();
                        col.setMisWidgetId(misWidget.getMisWidgetId());
                        col.setMisColumnId(c.getId());
                        widgetColumnDao.saveAndFlush(col);
                    }
                }
            }
        }
        return  true;
    }

    @Override
    public boolean editWidget(WidgetInputDto widgetDto){
        MisWidget misWidget = new MisWidget();
        misWidget.setMisWidgetId(widgetDto.getMisWidgetId());
        misWidget.setMisBasicWidget(widgetDto.getMisBasicWidget());
        misWidget.setMisWidgetConfig(widgetDto.getMisWidgetConfig());
        misWidget.setMisWidgetName(widgetDto.getMisWidgetName());
        misWidget.setMisWidgetType(widgetDto.getMisWidgetType());
        misWidget.setMisDefaultTable(widgetDto.getMisDefaultTable());
        misWidget.setMisSimpleSearchId(widgetDto.getMisSimpleSearchId());
        widgetDao.saveAndFlush(misWidget);
        if(null != misWidget.getMisWidgetId()){
            widgetColumnDao.deleteByMisWidgetId(misWidget.getMisWidgetId());
            if(null != widgetDto.getList().getIncludeList()){
                if(widgetDto.getList().getIncludeList().size() > 0){
                    for(WidgetColDto c:widgetDto.getList().getIncludeList()){
                        MisWidgetColumn col = new MisWidgetColumn();
                        col.setMisWidgetId(misWidget.getMisWidgetId());
                        col.setMisColumnId(c.getId());
                        widgetColumnDao.saveAndFlush(col);
                    }
                }
            }
        }
        return  true;
    }

    @Override
    public boolean deleteWidget(TypeIdDto dto){
        //check if widget exit in workspace
        widgetDao.deleteById(dto.getId());
        return true;
    }

}
