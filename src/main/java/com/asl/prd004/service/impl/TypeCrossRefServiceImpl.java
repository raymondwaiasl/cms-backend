package com.asl.prd004.service.impl;

import cn.hutool.core.util.StrUtil;
import com.asl.prd004.dao.MisCrossRefDao;
import com.asl.prd004.dao.SysConfigDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisCrossRef;
import com.asl.prd004.entity.MisUser;
import com.asl.prd004.service.ITypeCrossRefService;
import com.asl.prd004.utils.SerialNumberUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TypeCrossRefServiceImpl  implements ITypeCrossRefService {

    @Autowired
    private MisCrossRefDao misCrossRefDao;

    @Override
    public PageDataDto getAllTypeRef(TypeRefPageDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "misCrossRefId":
                    sortField = "misCrossRefId";
                    break;
                case "misCrossRefName":
                    sortField = "misCrossRefName";
                    break;
                case "misCrossRefParentTableName":
                    sortField = "misCrossRefParentTableName";
                    break;
                case "misCrossRefChildTableName":
                    sortField = "misCrossRefChildTableName";
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
        String tableId = pageable.getTableId();
        Page<TypeRefDto> typePage = misCrossRefDao.findAllTypeRefPageable(page, tableId);
        List<TypeRefDto> types = typePage.getContent();
        PageDataDto pageData = new PageDataDto();
        pageData.setData(types);
        pageData.setTotal(typePage.getTotalElements());
        return pageData;
    }

    @Override
    public Boolean addTypeRef(MisCrossRef misCrossRef) {
        misCrossRef.setMisCrossRefId(SerialNumberUtils.getTableSequence("mis_cross_ref"));
        MisCrossRef misCrossRef1 = misCrossRefDao.save(misCrossRef);
        return true;
    }

    @Override
    public Boolean editTypeRef(MisCrossRef misCrossRef) {
        MisCrossRef misCrossRef1 = misCrossRefDao.save(misCrossRef);
        return true;
    }

    @Override
    public Boolean deleteTypeRef(TypeIdDto typeIdDto) {
        misCrossRefDao.deleteById(typeIdDto.getId());
        return true;
    }

    @Override
    public  List getTypeRefByTypeId(String typeId){
        if(StrUtil.isBlank(typeId)){
            return Collections.emptyList();
        }
        List<TypeRefPropertyDto> list =  misCrossRefDao.findAllTypeRef(typeId);
        return list;
    }

}
