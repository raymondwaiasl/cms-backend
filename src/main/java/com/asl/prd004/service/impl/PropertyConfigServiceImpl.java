package com.asl.prd004.service.impl;

import com.asl.prd004.config.ContextHolder;
import com.asl.prd004.config.DefinitionException;
import com.asl.prd004.dao.MisPropertyConfigDao;
import com.asl.prd004.dao.MisPropertyConfigDetailDao;
import com.asl.prd004.dao.MisPropertySectionDao;
import com.asl.prd004.dto.*;
import com.asl.prd004.entity.MisPropertyConfig;
import com.asl.prd004.entity.MisPropertyConfigDetail;
import com.asl.prd004.entity.MisPropertySection;
import com.asl.prd004.service.IPropertyColumnConfService;
import com.asl.prd004.service.IPropertyConfigService;
import com.asl.prd004.utils.SerialNumberUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class PropertyConfigServiceImpl implements IPropertyConfigService {

    @Autowired
    MisPropertyConfigDao propertyConfigDao;

    @Autowired
    MisPropertyConfigDetailDao propertyConfigDetailDao;

    @Autowired
    IPropertyColumnConfService iPropertyColumnConfService;

    @Autowired
    MisPropertySectionDao misPropertySectionDao;

    @Autowired
    private MisPropertyConfigDetailDao misPropertyConfigDetailDao;


    @Override
    public PageDataDto getAllPropertyConfigs(PageableDto pageable) {
        Pageable page;
        if (StringUtils.isNotEmpty(pageable.getSortModel().getField())) {
            String sortField = pageable.getSortModel().getField();
            switch (sortField) {
                case "misPropertyId":
                    sortField = "misPropertyId";
                    break;
                case "misPropertyName":
                    sortField = "misPropertyName";
                    break;
                case "tableLabel":
                    sortField = "t.misTypeLabel";
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
        Page<PropertyListDto> propertyListDtos = propertyConfigDao.getPropertyPageable(page);
        List<PropertyListDto> propertyListDtoList = propertyListDtos.getContent();
        PageDataDto pageData = new PageDataDto();
        pageData.setData(propertyListDtoList);
        pageData.setTotal(propertyListDtos.getTotalElements());
        return pageData;
    }

    @Override
    public boolean addPropertyPage(PropertyPageDto dto) {

        MisPropertyConfig temp = dto.getPropertyConfig();
        String misPropertyTableId = temp.getMisPropertyTableId();
        List<MisPropertyConfig> propertyConfigByTableId = propertyConfigDao.getMisPropertyConfigsByMisPropertyTableId(misPropertyTableId);
        if(propertyConfigByTableId.size()>0){
            throw new DefinitionException(300, "The table has exited property.");
        }
        String seq = SerialNumberUtils.getTableSequence(  "mis_property_config");
        temp.setMisPropertyId(seq);
        temp.setCreationDate(new Timestamp(System.currentTimeMillis()));
        temp.setCreatorUserId(ContextHolder.getUserId());
        temp.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        temp.setUpdatedUserId(ContextHolder.getUserId());
        temp.setMisPropertyTableId(misPropertyTableId);
        temp.setMisIsLock(temp.getMisLockedLevel()==null?"N":"Y");
        temp.setMisLockedBy(ContextHolder.getUserId());
        MisPropertyConfig propertyConfig = propertyConfigDao.saveAndFlush(temp);
        boolean isSuccess = addPropertyConfigDetail(dto.getPropertyConfigDetails(),propertyConfig);
        return isSuccess;

    }

    public boolean addPropertyConfigDetail(List<propertyConfigDetailsDto> details, MisPropertyConfig propertyConfig){
        if(null != propertyConfig){
            if(details.size() > 0){
                for(int i=0;i<details.size();i++) {
                    MisPropertySection misPropertySection=new MisPropertySection();
                    misPropertySection.setMisPropertySectionId(SerialNumberUtils.getTableSequence("mis_property_section"));
                    misPropertySection.setMisPropertyId(propertyConfig.getMisPropertyId());
                    misPropertySection.setMisSectionLabel(details.get(i).getName());
                    misPropertySection.setMisIsLock(propertyConfig.getMisIsLock());
                    misPropertySection.setMisLockedBy(propertyConfig.getMisLockedBy());
                    misPropertySection.setMisSectionVisible("Y".equals(propertyConfig.getMisIsLock())?"Y":"N");
                    MisPropertySection saveSection=misPropertySectionDao.saveAndFlush(misPropertySection);
                    List<MisPropertyConfigDetail> columns = details.get(i).getColumns();
                    for (MisPropertyConfigDetail detail : columns) {
                        MisPropertyConfigDetail temp;
                        detail.setMisPropertyId(propertyConfig.getMisPropertyId());
                        String seq = SerialNumberUtils.getTableSequence("mis_property_config_detail");
                        detail.setMisPropertyConfigDetailId(seq);
                        detail.setCreationDate(new Timestamp(System.currentTimeMillis()));
                        detail.setCreatorUserId(ContextHolder.getUserId());
                        detail.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                        detail.setUpdatedUserId(ContextHolder.getUserId());
                        detail.setMisPropertyConfigDetailColumnId(detail.getMisColumnId());
                        detail.setMisIsLock(propertyConfig.getMisIsLock());
                        detail.setMisLockedBy(propertyConfig.getMisLockedBy());
                        detail.setMisPropertySectionId(saveSection.getMisPropertySectionId());
                        temp = propertyConfigDetailDao.saveAndFlush(detail);
                        if (null == temp) {
                            return false;
                        }
                    }
                }
            }
        }else{
            return false;
        }

        return true;

    }

    @Override
    public List<SelectProPertyDetailDTO> selectPropertyById(String id) {

        PropertyColumConfigDto pageDto = new PropertyColumConfigDto();
        Optional<MisPropertyConfig> optional = propertyConfigDao.findById(id);
        String tableId="";
        if (optional.isPresent()){
            MisPropertyConfig propertyConfig = optional.get();
            tableId=propertyConfig.getMisPropertyTableId();
            if (propertyConfig != null){
                pageDto.setPropertyConfig(propertyConfig);
                List<MisPropertyConfigDetail> details = propertyConfigDetailDao.getMisPropertyConfigDetailsByMisPropertyId(propertyConfig.getMisPropertyId());

                List<PropertyColumnConfigDetailDto> propertyColumnConfigDetailDtos = new ArrayList<>();
                for(MisPropertyConfigDetail detail : details){
                    PropertyColumnConfigDetailDto propertyConfigDetailDto = new PropertyColumnConfigDetailDto();
                    propertyConfigDetailDto.setMisPropertyConfigDetailColumnId(detail.getMisPropertyConfigDetailColumnId());
                    propertyConfigDetailDto.setMisPropertyConfigDetailId(detail.getMisPropertyConfigDetailId());
                    propertyConfigDetailDto.setColSize(detail.getColSize());
                    propertyConfigDetailDto.setRowSize(detail.getRowSize());
                    propertyConfigDetailDto.setMisPropertyId(detail.getMisPropertyId());
                    propertyConfigDetailDto.setCreationDate(detail.getCreationDate());
                    propertyConfigDetailDto.setUpdatedDate(detail.getUpdatedDate());
                    propertyConfigDetailDto.setCreatorUserId(detail.getCreatorUserId());
                    propertyConfigDetailDto.setMisPropertySectionId(detail.getMisPropertySectionId());
                    propertyConfigDetailDto.setMisLockedBy(detail.getMisLockedBy());
                    propertyConfigDetailDto.setMisLockedBy(detail.getMisLockedBy());
                    PropertyColumnCondDto propertyColumnCondDto =
                            iPropertyColumnConfService.getPropertyColumnConfByMisPropertyConfigDetailId(detail.getMisPropertyConfigDetailId());

                    propertyConfigDetailDto.setColumnConfigDetail(propertyColumnCondDto);
                    propertyColumnConfigDetailDtos.add(propertyConfigDetailDto);
                }
                
                pageDto.setPropertyConfigDetails(propertyColumnConfigDetailDtos);
            }
        }
        List<SelectProPertyDetailDTO>  sectionColumnLs=new ArrayList<>();
        SelectProPertyDetailDTO sectionColumnDTO=null;
        List<PropertyColumnConfigDetailDto> columnInputDTOList=null;
        List<SectionLableColumnDTO> sectionLableColumnDTOList=misPropertyConfigDetailDao.getSectionLableColumnList(tableId);
        for(SectionLableColumnDTO sectionLableColumnDTO: sectionLableColumnDTOList){
            if(!sectionColumnLs.stream().anyMatch(p->p.getName().equals(sectionLableColumnDTO.getMisSectionLabel()))){
                sectionColumnDTO=new SelectProPertyDetailDTO();
                columnInputDTOList=new ArrayList<>();
                sectionColumnDTO.setName(sectionLableColumnDTO.getMisSectionLabel());
            }
            for(int i=0;i<pageDto.getPropertyConfigDetails().size();i++){
                if(sectionLableColumnDTO.getMisPropertyConfigDetailColumnId().equals(pageDto.getPropertyConfigDetails().get(i).getMisPropertyConfigDetailColumnId())){
                    columnInputDTOList.add(pageDto.getPropertyConfigDetails().get(i));
                    sectionColumnDTO.setPropertyConfigDetails(columnInputDTOList);
                }
            }
            sectionColumnLs.add(sectionColumnDTO);
        }
        List<SelectProPertyDetailDTO> sectionColumnDTOS=sectionColumnLs.stream().distinct().collect(Collectors.toList());
        return sectionColumnDTOS;
    }

    @Override
    public boolean editPropertyPage(PropertyPageDto dto) {
        MisPropertyConfig temp = dto.getPropertyConfig();
        MisPropertyConfig propertyConfig = propertyConfigDao.save(temp);
        boolean isSuccess = true;
                //editPropertyConfigDetail(dto.getPropertyConfigDetails(),propertyConfig);
        return isSuccess;

    }


    public boolean editPropertyConfigDetail(List<MisPropertyConfigDetail> details, MisPropertyConfig propertyConfig){
        if(null != propertyConfig){
            List<MisPropertyConfigDetail> propertyConfigDetails = propertyConfigDetailDao.getMisPropertyConfigDetailsByMisPropertyId(propertyConfig.getMisPropertyId());
            for(MisPropertyConfigDetail d:propertyConfigDetails){
                propertyConfigDetailDao.delete(d);
            }
            if(details.size() > 0){
                for(MisPropertyConfigDetail detail:details){
                    MisPropertyConfigDetail temp ;
                    detail.setMisPropertyId(propertyConfig.getMisPropertyId());
                    String seq = SerialNumberUtils.getTableSequence( "mis_property_config_detail");
                    detail.setMisPropertyConfigDetailId(seq);
                    detail.setCreationDate(new Timestamp(System.currentTimeMillis()));
                    detail.setCreatorUserId("1");
                    detail.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                    detail.setUpdatedUserId("1");
                    temp = propertyConfigDetailDao.saveAndFlush(detail);
                    if(null == temp){
                        return false;
                    }
                }
            }
        }else{
            return false;
        }

        return true;

    }

    @Override
    public boolean deleteProperty(TypeIdDto dto){
        Optional<MisPropertyConfig> propertyConfig = propertyConfigDao.findById(dto.getId());
        if (propertyConfig.isPresent()){
            MisPropertyConfig misPropertyConfig = propertyConfig.get();
            if (misPropertyConfig != null){
                propertyConfigDao.deleteById(dto.getId());
                propertyConfigDetailDao.deleteByMisPropertyId( dto.getId() );
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    @Override
    public PropertyConfigDto selectPropertyConfigDetailById(String id) {
        PropertyConfigDto propertyConfigDto=new PropertyConfigDto();
        List<PropertyConfigDetailDto> propertyConfigDetailDtoOne=new ArrayList<>();
        MisPropertyConfig misPropertyConfigList=propertyConfigDao.getMisPropertyConfigsByMisPropertyId(id);
        List<MisPropertySection> misPropertySectionList=misPropertySectionDao.findByMisPropertyId(id);
        propertyConfigDto.setPropertyConfig(misPropertyConfigList);
        if(misPropertySectionList.size()>0) {
            for (MisPropertySection p : misPropertySectionList) {
                PropertyConfigDetailDto propertyConfigDetailDto=new PropertyConfigDetailDto();
                String sectionName=p.getMisSectionLabel();
                propertyConfigDetailDto.setName(sectionName);
                propertyConfigDetailDto.setMisPropertyTableId(misPropertyConfigList.getMisPropertyTableId());
                List<MisPropertyConfigDetail> columns=propertyConfigDetailDao.getMisPropertyConfigDetailsByMisPropertyIdAndMisPropertySectionId(id,p.getMisPropertySectionId());
                propertyConfigDetailDto.setColumns(columns);
                propertyConfigDetailDtoOne.add(propertyConfigDetailDto);
            }
        }
        propertyConfigDto.setPropertyConfigDetails(propertyConfigDetailDtoOne);
        return propertyConfigDto;
    }
}
