package com.asl.prd004.dao;

import com.asl.prd004.dto.TypeRefDto;
import com.asl.prd004.dto.TypeRefPropertyDto;
import com.asl.prd004.entity.MisCrossRef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MisCrossRefDao extends JpaRepository<MisCrossRef, String> {

    @Query(value = "select new com.asl.prd004.dto.TypeRefDto(mcr.misCrossRefId, " +
            " mcr.misCrossRefName, pt.misTypeId, pt.misTypeLabel, pt.misTypeName, pc.misColumnId, pc.misColumnLabel, pc.misColumnName, " +
            " ct.misTypeId, ct.misTypeLabel,ct.misTypeName, cc.misColumnId, cc.misColumnLabel, cc.misColumnName )" +
            " from MisCrossRef mcr " +
            " left join MisType pt on pt.misTypeId = mcr.misCrossRefParentTable\n" +
            " left join MisColumn pc on pc.misColumnId = mcr.misCrossRefParentKey\n" +
            " left join MisType ct on ct.misTypeId = mcr.misCrossRefChildTable\n" +
            " left join MisColumn cc on cc.misColumnId = mcr.misCrossRefChildKey " +
            " where pt.misTypeId = :typeId or ct.misTypeId = :typeId "
    )
    Page<TypeRefDto> findAllTypeRefPageable(Pageable pageable, @Param("typeId") String tableId);


    @Query(value = "select new com.asl.prd004.dto.TypeRefPropertyDto( mcr.misCrossRefId, " +
            " pt.misTypeId, pt.misTypeLabel," +
            " ct.misTypeId, ct.misTypeLabel )" +
            " from MisCrossRef mcr " +
            " left join MisType pt on pt.misTypeId = mcr.misCrossRefParentTable\n" +
            " left join MisColumn pc on pc.misColumnId = mcr.misCrossRefParentKey\n" +
            " left join MisType ct on ct.misTypeId = mcr.misCrossRefChildTable\n" +
            " left join MisColumn cc on cc.misColumnId = mcr.misCrossRefChildKey " +
            " where pt.misTypeId = :typeId or ct.misTypeId = :typeId "
    )
    List<TypeRefPropertyDto> findAllTypeRef(@Param("typeId") String tableId);

    @Query(value = "select new com.asl.prd004.dto.TypeRefPropertyDto( mcr.misCrossRefId, " +
            " pt.misTypeId, pt.misTypeLabel," +
            " ct.misTypeId, ct.misTypeLabel )" +
            " from MisCrossRef mcr " +
            " left join MisType pt on pt.misTypeId = mcr.misCrossRefParentTable\n" +
            " left join MisColumn pc on pc.misColumnId = mcr.misCrossRefParentKey\n" +
            " left join MisType ct on ct.misTypeId = mcr.misCrossRefChildTable\n" +
            " left join MisColumn cc on cc.misColumnId = mcr.misCrossRefChildKey " +
            " where pt.misTypeId in (:typeIds) or ct.misTypeId in (:typeIds) "
    )
    List<TypeRefPropertyDto> findAllTypeRef(@Param("typeIds") List<String> typeIds);

    @Query(
        value =
            "select * \n" +
            "  from mis_cross_ref \n" +
            " where (mis_cross_ref_parent_table = :typeId and mis_cross_ref_child_table in (:typeIds))\n" +
            "    or (mis_cross_ref_child_table = :typeId and mis_cross_ref_parent_table in (:typeIds))\n",
        nativeQuery = true
    )
    List<MisCrossRef> findCrossRef(@Param("typeId") String targetTypeId,@Param("typeIds") List<String> sourceTypeIds);

    @Query(value = "select new com.asl.prd004.dto.TypeRefDto(mcr.misCrossRefId, " +
            " mcr.misCrossRefName, pt.misTypeId, pt.misTypeLabel, pt.misTypeName, pc.misColumnId, pc.misColumnLabel, pc.misColumnName, " +
            " ct.misTypeId, ct.misTypeLabel,ct.misTypeName, cc.misColumnId, cc.misColumnLabel, cc.misColumnName )" +
            " from MisCrossRef mcr " +
            " left join MisType pt on pt.misTypeId = mcr.misCrossRefParentTable\n" +
            " left join MisColumn pc on pc.misColumnId = mcr.misCrossRefParentKey\n" +
            " left join MisType ct on ct.misTypeId = mcr.misCrossRefChildTable\n" +
            " left join MisColumn cc on cc.misColumnId = mcr.misCrossRefChildKey " +
            " where mcr.misCrossRefId = :typeId "
    )
    TypeRefDto findMisCrossRefByMisCrossRefId(@Param("typeId") String typeId);





}
