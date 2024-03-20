package com.asl.prd004.dao;

import com.asl.prd004.dto.DicDto;
import com.asl.prd004.dto.MySearchDto;
import com.asl.prd004.dto.TypeListDto;
import com.asl.prd004.dto.WorkspaceListPageDto;
import com.asl.prd004.entity.MisType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MisTypeDao extends JpaRepository<MisType, String> {

    @Query(value = "select new com.asl.prd004.dto.DicDto(m.misTypeId,m.misTypeLabel) " +
            "from MisType m   ")
    List<DicDto> getTypeDic();

    @Query(value = "select new com.asl.prd004.dto.DicDto(m.misTypeId,m.misTypeLabel) " +
            "from MisType m  where m.misTypeId in(select misPropertyTableId from MisPropertyConfig)  ")
    List<DicDto> getTableName();

    @Query(nativeQuery = true,value="select concat(mis_type_name,\"_s\")as mis_type_name from mis_type mt where mt.mis_type_id =?1")
    String getTableNameById(String tableId);


    @Query(nativeQuery = true,value="select concat(mis_type_label)as mis_type_label from mis_type mt where mt.mis_type_id =?1")
    String getTableLabelById(String tableId);


    @Query(value = "select new com.asl.prd004.dto.DicDto(m.misTypeId,m.misTypeLabel) " +
            "from MisType m ,MisQueryForm mq where m.misTypeId = mq.misQfTableId and mq.misQfId =?1")
    List<DicDto> getTypeDicById(String typeId);

/*    @Query(value = "select t from MisType t")
    Page<MisType> findAll(Pageable pageable);*/

    @Modifying
    @Query(nativeQuery = true,value="update mis_type set mis_type_label = :misTypeLabel where mis_type_id = :misTypeId")
    int updateTypeLabel(@Param("misTypeLabel") String misTypeLabel, @Param("misTypeId") String misTypeId);


    @Query(value = "select new com.asl.prd004.dto.TypeListDto(m.misTypeId, m.misTypeLabel, m.misTypeName) " +
            "from MisType m   " )
    Page<TypeListDto> findAllTypePageable(Pageable pageable);

    @Query(value = "select new com.asl.prd004.dto.TypeListDto(m.misTypeId, m.misTypeLabel, m.misTypeName) " +
            "from MisType m  where m.misTypeId =?1 " )
    TypeListDto findTypeByMisTypeId(String misTypeId);


    List<MisType> getMisTypesByMisTypeName(String name);

    @Query(value = "select new com.asl.prd004.dto.TypeListDto(m.misTypeId,m.misTypeLabel,misTypeName) " +
            "from MisType m   ")
    List<TypeListDto> getTypeAll();
    @Query(nativeQuery = true,value="select mis_type_id,mis_type_label,mis_type_name from mis_type mt where if(?1!='',mis_type_label =?1,1=1) and if(?2!='',mis_type_name =?2,1=1)")
    List<Object[]> searchRecord(String misTypeLabel, String misTypeName);
    @Query(nativeQuery = true,value="select mc.mis_column_id ,mc.mis_column_type ,mc.mis_column_name ,mc.mis_column_label from mis_column mc where mc.mis_type_id=?1")
    List<Object[]> getRecordType(String misTypeId);


    MisType getMisTypeByMisTypeName(String name);
}
