package com.asl.prd004.service.impl;

import com.asl.prd004.dao.CmsStorageDao;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.dto.PageableDto;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.entity.CmsStorage;
import com.asl.prd004.service.IStorageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class StorageServiceImpl implements IStorageService {

    @Autowired
    CmsStorageDao storageDao;


    @Override
    public PageDataDto getAllStorage(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "misTypeId":
                    sortField = "misTypeId";
                    break;
                case "misTypeLabel":
                    sortField = "misTypeLabel";
                    break;
                case "misTypeName":
                    sortField = "misTypeName";
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
        Page<CmsStorage> storagPage = storageDao.findAll(page);
        List<CmsStorage> storags = storagPage.getContent();
        PageDataDto pageData = new PageDataDto();
        pageData.setData(storags);
        pageData.setTotal(storagPage.getTotalElements());
        return pageData;
    }

    @Override
    public boolean addNewStorage(CmsStorage storage) {
        CmsStorage cmsStorage = storageDao.saveAndFlush(storage);
        if (null != cmsStorage) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CmsStorage getStorageById(String id) {
        Optional<CmsStorage> optional = storageDao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public boolean deleteStorage(TypeIdDto dto) {
        storageDao.deleteById(dto.getId());
        return true;
    }
}
