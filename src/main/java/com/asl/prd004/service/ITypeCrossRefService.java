package com.asl.prd004.service;

import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisCrossRef;

import java.util.List;

public interface ITypeCrossRefService {

    PageDataDto getAllTypeRef(TypeRefPageDto typeRefPageDto);

    Boolean addTypeRef(MisCrossRef misCrossRef);

    Boolean editTypeRef(MisCrossRef misCrossRef);

    Boolean deleteTypeRef(TypeIdDto typeIdDto);

    List getTypeRefByTypeId(String typeId);

}
