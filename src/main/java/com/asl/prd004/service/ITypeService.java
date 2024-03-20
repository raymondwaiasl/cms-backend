package com.asl.prd004.service;

import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisColumn;
import com.asl.prd004.entity.MisType;
import com.asl.prd004.vo.CalcColumnQueryResultVO;
import com.asl.prd004.vo.TypeVO;

import java.util.List;
import java.util.Map;


public interface ITypeService {

    List<DicDto> getTableNames();

    public TypeVO selectTypeById(String id);

    public TypeIdDto addNewType(MisType misType);

    public boolean updateType(MisType misType);

    public boolean deleteType(TypeIdDto dto);

    public boolean addNewColumn(MisColumn misColumn);

    public boolean updateColumn(MisColumn MisColumn);

    public boolean deleteColumn(TypeIdDto dto);

    PageDataDto getAllTypes(PageableDto pageable);

    List<DicDto> getDicList();

    List<TypeListDto> getAllTypes();

    List<MisColumn> queryColumnByTypeId(String id);

    TypeIdDto addNewTypeAndDraf(TypeAndDrafDto dto);
}
