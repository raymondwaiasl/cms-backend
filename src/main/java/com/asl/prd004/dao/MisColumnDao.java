package com.asl.prd004.dao;

import com.asl.prd004.dto.ColumnDTO;
import com.asl.prd004.dto.ColumnNestDTO;
import com.asl.prd004.dto.DicDto;
import com.asl.prd004.entity.MisColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MisColumnDao extends JpaRepository<MisColumn, String>, JpaSpecificationExecutor<MisColumn> {
    List<MisColumn> findByMisTypeId(String misTypeId);
    void deleteByMisTypeId(String typeId);

    @Query(value = "select new com.asl.prd004.dto.DicDto(m.misColumnId,m.misColumnName) " +
            "from MisColumn m where  m.misColumnId in(select misPropertyConfigDetailColumnId from MisPropertyConfigDetail where misPropertyId in(select misPropertyId from MisPropertyConfig where misPropertyTableId = :typeId  ))")
    List<DicDto> getColumnDic(@Param("typeId") String typeId);

    @Query(value = "select new com.asl.prd004.dto.DicDto(m.misColumnId,m.misColumnLabel) " +
            "from MisColumn m where m.misTypeId = :typeId  ")
    List<DicDto> getColumnDicByTypeId(@Param("typeId") String typeId);

    @Modifying
    @Query(nativeQuery = true,value=":insertSql")
    int insertTableData(@Param("insertSql")String insertSql);

    @Query(nativeQuery = true,value = "select a.mcid as columnId,a.mcname as columnName,mq.mis_qfc_column_id as checked from " +
            "(select mc.mis_column_id mcid,mc.mis_column_label mcname from mis_column mc where mc.mis_type_id  =?1) a " +
            "left join mis_query_form_column mq on a.mcid = mq.mis_qfc_column_id and mq.mis_qf_id  =?2 ")
    List<Object[]> getCheckedColumn(String typeId,String qfId);
    @Query(value = "select new com.asl.prd004.dto.ColumnNestDTO(m.misColumnId,m.misColumnLabel,m.misColumnName,m" +
            ".misColumnInputType,m.misColumnDictionary, m.misColumnType, m.misColumnLength, m.misColumnAllowEmpty , 0, 0,mp.misPropertySectionId) " +
            " from MisColumn m,MisPropertyConfigDetail mp,MisPropertyConfig mc where m.misColumnId=mp.misPropertyConfigDetailColumnId and mp.misPropertyId=mc.misPropertyId and  mc.misPropertyTableId = :typeId  ORDER BY m.misColumnId ASC ")
    List<ColumnNestDTO> getColumnNestLs(@Param("typeId") String typeId);

    List<MisColumn> findByMisTypeIdAndMisColumnName(String misTypeId,String misColumnName);

    MisColumn findByMisColumnId(String misColumnId);

}
