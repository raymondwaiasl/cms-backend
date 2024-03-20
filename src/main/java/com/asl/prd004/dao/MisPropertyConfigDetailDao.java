package com.asl.prd004.dao;

import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.SectionLableColumnDTO;
import com.asl.prd004.dto.TypeIdDto;
import com.asl.prd004.entity.MisPropertyConfigDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MisPropertyConfigDetailDao  extends JpaRepository<MisPropertyConfigDetail,String> {

    List<MisPropertyConfigDetail> getMisPropertyConfigDetailsByMisPropertyId(String propertyId);

    @Query(value = "select new com.asl.prd004.dto.TypeIdDto(mc.misPropertyConfigDetailColumnId ) " +
            "from MisPropertyConfig m, MisType t, MisPropertyConfigDetail mc  where m.misPropertyId = mc.misPropertyId" +
            " and m.misPropertyTableId = t.misTypeId  and t.misTypeId= :typeId")
    List<TypeIdDto> getMisPropertyConfigDetailsByMisTypeId(@Param("typeId")String misTypeId);

    void deleteByMisPropertyId(String typeId);
    List<MisPropertyConfigDetail> getMisPropertyConfigDetailsByMisPropertyIdAndMisPropertySectionId(String propertyId,String misPropertySectionId);
    @Query(value = "select distinct new com.asl.prd004.dto.SectionLableColumnDTO(mps.misSectionLabel,mps.misPropertySectionId,mpcd.misPropertyConfigDetailColumnId) from MisPropertyConfigDetail mpcd ,MisPropertySection mps where mpcd.misPropertySectionId =mps.misPropertySectionId  and mpcd.misPropertyId =mps.misPropertyId  and mpcd.misPropertyId  in(select misPropertyId from MisPropertyConfig mpc where misPropertyTableId=:tableId)")
    List<SectionLableColumnDTO> getSectionLableColumnList(@Param("tableId")String tableId);
}
