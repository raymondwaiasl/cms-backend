package com.asl.prd004.dao;
import com.asl.prd004.entity.BiToolConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description: TODO
 * @author: billy
 * @date: 2023/6/5 16:08
 * @version: 1.0
 */
public interface BiToolDao extends JpaRepository<BiToolConfig,String> {
    @Query(nativeQuery = true,value=" select mbc.mis_bi_config_id as misBiConfigId, mbc.mis_bi_config_name as " +
            " misBiConfigName, mbc.mis_bi_config_type_id misBiConfigTypeId, " +
            " IFNULL (mt.mis_type_name, mbc.mis_bi_config_type_id) as misBiConfigTypeName, "+
            " (case when mbc.mis_bi_config_graphic_type='1' then 'Bar Chart' " +
            " when mbc.mis_bi_config_graphic_type='2' then 'Pie Chart' when mbc.mis_bi_config_graphic_type='3' then " +
            " 'Line Chart' when mbc.mis_bi_config_graphic_type='4' then 'Bar Race' else 'Pie Race' end)\n " +
            " misBiConfigGraphicType,mbc.mis_bi_config_column_hor misBiConfigColumnHor,mbc.mis_bi_config_column_vet " +
            " misBiConfigColumnVet,mbc.mis_bi_config_date misBiConfigDate,(case when mbc.mis_bi_config_def_view='D' " +
            " then 'Data View' else 'Graphics View' end ) misBiConfigDefView from mis_bi_config mbc left join mis_type mt " +
            " on mbc.mis_bi_config_type_id=mt.mis_type_id")
    Page<Object[]> getAllBiTool(Pageable pageable);

    @Query(nativeQuery = true,value="select mbc.mis_bi_config_id as misBiConfigId, mbc.mis_bi_config_name as misBiConfigName ,mbc.mis_bi_config_type_id misBiConfigTypeId ,mt.mis_type_name as misBiConfigTypeName,(case when mbc.mis_bi_config_graphic_type='1' then 'Bar Chart'\n" +
            "when mbc.mis_bi_config_graphic_type='2' then 'Pie Chart' when mbc.mis_bi_config_graphic_type='3' then 'Line Chart' when mbc.mis_bi_config_graphic_type='4' then 'Bar Race' else 'Pie Race' end)\n" +
            "misBiConfigGraphicType,mbc.mis_bi_config_column_hor misBiConfigColumnHor,mbc.mis_bi_config_column_vet misBiConfigColumnVet,mbc.mis_bi_config_date misBiConfigDate,(case when mbc.mis_bi_config_def_view='D' then 'Data View' else 'Graphic View' end ) misBiConfigDefView from mis_bi_config mbc , mis_type mt where mbc.mis_bi_config_type_id=mt.mis_type_id and mbc.mis_bi_config_id=?1 ")
    List<Object[]> getBiTool(String misBiConfigId);


    BiToolConfig getBiToolConfigByMisBiConfigId(String misBiConfigId);

    List<BiToolConfig> findAll();


    @Query(nativeQuery = true,value="delete from mis_bi_config where mis_bi_config_type_id = ?1")
    @Modifying
    void deleteByMisBiConfigTypeId(String id);
}
