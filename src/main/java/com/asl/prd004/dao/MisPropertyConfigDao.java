package com.asl.prd004.dao;

import com.asl.prd004.dto.PropertyListDto;
import com.asl.prd004.entity.MisPropertyConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MisPropertyConfigDao  extends JpaRepository<MisPropertyConfig,String> {

    @Query(value = "select new com.asl.prd004.dto.PropertyListDto(m.misPropertyId,m.misPropertyName,t.misTypeLabel) " +
            "from MisPropertyConfig m,MisType t where m.misPropertyTableId = t.misTypeId  ")
    List<PropertyListDto> getPropertyPage();

    @Query(value = "select new com.asl.prd004.dto.PropertyListDto(m.misPropertyId,m.misPropertyName,t.misTypeLabel) " +
            "from MisPropertyConfig m join MisType t on m.misPropertyTableId = t.misTypeId  ")
    Page<PropertyListDto> getPropertyPageable(Pageable pageable);
    /**
     * get MisPropertyConfigs
     *
     * @param tableId
     * @return List<MisPropertyConfig>
     */
    List<MisPropertyConfig> getMisPropertyConfigsByMisPropertyTableId(String tableId);

    MisPropertyConfig getMisPropertyConfigsByMisPropertyId(String id);
}
